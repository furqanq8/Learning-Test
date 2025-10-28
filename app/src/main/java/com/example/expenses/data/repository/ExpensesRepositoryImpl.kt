package com.example.expenses.data.repository

import android.content.Context
import com.example.expenses.data.local.dao.BudgetDao
import com.example.expenses.data.local.dao.CategoryDao
import com.example.expenses.data.local.dao.RecurringDao
import com.example.expenses.data.local.dao.TransactionDao
import com.example.expenses.data.local.entities.BudgetEntity
import com.example.expenses.data.local.entities.CategoryEntity
import com.example.expenses.data.local.entities.RecurringTransactionEntity
import com.example.expenses.data.local.entities.TransactionEntity
import com.example.expenses.data.local.entities.TransactionWithCategory
import com.example.expenses.data.preferences.UserPreferences
import com.example.expenses.domain.model.Budget
import com.example.expenses.domain.model.Category
import com.example.expenses.domain.model.CategoryType
import com.example.expenses.domain.model.DashboardSummary
import com.example.expenses.domain.model.RecurringTransaction
import com.example.expenses.domain.model.Transaction
import com.example.expenses.domain.model.TransactionType
import com.example.expenses.domain.usecase.ExpensesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.io.File
import java.io.FileOutputStream

class ExpensesRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val recurringDao: RecurringDao,
    private val budgetDao: BudgetDao,
    private val context: Context
) : ExpensesRepository {

    private val preferences = UserPreferences(context)

    init {
        seedCategories()
    }

    override fun observeTransactions(): Flow<List<Transaction>> =
        transactionDao.observeTransactions().map { list -> list.map { it.toDomain() } }

    override fun observeTransactionsInRange(start: LocalDate, end: LocalDate): Flow<List<Transaction>> =
        transactionDao.observeTransactionsInRange(start, end).map { list -> list.map { it.toDomain() } }

    override suspend fun getTransaction(id: Long): Transaction? =
        transactionDao.getTransaction(id)?.toDomain()

    override suspend fun addTransaction(transaction: Transaction): Long =
        transactionDao.insert(TransactionEntity.fromDomain(transaction))

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(TransactionEntity.fromDomain(transaction))
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(TransactionEntity.fromDomain(transaction))
    }

    override fun searchTransactions(query: String): Flow<List<Transaction>> =
        transactionDao.searchTransactions(query).map { list -> list.map { it.toDomain() } }

    override fun observeCategories(type: TransactionType): Flow<List<Category>> =
        categoryDao.observeByType(CategoryType.valueOf(type.name)).map { it.map(CategoryEntity::toDomain) }

    override suspend fun upsertCategory(category: Category): Long {
        val id = categoryDao.insert(CategoryEntity.fromDomain(category))
        return if (id == 0L) category.id else id
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.delete(CategoryEntity.fromDomain(category))
    }

    override fun observeRecurring(): Flow<List<RecurringTransaction>> =
        recurringDao.observeActive().map { list -> list.map { it.toDomain() } }

    override suspend fun upsertRecurring(recurring: RecurringTransaction): Long {
        val id = recurringDao.insert(RecurringTransactionEntity.fromDomain(recurring))
        return if (id == 0L) recurring.id else id
    }

    override suspend fun deleteRecurring(recurring: RecurringTransaction) {
        recurringDao.delete(RecurringTransactionEntity.fromDomain(recurring))
    }

    override suspend fun getBudget(categoryId: Long, month: Int, year: Int): Budget? =
        budgetDao.getBudget(categoryId, month, year)?.toDomain()

    override fun observeBudgets(month: Int, year: Int): Flow<List<Budget>> =
        budgetDao.observeForMonth(month, year).map { list -> list.map { it.toDomain() } }

    override suspend fun upsertBudget(budget: Budget): Long {
        val id = budgetDao.insert(BudgetEntity.fromDomain(budget))
        return if (id == 0L) budget.id else id
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.delete(BudgetEntity.fromDomain(budget))
    }

    override suspend fun getDashboardSummary(start: LocalDate, end: LocalDate): DashboardSummary {
        val transactions = transactionDao.observeTransactionsInRange(start, end).first()
        val domainTransactions = transactions.map { it.toDomain() }
        val balance = domainTransactions.sumOf { tx ->
            if (tx.type == TransactionType.INCOME) tx.amountMinor else -tx.amountMinor
        }
        val groupedByMonth = domainTransactions.groupBy { it.date.month to it.date.year }
        val summaries = groupedByMonth.map { (key, monthTransactions) ->
            val month = key.first
            val year = key.second
            val totalIncome = monthTransactions.filter { it.type == TransactionType.INCOME }
                .sumOf { it.amountMinor }
            val totalExpense = monthTransactions.filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amountMinor }
            val categoryTotals = monthTransactions.groupBy { it.category.id }.values.map { list ->
                val category = list.first().category
                com.example.expenses.domain.model.CategoryTotal(
                    category = category,
                    amountMinor = list.sumOf { it.amountMinor }
                )
            }
            val incomeTypeTotals = monthTransactions.filter { it.type == TransactionType.INCOME }
                .groupBy { it.category.id }
                .values.map { list ->
                    val category = list.first().category
                    com.example.expenses.domain.model.IncomeTypeTotal(
                        category = category,
                        amountMinor = list.sumOf { it.amountMinor }
                    )
                }
            com.example.expenses.domain.model.MonthlySummary(
                month = month,
                year = year,
                totalIncomeMinor = totalIncome,
                totalExpenseMinor = totalExpense,
                categoryTotals = categoryTotals,
                incomeTypeTotals = incomeTypeTotals
            )
        }
        return DashboardSummary(
            balanceMinor = balance,
            recentTransactions = domainTransactions.sortedByDescending { it.date }.take(10),
            monthlySummaries = summaries.sortedByDescending { it.year * 100 + it.month.ordinal }
        )
    }

    override suspend fun getDefaultCurrency(): String = preferences.currencyFlow.first()

    override suspend fun setDefaultCurrency(currency: String) {
        preferences.setCurrency(currency)
    }

    override suspend fun isPinRequired(): Boolean = preferences.isPinRequired()

    override suspend fun setPinCode(pin: String) {
        preferences.setPin(pin)
    }

    override suspend fun validatePin(pin: String): Boolean = preferences.validatePin(pin)

    override suspend fun backupDatabase(uri: String) {
        withContext(Dispatchers.IO) {
            val dbFile = context.getDatabasePath("expenses.db")
            val destination = File(uri)
            dbFile.copyTo(destination, overwrite = true)
        }
    }

    override suspend fun restoreDatabase(uri: String) {
        withContext(Dispatchers.IO) {
            val dbFile = context.getDatabasePath("expenses.db")
            val source = File(uri)
            source.copyTo(dbFile, overwrite = true)
        }
    }

    override suspend fun exportMonthlySummary(year: Int, month: Int, formats: Set<String>, uri: String) {
        withContext(Dispatchers.IO) {
            val start = LocalDate(year, month, 1)
            val end = start.plusMonths(1).minusDays(1)
            val transactions = transactionDao.observeTransactionsInRange(start, end).first().map { it.toDomain() }
            if (transactions.isEmpty()) return@withContext
            val fileBase = File(uri)
            if (!fileBase.exists()) fileBase.mkdirs()
            if (formats.contains("csv")) {
                val csvFile = File(fileBase, "summary_${year}_${month}.csv")
                csvFile.printWriter().use { writer ->
                    writer.println("Date,Type,Category,Amount,Currency,Notes")
                    transactions.forEach { tx ->
                        writer.println("${tx.date},${tx.type},${tx.category.name},${tx.amountMinor / 100.0},${tx.currency},${tx.notes ?: ""}")
                    }
                }
            }
            if (formats.contains("xlsx")) {
                val workbook = org.apache.poi.xssf.usermodel.XSSFWorkbook()
                val sheet = workbook.createSheet("Summary")
                val header = sheet.createRow(0)
                val headers = listOf("Date", "Type", "Category", "Amount", "Currency", "Notes")
                headers.forEachIndexed { index, title ->
                    header.createCell(index).setCellValue(title)
                }
                transactions.forEachIndexed { index, tx ->
                    val row = sheet.createRow(index + 1)
                    row.createCell(0).setCellValue(tx.date.toString())
                    row.createCell(1).setCellValue(tx.type.name)
                    row.createCell(2).setCellValue(tx.category.name)
                    row.createCell(3).setCellValue(tx.amountMinor / 100.0)
                    row.createCell(4).setCellValue(tx.currency)
                    row.createCell(5).setCellValue(tx.notes ?: "")
                }
                val xlsxFile = File(fileBase, "summary_${year}_${month}.xlsx")
                FileOutputStream(xlsxFile).use { output ->
                    workbook.write(output)
                }
                workbook.close()
            }
            if (formats.contains("pdf")) {
                val pdfFile = File(fileBase, "summary_${year}_${month}.pdf")
                val writer = com.itextpdf.kernel.pdf.PdfWriter(pdfFile)
                val pdf = com.itextpdf.kernel.pdf.PdfDocument(writer)
                val document = com.itextpdf.layout.Document(pdf)
                val table = com.itextpdf.layout.element.Table(floatArrayOf(2f, 1f, 2f, 1f, 1f, 3f))
                listOf("Date", "Type", "Category", "Amount", "Currency", "Notes").forEach {
                    table.addHeaderCell(it)
                }
                transactions.forEach { tx ->
                    table.addCell(tx.date.toString())
                    table.addCell(tx.type.name)
                    table.addCell(tx.category.name)
                    table.addCell(String.format("%.2f", tx.amountMinor / 100.0))
                    table.addCell(tx.currency)
                    table.addCell(tx.notes ?: "")
                }
                document.add(table)
                document.close()
            }
        }
    }

    private fun TransactionWithCategory.toDomain(): Transaction =
        transaction.toDomain(category)

    private suspend fun seedCategories() {
        val existing = categoryDao.getAll()
        if (existing.isNotEmpty()) return
        val incomeCategories = listOf(
            "Salary",
            "Allowance",
            "Overtime",
            "Commission",
            "Gift",
            "Profit"
        )
        val expenseCategories = listOf(
            "Food",
            "Shopping",
            "Rent",
            "Mobile",
            "Transport",
            "Utilities",
            "Education",
            "Health",
            "Entertainment",
            "Charity",
            "Misc"
        )
        incomeCategories.forEach {
            categoryDao.insert(
                CategoryEntity(name = it, type = CategoryType.INCOME, isCustom = false)
            )
        }
        expenseCategories.forEach {
            categoryDao.insert(
                CategoryEntity(name = it, type = CategoryType.EXPENSE, isCustom = false)
            )
        }
    }
}

private fun LocalDate.plusMonths(months: Int): LocalDate {
    val javaDate = this.toJavaLocalDate().plusMonths(months.toLong())
    return javaDate.toKotlinLocalDate()
}

private fun LocalDate.minusDays(days: Int): LocalDate {
    val javaDate = this.toJavaLocalDate().minusDays(days.toLong())
    return javaDate.toKotlinLocalDate()
}

package com.example.expenses.domain.usecase

class ManageUserPreferencesUseCase(private val repository: ExpensesRepository) {
    suspend fun getCurrency(): String = repository.getDefaultCurrency()

    suspend fun setCurrency(currency: String) = repository.setDefaultCurrency(currency)

    suspend fun isPinRequired(): Boolean = repository.isPinRequired()

    suspend fun setPin(pin: String) = repository.setPinCode(pin)

    suspend fun validatePin(pin: String): Boolean = repository.validatePin(pin)

    suspend fun backup(uri: String) = repository.backupDatabase(uri)

    suspend fun restore(uri: String) = repository.restoreDatabase(uri)
}

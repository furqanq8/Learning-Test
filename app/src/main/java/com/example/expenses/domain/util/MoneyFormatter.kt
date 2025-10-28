package com.example.expenses.domain.util

import java.text.NumberFormat
import java.util.Locale

object MoneyFormatter {
    fun format(amountMinor: Long, currency: String): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = java.util.Currency.getInstance(currency)
        return format.format(amountMinor / 100.0)
    }
}

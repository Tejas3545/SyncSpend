package com.spendsync.app.util

import java.util.Locale

object CurrencyUtils {
    
    fun formatCurrency(amount: Double): String {
        return "₹${String.format(Locale.getDefault(), "%,.2f", amount)}"
    }
    
    fun formatAmount(amount: Double): String {
        return "₹${String.format(Locale.getDefault(), "%,.2f", amount)}"
    }
}

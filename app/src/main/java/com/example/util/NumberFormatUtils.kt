package com.example.util

import java.util.Locale

/**
 * Utility functions for clean number formatting in sports UI.
 * Prevents floating point errors like "2.4300000000000006" and formats as "2.43".
 */
fun Double.formatXg(fallback: Double = 1.38): String {
  val safeValue = if (this > 0.05) this else fallback
  return String.format(Locale.US, "%.2f", safeValue)
}

fun Double.formatOdd(): String {
  return String.format(Locale.US, "%.2f", this)
}

fun Long.formatTp(): String {
  return java.text.NumberFormat.getIntegerInstance(Locale.forLanguageTag("tr-TR")).format(this)
}

fun Double.formatTp(): String {
  return java.text.NumberFormat.getNumberInstance(Locale.forLanguageTag("tr-TR")).apply {
    maximumFractionDigits = 0
  }.format(this)
}


package com.koreykaigle.budgetapp.util

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/** What we could guess from a scanned receipt or pay stub. Nothing here is ever
 *  saved on its own -- it only pre-fills the normal add/edit form so the person
 *  can double-check and correct it before tapping Save. */
data class ScanResult(
    val rawText: String,
    val suggestedName: String,
    val suggestedAmount: String
)

/**
 * On-device OCR via ML Kit's local text recognizer -- the image is processed on
 * the phone and never leaves it. Callers are responsible for capturing to a
 * private temp file and deleting it once [scan] returns (see
 * `ui/common/ReceiptScan.kt`).
 */
object ReceiptScanner {

    suspend fun scan(context: Context, imageUri: Uri): ScanResult {
        val text = recognizeText(context, imageUri)
        return parse(text)
    }

    private suspend fun recognizeText(context: Context, imageUri: Uri): String =
        suspendCancellableCoroutine { cont ->
            try {
                val image = InputImage.fromFilePath(context, imageUri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        if (cont.isActive) cont.resume(visionText.text)
                    }
                    .addOnFailureListener {
                        // Fail soft -- the user can always fill the form in by hand.
                        if (cont.isActive) cont.resume("")
                    }
            } catch (e: Exception) {
                if (cont.isActive) cont.resume("")
            }
        }

    fun parse(text: String): ScanResult {
        if (text.isBlank()) return ScanResult(rawText = "", suggestedName = "", suggestedAmount = "")

        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        val name = lines.firstOrNull { line -> line.any { it.isLetter() } }?.take(60) ?: ""

        return ScanResult(
            rawText = text,
            suggestedName = name,
            suggestedAmount = bestAmount(lines)
        )
    }

    private val moneyRegex = Regex("\\$?\\s?(\\d{1,3}(?:,\\d{3})*|\\d+)\\.(\\d{2})(?!\\d)")
    private val totalKeywords = listOf(
        "grand total", "amount due", "balance due", "total due",
        "net pay", "amount paid", "total", "due", "amount"
    )

    private fun cleanAmount(raw: String): String = raw.removePrefix("$").trim().replace(",", "")

    private fun bestAmount(lines: List<String>): String {
        // Prefer an amount on a line that reads like a total/balance/net-pay line.
        for (keyword in totalKeywords) {
            val hit = lines.firstOrNull { it.lowercase().contains(keyword) }
                ?.let { moneyRegex.find(it) }
            if (hit != null) return cleanAmount(hit.value)
        }
        // Otherwise, the largest dollar-looking number on the page is usually the total.
        val best = moneyRegex.findAll(lines.joinToString("\n"))
            .maxByOrNull { cleanAmount(it.value).toDoubleOrNull() ?: 0.0 }
        return best?.let { cleanAmount(it.value) } ?: ""
    }
}

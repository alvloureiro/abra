package com.abra.data.pdf

import android.content.Context
import androidx.core.net.toUri
import com.abra.domain.model.ListeningSegment
import com.abra.domain.model.PdfExtractionResult
import com.abra.domain.repository.PdfTextExtractor
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PdfBoxPdfTextExtractor(
    private val context: Context,
) : PdfTextExtractor {
    override suspend fun extract(
        ebookId: String,
        sourceUri: String,
    ): PdfExtractionResult =
        withContext(Dispatchers.IO) {
            runCatching {
                PDFBoxResourceLoader.init(context.applicationContext)
                val uri = sourceUri.toUri()
                val inputStream =
                    context.contentResolver.openInputStream(uri)
                        ?: return@withContext PdfExtractionResult.Failure(
                            "Unable to open PDF file.",
                        )

                inputStream.use { stream ->
                    PDDocument.load(stream).use { document ->
                        if (document.isEncrypted) {
                            return@withContext PdfExtractionResult.Unsupported(
                                reason = "Encrypted PDFs are not supported in the MVP.",
                                pageCount = document.numberOfPages,
                            )
                        }

                        val segments = extractSegments(ebookId, document)
                        if (segments.isEmpty()) {
                            PdfExtractionResult.Unsupported(
                                reason = NO_SELECTABLE_TEXT_REASON,
                                pageCount = document.numberOfPages,
                            )
                        } else {
                            PdfExtractionResult.Success(
                                segments = segments,
                                pageCount = document.numberOfPages,
                            )
                        }
                    }
                }
            }.getOrElse { error ->
                PdfExtractionResult.Failure(error.message ?: "PDF extraction failed.")
            }
        }

    private fun extractSegments(
        ebookId: String,
        document: PDDocument,
    ): List<ListeningSegment> {
        val stripper = PDFTextStripper()
        var segmentIndex = 0
        val segments = mutableListOf<ListeningSegment>()

        for (pageNumber in 1..document.numberOfPages) {
            stripper.startPage = pageNumber
            stripper.endPage = pageNumber
            val pageText = stripper.getText(document).cleanText()
            pageText.toListeningChunks().forEach { chunk ->
                segments +=
                    ListeningSegment(
                        ebookId = ebookId,
                        index = segmentIndex,
                        pageNumber = pageNumber,
                        text = chunk,
                    )
                segmentIndex += 1
            }
        }

        return segments
    }

    private fun String.cleanText(): String =
        replace(Regex("[\\t ]+"), " ")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()

    private fun String.toListeningChunks(): List<String> {
        if (isBlank()) return emptyList()
        return split(Regex("\\n\\s*\\n"))
            .flatMap { paragraph ->
                paragraph
                    .trim()
                    .split(Regex("(?<=[.!?])\\s+(?=[A-Z0-9])"))
            }.map { it.trim() }
            .filter { it.length >= MIN_SEGMENT_LENGTH }
    }

    private companion object {
        const val MIN_SEGMENT_LENGTH = 12
        const val NO_SELECTABLE_TEXT_REASON =
            "No selectable text was found. Scanned PDFs require OCR, " +
                "which is out of scope for the MVP."
    }
}

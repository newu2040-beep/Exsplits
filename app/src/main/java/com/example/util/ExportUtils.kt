package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.data.DomainBill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtils {

    suspend fun exportJson(context: Context, uri: Uri, bill: DomainBill) = withContext(Dispatchers.IO) {
        val jsonString = Json { prettyPrint = true }.encodeToString(bill)
        writeString(context, uri, jsonString)
        NotificationUtils.showExportNotification(context, "JSON")
    }

    suspend fun exportText(context: Context, uri: Uri, bill: DomainBill) = withContext(Dispatchers.IO) {
        val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(bill.date))
        val sb = StringBuilder()
        sb.append("Bill: ${bill.title}\n")
        sb.append("Amount: ${bill.currency} ${bill.amount}\n")
        sb.append("Date: $date\n")
        sb.append("Paid by: ${bill.participants.find { it.id == bill.paidBy }?.name ?: "Unknown"}\n\n")
        sb.append("Split Details:\n")
        bill.splitData.forEach { (id, value) ->
            val p = bill.participants.find { it.id == id }?.name ?: id
            sb.append("- $p: ${bill.currency} $value\n")
        }
        writeString(context, uri, sb.toString())
        NotificationUtils.showExportNotification(context, "Text")
    }

    suspend fun exportPdf(context: Context, uri: Uri, bill: DomainBill) = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        paint.color = Color.BLACK
        paint.textSize = 24f
        canvas.drawText("Bill Report: ${bill.title}", 50f, 50f, paint)

        paint.textSize = 16f
        canvas.drawText("Amount: ${bill.currency} ${bill.amount}", 50f, 90f, paint)
        val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(bill.date))
        canvas.drawText("Date: $date", 50f, 115f, paint)
        val payer = bill.participants.find { it.id == bill.paidBy }?.name ?: "Unknown"
        canvas.drawText("Paid by: $payer", 50f, 140f, paint)

        canvas.drawText("Split Details:", 50f, 180f, paint)
        var yPos = 210f
        bill.splitData.forEach { (id, value) ->
            val p = bill.participants.find { it.id == id }?.name ?: id
            val amountStr = String.format("%.2f", value)
            canvas.drawText("- $p: ${bill.currency} $amountStr", 70f, yPos, paint)
            yPos += 30f
        }

        pdfDocument.finishPage(page)
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }
        pdfDocument.close()
        NotificationUtils.showExportNotification(context, "PDF")
    }

    private fun writeString(context: Context, uri: Uri, content: String) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(content.toByteArray())
        }
    }
}

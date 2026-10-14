package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object IntentHelper {
  fun dialPhone(context: Context, rawNumber: String) {
    try {
      val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$rawNumber")
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Could not open phone dialer", Toast.LENGTH_SHORT).show()
    }
  }

  fun openWhatsApp(context: Context, rawNumber: String, prefillMessage: String) {
    try {
      // Pakistani numbers: e.g. 03337803988 -> 923337803988
      val cleanNumber = rawNumber.replace("+", "").replace("-", "").replace(" ", "").trim()
      val intlNumber = if (cleanNumber.startsWith("0")) {
        "92" + cleanNumber.substring(1)
      } else if (!cleanNumber.startsWith("92")) {
        "92$cleanNumber"
      } else {
        cleanNumber
      }

      val encodedMsg = URLEncoder.encode(prefillMessage, "UTF-8")
      val url = "https://api.whatsapp.com/send?phone=$intlNumber&text=$encodedMsg"
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "WhatsApp not installed or could not be opened", Toast.LENGTH_SHORT).show()
    }
  }

  fun openWebUrl(context: Context, url: String) {
    try {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
    }
  }

  fun copyToClipboard(context: Context, label: String, text: String, toastMessage: String? = "Copied to clipboard") {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    if (!toastMessage.isNullOrBlank()) {
      Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }
  }
}

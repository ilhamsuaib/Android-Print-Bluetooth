package id.ilhamsuaib.androidprintbluetooth.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonElement
import java.text.NumberFormat

/**
 * Created by @ilhamsuaib on 20/10/18.
 * github.com/ilhamsuaib
 */

fun EditText.onTextChangedListener(delay: Long = 500,
                                   onChanged: (s: String) -> Unit) {
    val handler = Handler(Looper.getMainLooper() /*UI thread*/)
    var workRunnable: Runnable? = null
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            workRunnable = Runnable {
                onChanged(s.toString())
            }
            handler.postDelayed(workRunnable, delay)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            handler.removeCallbacks(workRunnable)
        }
    })
}

fun Context.toast(s: String) {
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

fun String.fromCurrency(): Int {
    val nominal = if (this == "") "0" else this
    val result = nominal.replace(".", "")
    return result.replace(",", "").toInt()
}

fun Int.toCurrency(): String {
    val num = NumberFormat.getInstance()
    return num.format(this.toLong()).replace(",", ".")
}

fun Any.toJson(): JsonElement = Gson().toJsonTree(this)
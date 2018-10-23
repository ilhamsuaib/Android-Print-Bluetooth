package id.ilhamsuaib.androidprintbluetooth.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

import java.text.NumberFormat
import java.util.Locale

/**
 * Created by ilham on 9/5/17.
 */

class CustomTextWatcher(private val edt: EditText) : TextWatcher {

    private var current = ""

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

    }

    override fun afterTextChanged(s: Editable) {
        if (s.toString() != current) {
            edt.removeTextChangedListener(this)

            val local = Locale("id", "id")
            val replaceable = String.format("[Rp,.\\s]",
                    NumberFormat.getCurrencyInstance().currency
                            .getSymbol(local))
            val cleanString = s.toString().replace(replaceable.toRegex(), "")

            val parsed: Double = try {
                java.lang.Double.parseDouble(cleanString)
            } catch (e: NumberFormatException) {
                0.00
            }

            val formatter = NumberFormat
                    .getCurrencyInstance(local)
            formatter.maximumFractionDigits = 0
            formatter.isParseIntegerOnly = true
            val formatted = formatter.format(parsed)

            val replace = String.format("[Rp\\s]",
                    NumberFormat.getCurrencyInstance().currency
                            .getSymbol(local))
            val clean = formatted.replace(replace.toRegex(), "")

            current = formatted
            edt.setText(clean)
            try {
                edt.setSelection(clean.length)
            } catch (ioobe: IndexOutOfBoundsException) {
                edt.setSelection(clean.length.minus(1))
            }
            edt.addTextChangedListener(this)
        }
    }
}

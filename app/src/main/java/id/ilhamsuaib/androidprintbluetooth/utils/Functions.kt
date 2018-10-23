package id.ilhamsuaib.androidprintbluetooth.utils

/**
 * Created by @ilhamsuaib on 22/10/18.
 * github.com/ilhamsuaib
 */

/*this method is for making printer output alignment justify*/
fun justifyPrintLine(leftText: String? = "", rightText: String? = "") : String{
    //dik : total char per line = 31 chars
    //dit : buat print line biar rata kiri dan kanan tanpa pindah baris
    /*solusi :
    1. a = hitung jumlah karakter key (left text)
    2. b = hitung jumlah karakter value (right text)
    3. total_chars = a+b
    4. total whitespace = 30 - total_chars
    5. lakukan loping untuk mencetak whitespace
    6. gabungkan : a+whitespace+b */
    val totalChars: Int = rightText?.length?.let { leftText?.length?.plus(it) } ?: 0
    val whiteSpaceChars = 30 - totalChars
    return if (whiteSpaceChars >= 0) {
        var whiteSpace = ""
        (0..whiteSpaceChars).forEach {
            whiteSpace += " "
        }
        "$leftText$whiteSpace$rightText"
    } else {
        "$leftText $rightText"
    }
}

object Const {
    var ID_PRINT: String? = null
}

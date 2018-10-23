package id.ilhamsuaib.androidprintbluetooth.nota

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by @ilhamsuaib on 20/10/18.
 * github.com/ilhamsuaib
 */

@Parcelize
data class Item(var nama: String = "",
                var harga: Int = 0,
                var jumlah: Int = 0,
                var totalHarga: Int = 0) : Parcelable
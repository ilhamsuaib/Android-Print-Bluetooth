package id.ilhamsuaib.androidprintbluetooth.nota

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.ilhamsuaib.androidprintbluetooth.R
import id.ilhamsuaib.androidprintbluetooth.utils.CustomTextWatcher
import id.ilhamsuaib.androidprintbluetooth.utils.fromCurrency
import id.ilhamsuaib.androidprintbluetooth.utils.onTextChangedListener
import id.ilhamsuaib.androidprintbluetooth.utils.toCurrency
import kotlinx.android.synthetic.main.adapter_item.view.*

/**
 * Created by @ilhamsuaib on 20/10/18.
 * github.com/ilhamsuaib
 */

class ItemAdapter(private val listItem: MutableList<Item>,
                  private val onTextChange: (item: Item, i: Int) -> Unit) : RecyclerView.Adapter<ItemAdapter.Holder>() {

    override fun onCreateViewHolder(group: ViewGroup, type: Int): Holder {
        val view = LayoutInflater.from(group.context).inflate(R.layout.adapter_item, group, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = listItem.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position)
    }

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(position: Int) {
            val item = listItem[position]

            view.edtNama.setText(item.nama)
            view.edtHarga.setText(item.harga.toString())
            view.edtJumlah.setText(item.jumlah.toString())
            view.tvTotalHarga.text = item.totalHarga.toString()

            view.edtHarga.addTextChangedListener(CustomTextWatcher(view.edtHarga))
            view.edtJumlah.addTextChangedListener(CustomTextWatcher(view.edtJumlah))

            view.edtNama.onTextChangedListener(delay = 0) {
                item.nama = it
            }

            view.edtHarga.onTextChangedListener(delay = 100) {
                item.harga = it.fromCurrency()
                item.totalHarga = item.harga * item.jumlah
                view.tvTotalHarga.text = item.totalHarga.toCurrency()
                onTextChange(item, position)
            }

            view.edtJumlah.onTextChangedListener(delay = 100) {
                item.jumlah = it.fromCurrency()
                item.totalHarga = item.harga * item.jumlah
                view.tvTotalHarga.text = item.totalHarga.toCurrency()
                onTextChange(item, position)
            }
        }
    }
}
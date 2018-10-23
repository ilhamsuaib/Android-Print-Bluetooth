package id.ilhamsuaib.androidprintbluetooth

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.ilhamsuaib.androidprintbluetooth.nota.Item
import id.ilhamsuaib.androidprintbluetooth.utils.toCurrency
import kotlinx.android.synthetic.main.adapter_preview.view.*

/**
 * Created by @ilhamsuaib on 22/10/18.
 * github.com/ilhamsuaib
 */

class PreviewAdapter(private val listItem: List<Item>) : RecyclerView.Adapter<PreviewAdapter.Holder>() {

    override fun onCreateViewHolder(group: ViewGroup, type: Int): Holder {
        val inflater = LayoutInflater.from(group.context)
        val view = inflater.inflate(R.layout.adapter_preview, group, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = listItem.size

    override fun onBindViewHolder(holder: Holder, i: Int) {
        holder.bind(i)
    }

    inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val item = listItem[position]
            view.tvNamaItem.text = item.nama
            view.tvHarga.text = "${item.harga.toCurrency()} x ${item.jumlah}"
            view.tvTotalHarga.text = item.totalHarga.toCurrency()
        }
    }
}
package id.ilhamsuaib.androidprintbluetooth.nota

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import id.ilhamsuaib.androidprintbluetooth.R
import id.ilhamsuaib.androidprintbluetooth.utils.toJson
import kotlinx.android.synthetic.main.activity_nota.*

/**
 * Created by @ilhamsuaib on 20/10/18.
 * github.com/ilhamsuaib
 */

class NotaActivity : AppCompatActivity() {

    private val listItem = mutableListOf<Item>()
    private val itemAdapter = ItemAdapter(listItem) { item, i ->
        listItem[i] = item
        Log.d(TAG, "listItem : ${listItem.toJson()}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nota)

        setupView()
    }

    private fun setupView() {
        listItem.add(Item())

        rvNota.apply {
            layoutManager = LinearLayoutManager(this@NotaActivity)
            adapter = itemAdapter
        }

        btnAdd.setOnClickListener {
            listItem.add(Item())
            itemAdapter.notifyItemInserted(itemAdapter.itemCount.minus(1))
        }

        btnMin.setOnClickListener {
            if (listItem.size > 1) {
                listItem.removeAt(itemAdapter.itemCount.minus(1))
                itemAdapter.notifyItemRemoved(itemAdapter.itemCount)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_print) {
            print()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun print() {
        val intent = Intent(this, PreviewActivity::class.java)
        intent.putExtra(TOKO, edtNamaToko.text.toString().toUpperCase())
        intent.putExtra(ALAMAT, edtAlamat.text.toString())
        intent.putExtra(NO_HP, edtNoHp.text.toString())
        intent.putExtra(TANGGAL, edtTanggal.text.toString())
        intent.putExtra(FOOTER, edtFooter.text.toString())
        intent.putParcelableArrayListExtra(LIST_ITEM, ArrayList(listItem))
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_print, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        const val TAG = "NotaActivity"
        const val TOKO = "toko"
        const val ALAMAT = "alamat"
        const val NO_HP = "no_hp"
        const val TANGGAL = "tanggal"
        const val FOOTER = "footer"
        const val LIST_ITEM = "listItem"
    }
}
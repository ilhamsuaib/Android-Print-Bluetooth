package id.ilhamsuaib.androidprintbluetooth.nota

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.zj.btsdk.BluetoothService
import id.ilhamsuaib.androidprintbluetooth.*
import id.ilhamsuaib.androidprintbluetooth.utils.Const
import id.ilhamsuaib.androidprintbluetooth.utils.justifyPrintLine
import id.ilhamsuaib.androidprintbluetooth.utils.toCurrency
import id.ilhamsuaib.androidprintbluetooth.utils.toast
import kotlinx.android.synthetic.main.activity_preview_nota.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

@SuppressLint("SetTextI18n")
class PreviewActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, HandlerInterface {

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_BLUETOOTH = 0
        private const val RC_CONNECT_DEVICE = 1
        private const val RC_ENABLE_BLUETOOTH = 2
    }

    private lateinit var mService: BluetoothService
    private lateinit var listItem: List<Item>
    private lateinit var namaToko: String
    private lateinit var alamat: String
    private lateinit var noHp: String
    private lateinit var tanggal: String
    private val noTransaksi: Int = getRandomCode()
    private var footer = ""
    private val footer2 = ""
    private var isPrinterReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_nota)

        listItem = intent.getParcelableArrayListExtra(NotaActivity.LIST_ITEM)
        namaToko = intent.getStringExtra(NotaActivity.TOKO)
        alamat = intent.getStringExtra(NotaActivity.ALAMAT)
        noHp = intent.getStringExtra(NotaActivity.NO_HP)
        tanggal = intent.getStringExtra(NotaActivity.TANGGAL)
        footer = intent.getStringExtra(NotaActivity.FOOTER)

        setupView()
        setupBluetooth()
    }

    private fun setupView() {
        tvNamaToko.text = namaToko
        tvAlamat.text = alamat
        tvTelp.text = "Hp : $noHp"
        tvTanggal.text = tanggal
        tvNoTransaksi.text = "No. $noTransaksi"
        tvFooter.text = footer
        val itemAdapter = PreviewAdapter(listItem)
        rvItem.apply {
            layoutManager = LinearLayoutManager(this@PreviewActivity)
            adapter = itemAdapter
        }
        tvTotalHarga.text = "Rp ${listItem.sumBy { it.totalHarga }.toCurrency()}"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_print, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_print) printText()
        return super.onOptionsItemSelected(item)
    }

    @AfterPermissionGranted(RC_BLUETOOTH)
    private fun setupBluetooth() {
        val params = arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
        if (!EasyPermissions.hasPermissions(this, *params)) {
            EasyPermissions.requestPermissions(this, "You need bluetooth permission",
                    RC_BLUETOOTH, *params)
            return
        }
        mService = BluetoothService(this, BluetoothHandler(this))

        Const.ID_PRINT?.let {
            val mDevice = mService.getDevByMac(it)
            mService.connect(mDevice)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // TODO: 10/11/17 do something
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // TODO: 10/11/17 do something
    }

    override fun onDeviceConnected() {
        isPrinterReady = true
        toast("Terhubung dengan perangkat")
    }

    override fun onDeviceConnecting() {
        toast("Sedang menghubungkan...")
    }

    override fun onDeviceConnectionLost() {
        isPrinterReady = false
        toast("Koneksi perangkat terputus")
    }

    override fun onDeviceUnableToConnect() {
        toast("Tidak dapat terhubung ke perangkat")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_ENABLE_BLUETOOTH -> if (resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "onActivityResult: bluetooth aktif")
            } else
                Log.i(TAG, "onActivityResult: bluetooth harus aktif untuk menggunakan fitur ini")
            RC_CONNECT_DEVICE -> if (resultCode == Activity.RESULT_OK) {
                val address = data?.extras?.getString(DeviceActivity.EXTRA_DEVICE_ADDRESS)
                Const.ID_PRINT = address
                val mDevice = mService.getDevByMac(address)
                mService.connect(mDevice)
            }
        }
    }

    private fun printText() {
        if (!mService.isAvailable) {
            Log.i(TAG, "printText: perangkat tidak support bluetooth")
            return
        }
        if (isPrinterReady) {
            mService.write(PrinterCommands.ESC_ALIGN_CENTER)
            mService.sendMessage(namaToko, "")
            mService.sendMessage(alamat, "")
            mService.sendMessage("Hp. $noHp", "")
            mService.sendMessage(PrinterCommands.dashLine, "")
            mService.sendMessage(justifyPrintLine(tanggal, "No. $noTransaksi"), "")
            mService.sendMessage(PrinterCommands.dashLine, "")
            var totalHarga = 0
            mService.write(PrinterCommands.ESC_ALIGN_LEFT)
            listItem.forEach {
                mService.sendMessage(it.nama, "")
                mService.sendMessage(justifyPrintLine("${it.harga.toCurrency()} x ${it.jumlah.toCurrency()}", it.totalHarga.toCurrency()), "")
                totalHarga += it.totalHarga
            }
            mService.write(PrinterCommands.ESC_ALIGN_CENTER)
            mService.sendMessage(PrinterCommands.dashLine, "")
            mService.sendMessage(justifyPrintLine("TOTAL", "Rp. ${totalHarga.toCurrency()}"), "")
            mService.write(PrinterCommands.ESC_ENTER)
            mService.sendMessage(footer, "")
            mService.sendMessage(footer2, "")
            mService.write(PrinterCommands.ESC_ENTER)
        } else {
            if (mService.isBTopen)
                startActivityForResult(Intent(this, DeviceActivity::class.java), RC_CONNECT_DEVICE)
            else
                requestBluetooth()
        }
    }

    private fun requestBluetooth() {
        if (!mService.isBTopen) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, RC_ENABLE_BLUETOOTH)
        }
    }

    private fun getRandomCode(): Int {
        val rnd = Random()
        return 100 + rnd.nextInt(900000)
    }
}

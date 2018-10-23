package id.ilhamsuaib.androidprintbluetooth.spbu

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Printer
import android.view.Menu
import android.view.MenuItem
import com.zj.btsdk.BluetoothService
import id.ilhamsuaib.androidprintbluetooth.*
import id.ilhamsuaib.androidprintbluetooth.utils.*
import kotlinx.android.synthetic.main.activity_spbu.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

@SuppressLint("SetTextI18n")
class SpbuActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, HandlerInterface {

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_BLUETOOTH = 0
        private const val RC_CONNECT_DEVICE = 1
        private const val RC_ENABLE_BLUETOOTH = 2
    }

    private lateinit var mService: BluetoothService
    private lateinit var noSpbu: String
    private lateinit var tanggal: String
    private lateinit var jam: String
    private lateinit var noPompa: String
    private lateinit var noSelang: String
    private lateinit var noNota: String
    private lateinit var jenisBBM: String
    private var liter = 0
    private var hargaLiter = 0
    private var total = 0
    private var thanks: String = "Terima kasih dan selamat jalan"
    private var isPrinterReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spbu)

        edtHargaLiter.addTextChangedListener(CustomTextWatcher(edtHargaLiter))
        edtLiter.onTextChangedListener(delay = 0) {
            liter = it.toInt()
            total = liter * hargaLiter

            tvTotal.text = total.toCurrency()
        }
        edtHargaLiter.onTextChangedListener(delay = 0) {
            hargaLiter = it.fromCurrency()
            total = liter * hargaLiter

            tvTotal.text = total.toCurrency()
        }
        setupBluetooth()
    }

    private fun setupText() {
        noSpbu = edtNoSpbu.text.toString()
        tanggal = edtTanggal.text.toString()
        jam = edtJam.text.toString()
        noPompa = edtNomorPompa.text.toString()
        noSelang = edtNomorSelang.text.toString()
        noNota = edtNomorNota.text.toString()
        jenisBBM = edtJenisBBM.text.toString()
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
        setupText()
        if (!mService.isAvailable) {
            Log.i(TAG, "printText: perangkat tidak support bluetooth")
            return
        }
        if (isPrinterReady) {
            mService.write(PrinterCommands.ESC_ALIGN_LEFT)
            mService.sendMessage(noSpbu.toUpperCase(), "")
            mService.write(PrinterCommands.ESC_ENTER)
            mService.sendMessage("$tanggal $jam", "")
            mService.sendMessage(PrinterCommands.dashLine, "")
            mService.sendMessage("Nomor Pompa  : $noPompa", "")
            mService.sendMessage("Nomor Selang : $noSelang", "")
            mService.sendMessage("Nomor Nota   : $noNota", "")
            mService.sendMessage("Jenis BBM    : $jenisBBM", "")
            mService.sendMessage("Liter        : $liter", "")
            mService.sendMessage("Harga/Liter  : Rp. $hargaLiter", "")
            mService.sendMessage("Total        : Rp. $hargaLiter", "")
            mService.sendMessage(PrinterCommands.dashLine, "")
            mService.sendMessage(thanks, "")
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

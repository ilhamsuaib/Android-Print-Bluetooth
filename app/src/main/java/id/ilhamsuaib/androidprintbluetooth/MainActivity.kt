package id.ilhamsuaib.androidprintbluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.zj.btsdk.BluetoothService
import com.zj.btsdk.PrintPic
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, HandlerInterface {

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_BLUETOOTH = 0
        private const val RC_CONNECT_DEVICE = 1
        private const val RC_ENABLE_BLUETOOTH = 2
    }

    private lateinit var mService: BluetoothService
    private var isPrinterReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBluetooth()
        btnPrintText.setOnClickListener {
            printText()
        }
        btnPrintImage.setOnClickListener {
            printImage()
        }
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
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // TODO: 10/11/17 do something
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // TODO: 10/11/17 do something
    }

    override fun onDeviceConnected() {
        isPrinterReady = true
        tvStatus.text = "Terhubung dengan perangkat"
    }

    override fun onDeviceConnecting() {
        tvStatus.text = "Sedang menghubungkan..."
    }

    override fun onDeviceConnectionLost() {
        isPrinterReady = false
        tvStatus.text = "Koneksi perangkat terputus"
    }

    override fun onDeviceUnableToConnect() {
        tvStatus.text = "Tidak dapat terhubung ke perangkat"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_ENABLE_BLUETOOTH -> if (resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "onActivityResult: bluetooth aktif")
            } else
                Log.i(TAG, "onActivityResult: bluetooth harus aktif untuk menggunakan fitur ini")
            RC_CONNECT_DEVICE -> if (resultCode == Activity.RESULT_OK) {
                val address = data!!.extras!!.getString(DeviceActivity.EXTRA_DEVICE_ADDRESS)
                val mDevice = mService.getDevByMac(address)
                mService.connect(mDevice)
            }
        }
    }

    fun printText() {
        if (!mService.isAvailable) {
            Log.i(TAG, "printText: perangkat tidak support bluetooth")
            return
        }
        if (isPrinterReady) {
            if (edtText.text.toString().isBlank()) {
                Toast.makeText(this, "Cant print null text", Toast.LENGTH_SHORT).show()
                return
            }
            mService.write(PrinterCommands.ESC_ALIGN_CENTER)
            mService.sendMessage(edtText.text.toString(), "")
            mService.write(PrinterCommands.ESC_ENTER)
        } else {
            if (mService.isBTopen)
                startActivityForResult(Intent(this, DeviceActivity::class.java), RC_CONNECT_DEVICE)
            else
                requestBluetooth()
        }
    }

    fun printImage() {
        if (isPrinterReady) {
            val pg = PrintPic()
            pg.initCanvas(400)
            pg.initPaint()
            pg.drawImage(0f, 0f, Environment.getExternalStorageDirectory().absolutePath + "/Londree/struk_londree.png")
            val sendData = pg.printDraw()
            mService.write(PrinterCommands.ESC_ALIGN_CENTER)
            mService.write(sendData)
        }
    }

    private fun requestBluetooth() {
        if (!mService.isBTopen) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, RC_ENABLE_BLUETOOTH)
        }
    }
}

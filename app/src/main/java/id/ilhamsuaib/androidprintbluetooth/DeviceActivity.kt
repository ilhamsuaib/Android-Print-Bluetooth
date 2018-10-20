package id.ilhamsuaib.androidprintbluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.zj.btsdk.BluetoothService
import kotlinx.android.synthetic.main.activity_device.*

class DeviceActivity : AppCompatActivity() {

    private lateinit var mService: BluetoothService
    private lateinit var newDeviceAdapter: ArrayAdapter<String>

    companion object {
        const val EXTRA_DEVICE_ADDRESS = "device_address"
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device.bondState != BluetoothDevice.BOND_BONDED) {
                    newDeviceAdapter.add(device.name + "\n" + device.address)
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                    title = "Pilih Perangkat"
                    if (newDeviceAdapter.count == 0) {
                        newDeviceAdapter.add("Perangkat tidak ditemukan")
                    }
                }
            }
        }
    }

    private val mDeviceClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        mService.cancelDiscovery()

        val info = (view as TextView).text.toString()
        val address = info.substring(info.length - 17)

        val intent = Intent()
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        title = "Perangkat Bluetooth"

        val pairedDeviceAdapter = ArrayAdapter<String>(this, R.layout.device_name)
        lvPairedDevice.adapter = pairedDeviceAdapter
        lvPairedDevice.onItemClickListener = mDeviceClickListener

        newDeviceAdapter = ArrayAdapter(this, R.layout.device_name)
        lvNewDevice.adapter = newDeviceAdapter
        lvNewDevice.onItemClickListener = mDeviceClickListener

        var intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, intentFilter)

        intentFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(mReceiver, intentFilter)

        mService = BluetoothService(this, null)

        val pairedDevice = mService.pairedDev

        if (pairedDevice.size > 0) {
            tvPairedDevice!!.visibility = View.VISIBLE
            for (device in pairedDevice) {
                pairedDeviceAdapter.add(device.name + "\n" + device.address)
            }
        } else {
            val noDevice = "Tidak ada perangkat terhubung!"
            pairedDeviceAdapter.add(noDevice)
        }

        btnScan.setOnClickListener {
            scan(it)
        }
    }

    fun scan(view: View) {
        doDiscovery()
        view.visibility = View.GONE
    }

    private fun doDiscovery() {
        title = "Mencari perangkat..."
        tvNewDevice.visibility = View.VISIBLE

        if (mService.isDiscovering) {
            mService.cancelDiscovery()
        }

        mService.startDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()
        mService.cancelDiscovery()
        unregisterReceiver(mReceiver)
    }
}

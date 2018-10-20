package id.ilhamsuaib.androidprintbluetooth

/**
 * Created by @ilhamsuaib on 20/10/18.
 * github.com/ilhamsuaib
 */

interface HandlerInterface {

    fun onDeviceConnected()

    fun onDeviceConnecting()

    fun onDeviceConnectionLost()

    fun onDeviceUnableToConnect()
}
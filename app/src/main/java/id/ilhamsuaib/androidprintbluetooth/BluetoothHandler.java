package id.ilhamsuaib.androidprintbluetooth;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zj.btsdk.BluetoothService;

/**
 * Created by ilham on 9/15/17.
 */

public class BluetoothHandler extends Handler{

    private static final String TAG = BluetoothHandler.class.getSimpleName();
    private Activity activity;
    private HandlerInterfce hi;

    public BluetoothHandler(Activity activity, HandlerInterfce hi) {
        this.activity = activity;
        this.hi = hi;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case BluetoothService.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                    case BluetoothService.STATE_CONNECTED:
                        hi.onDeviceConnected();
                        break;
                    case BluetoothService.STATE_CONNECTING:
                        hi.onDeviceConnecting();
                        break;
                    case BluetoothService.STATE_LISTEN:
                    case BluetoothService.STATE_NONE:
                        Log.i(TAG, "Bluetooth state listen or none");
                        break;
                }
                break;
            case BluetoothService.MESSAGE_CONNECTION_LOST:
                hi.onDeviceConnectionLost();
                break;
            case BluetoothService.MESSAGE_UNABLE_CONNECT:
                hi.onDeviceUnableToConnect();
                break;
        }
    }

    public interface HandlerInterfce {
        void onDeviceConnected();

        void onDeviceConnecting();

        void onDeviceConnectionLost();

        void onDeviceUnableToConnect();
    }
}

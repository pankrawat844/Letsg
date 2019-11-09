package come.texi.driver

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter

import java.net.URISyntaxException

class TestMapActivity : AppCompatActivity() {
    private var mSocket: Socket? = null

    /**
     * Listener to handle messages received from chat server of any type... Listener registered at the time of socket connected
     */
    private val onSocketConnectionListener = Emitter.Listener { args ->
        runOnUiThread {
            // handle the response args
            Toast.makeText(this@TestMapActivity, "Come", Toast.LENGTH_LONG).show()
            val data = args[0] as JSONObject

            Toast.makeText(this@TestMapActivity, data.toString() + "", Toast.LENGTH_LONG).show()
            Log.d("data", "connected data = $data")
        }
    }

    /**
     * Listener for socket connection error.. listener registered at the time of socket connection
     */
    private val onConnectError = Emitter.Listener {
        runOnUiThread {
            if (mSocket != null)
                if (mSocket!!.connected() == false) {
                    Log.d("connected", "connected two= " + mSocket!!.connected())
                    //socketConnection();
                } else {
                    Log.d("connected", "connected three= " + mSocket!!.connected())
                }
        }
    }

    //    private com.github.nkzawa.socketio.client.Socket mSocket;
    //    {
    //        try {
    //            mSocket = IO.socket("http://107.170.36.24:3000");
    //            mSocket.connect();
    //            Log.d("mSocket","mSocket = "+mSocket.connected());
    //        } catch (URISyntaxException e) {}
    //    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_map)

        try {
            mSocket = IO.socket(SERVER_IP)
            mSocket!!.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        CreateDriver()
        socketConnection()

    }

    fun CreateDriver() {


        try {
            val userobj = JSONObject()
            userobj.put("driver_id", "3")
            Log.d("connected ", "connected one = " + mSocket!!.connected() + "==" + userobj)
            mSocket!!.emit("New User Register", userobj)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    /**
     * chat socket connection methods
     */
    fun socketConnection() {
        mSocket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
        mSocket!!.on("Driver Detail", onSocketConnectionListener)

        Thread(Runnable {
            Log.d("connected ", "connected one = " + mSocket!!.connected())
            while (mSocket!!.connected() == false) {
                //do nothing
            }
            Log.d("connected ", "connected one = " + mSocket!!.connected())
            //sendConnectData();
        }).start()
    }

    companion object {

        private val SERVER_IP = "http://107.170.36.24:4040"
    }

}

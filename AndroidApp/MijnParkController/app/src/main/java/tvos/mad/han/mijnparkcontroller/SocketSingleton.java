package tvos.mad.han.mijnparkcontroller;

import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by DDulos on 21-Nov-16.
 */
public class SocketSingleton {
    // singleton Part

    // Singleton for sockets: http://stackoverflow.com/questions/28181265/use-socket-in-another-activity-android

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:3000");
        } catch (URISyntaxException e) {
        }
    }

    private static SocketSingleton instance;

    static SocketSingleton getInstance(){
        return (instance == null) ? instance = new SocketSingleton() : instance;
    }

    private SocketSingleton(){
        mSocket.connect();
        Log.v("connect", "Connect");
    }

    public Socket getSocket() {
        return mSocket;
    }

    void emit(String event, Object args) {
        mSocket.emit(event, args);
    }
}

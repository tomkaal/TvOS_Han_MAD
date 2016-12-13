package tvos.mad.han.mijnparkcontroller;

import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by tommi on 7-12-2016.
 */
public class SocketSingleton {
    // singleton Part

    // Singleton for sockets: http://stackoverflow.com/questions/28181265/use-socket-in-another-activity-android

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(MainActivity.MAIN_URL);     // Socket URL
//            mSocket = IO.socket("http://10.0.2.2:3000");  // zit nu static in mainactivity
//            mSocket = IO.socket("http://192.168.137.1:3000"); // real testing

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
    void emit(String event) {
        mSocket.emit(event);
    }

    void on(String event, Emitter.Listener listener){
        mSocket.on(event, listener);
    }
}

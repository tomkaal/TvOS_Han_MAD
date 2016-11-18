package tvos.mad.han.mijnparkcontroller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:3000");
        } catch (URISyntaxException e) {}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSocket.connect();
        Log.v("connect", "Connect");

        final EditText mEdit   = (EditText)findViewById(R.id.input);

        final Button sortButton = (Button) findViewById(R.id.sendButton);
        sortButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSocket.emit("chat message", mEdit.getText().toString());
            }
        });
    }
}

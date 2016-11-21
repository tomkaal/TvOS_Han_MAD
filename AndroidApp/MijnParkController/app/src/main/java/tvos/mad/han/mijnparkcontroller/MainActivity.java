package tvos.mad.han.mijnparkcontroller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
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
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                1);

        final EditText groupNameInput   = (EditText)findViewById(R.id.groupNameInput);

        final Button addGroupButton = (Button) findViewById(R.id.addGroupButton);
        addGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                mSocket.emit("Create group", groupNameInput.getText().toString());
            }
        });

        mSocket.connect();
        Log.v("connect", "Connect");

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userId", "hetUserId1234");
        editor.commit();
        final EditText userNameInput   = (EditText)findViewById(R.id.userNameInput);

        final Button addUserButton = (Button) findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                mSocket.emit("Join group", userNameInput.getText().toString());
                Intent intent = new Intent(MainActivity.this, JoinGroup.class)
                        .putExtra("name", userNameInput.getText().toString());
                startActivity(intent);
            }
        });

        final Button testBeaconButton = (Button) findViewById(R.id.beaconTest);
        testBeaconButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                mSocket.emit("Join group", userNameInput.getText().toString());
                Intent intent = new Intent(MainActivity.this, BeaconSearchActivity.class);
                startActivity(intent);
            }
        });
    }



}

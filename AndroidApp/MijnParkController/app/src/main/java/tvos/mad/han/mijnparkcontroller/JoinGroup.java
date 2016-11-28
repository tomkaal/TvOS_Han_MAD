package tvos.mad.han.mijnparkcontroller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class JoinGroup extends AppCompatActivity {
    private String group;
    private String uuid = "aaaaa";
    ListView listView;
    //    SocketSingleton socketSingleton;
    Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
//        socketSingleton = SocketSingleton.getInstance();

        final String userName = getIntent().getExtras().getString("name");
        Log.v("Username", userName);

        try {
//            socket = IO.socket("http://10.0.2.2:3000");
            socket = IO.socket("http://192.168.137.1:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        updateGreetingMessageWithUserName(userName);

        listView = (ListView) findViewById(R.id.groupListView);
        ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < 15; i++) {
            values.add("Klas " + (i + 1));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(position);
//                group =  "a123456";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user", "Tommie");
                    jsonObject.put("groupId", uuid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.emit("add_user_to_group", jsonObject.toString());


//                Intent intent = new Intent(JoinGroup.this, JoinTeam.class);
//                Bundle extras = new Bundle();
//                extras.putString("name", userName);
//                extras.putString("group", itemValue);
//                intent.putExtras(extras);
//
//                startActivity(intent);
                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();
            }

        });
        socket.on("group_response", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("response", "group response");
//                    JSONObject obj = (JSONObject) args[0]; wanneer het een object is
                String message = (String) args[0];
                Log.v("object", message);


            }
        }).on("conform", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String message = (String) args[0];
                Log.v("conformation", "room conformation " + message);
            }
        });

        socket.connect();
    }

    public void updateGreetingMessageWithUserName(String userName) {
        TextView greetText = (TextView) findViewById(R.id.joinGroupGreetText);
        greetText.setText("Dag " + userName + ", selecteer een groep");
    }

}

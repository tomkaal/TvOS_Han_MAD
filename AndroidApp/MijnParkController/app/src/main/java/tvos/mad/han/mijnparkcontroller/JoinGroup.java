package tvos.mad.han.mijnparkcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class JoinGroup extends AppCompatActivity {
    private String group;
    private String uuid = "aaaaa";
    Socket socket;

    public static final String GROUP_MAP_STRING = "group";
    public static final String OWNER_MAP_STRING = "owner";
    ListView listView;
    private ArrayList<String> groupList;
    private List<Map<String, String>> groupMapList;
    private SimpleAdapter adapter;

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
        groupList = new ArrayList<>();
        addTestGroupsToList();


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, groupList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
//                String itemValue = (String) listView.getItemAtPosition(position);
//                group =  "a123456";
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user", "Tommie");
                    jsonObject.put("groupId", uuid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                socket.emit("add_user_to_group", jsonObject.toString());

                Map itemValue = (Map) listView.getItemAtPosition(position);
                Intent intent = new Intent(JoinGroup.this, JoinTeam.class);
                Bundle extras = new Bundle();
                extras.putString("name", userName);
                extras.putString(GROUP_MAP_STRING, (String) itemValue.get(GROUP_MAP_STRING));
                extras.putString(OWNER_MAP_STRING, (String) itemValue.get(OWNER_MAP_STRING));
                intent.putExtras(extras);

                startActivity(intent);
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

    private void addTestGroupsToList() {
        groupMapList = new ArrayList<>();
        for (int i = 1; i < 14; i++) {
            char teamChar = (char) (64+i);
            addGroupToList("Group-" + teamChar, "owner-" + i);
        }
        adapter = new SimpleAdapter(this, groupMapList,
                android.R.layout.simple_list_item_2,
                new String[]{GROUP_MAP_STRING, OWNER_MAP_STRING},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
    }

    // Called by socket
    public void addGroupToList(String groupName, String groupOwner) {
        if (!groupIsPresent(groupName, groupOwner)) {
            Map<String, String> groupMap = new HashMap<>(2);
            groupMap.put(GROUP_MAP_STRING, groupName);
            groupMap.put(OWNER_MAP_STRING, groupOwner);
            groupMapList.add(groupMap);
        }
    }

    private boolean groupIsPresent(String groupName, String groupOwner) {
        for (Map<String, String> groupMap : groupMapList) {
            if (groupMap.get(GROUP_MAP_STRING).equals(groupName) && groupMap.get(OWNER_MAP_STRING).equals(groupOwner))
                return true;
        }
        return false;
    }

    // Called by socket
    public void removeGroupFromList(String groupName) {
        groupList.remove(groupName);
    }

    public void updateGreetingMessageWithUserName(String userName) {
        TextView greetText = (TextView) findViewById(R.id.joinGroupGreetText);
        greetText.setText("Dag " + userName + ", selecteer een groep");
    }

}

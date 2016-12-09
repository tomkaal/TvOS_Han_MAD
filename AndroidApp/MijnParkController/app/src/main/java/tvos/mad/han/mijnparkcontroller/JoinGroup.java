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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class JoinGroup extends AppCompatActivity {
//    private String group;
//    private String uuid = "aaaaa";

    private SocketSingleton socketSingleton;

    public static final String GROUP_MAP_STRING = "group";
    public static final String OWNER_MAP_STRING = "owner";
    ListView listView;
    private ArrayList<String> groupList;
    private List<Map<String, String>> groupMapList;
    private SimpleAdapter adapter;
    private ArrayList<String> groupIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        socketSingleton = SocketSingleton.getInstance();

        final String userName = getIntent().getExtras().getString("userName");
        final String userId = getIntent().getExtras().getString("userId");

        updateGreetingMessageWithUserName(userName);

        listView = (ListView) findViewById(R.id.groupListView);
        groupList = new ArrayList<>();
        groupIdArray = new ArrayList<>();
        addTestGroupsToList();
        socketSingleton.emit("get_groups");


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, groupList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
//                String itemValue = (String) listView.getItemAtPosition(position);

                Map itemValue = (Map) listView.getItemAtPosition(position);
                Intent intent = new Intent(JoinGroup.this, JoinTeam.class);
                Bundle extras = new Bundle();
                extras.putString("userName", userName);
                extras.putString("userId", userId);
                extras.putString(GROUP_MAP_STRING, (String) itemValue.get(GROUP_MAP_STRING));
                extras.putString(OWNER_MAP_STRING, (String) itemValue.get(OWNER_MAP_STRING));
                extras.putString("groupId", groupIdArray.get(itemPosition));
                intent.putExtras(extras);

                startActivity(intent);
            }

        });


        socketSingleton.on("group_response", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("response", "group response");
//                    JSONObject obj = (JSONObject) args[0]; wanneer het een object is
                String message = (String) args[0];
                Log.v("object", message);


            }
        });

        socketSingleton.on("conform", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String message = (String) args[0];
                Log.v("conformation", "room conformation " + message);
            }
        });
        socketSingleton.on("update_groups", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("response", "Update group");

                JSONArray groupList = (JSONArray) args[0];
                setGroupsFromServer(groupList);
            }
        });
    }

    private void setGroupsFromServer(JSONArray groupList){
        groupMapList.clear();
        groupIdArray.clear();

        Log.v("response", "setting groups");

        for (int i = 0; i < groupList.length(); i++){
            try {
                JSONObject curObj = groupList.getJSONObject(i);
                String groupName = (String) curObj.get("groupName");
                String userName = (String) curObj.get("userName");
                String groupId = (String) curObj.get("groupId");
                Log.v("response", groupName);
                groupIdArray.add(groupId);
                addGroupToList(groupName, userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Log.v("response", groupMapList.toString());
        Log.v("response", groupIdArray.toString());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addTestGroupsToList() {
        groupMapList = new ArrayList<>();
//        for (int i = 1; i < 14; i++) {
//            char teamChar = (char) (64+i);
//            addGroupToList("Group-" + teamChar, "owner-" + i);
//        }

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

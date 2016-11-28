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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinGroup extends AppCompatActivity {

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

        final String userName = getIntent().getExtras().getString("name");
        Log.v("Username", userName);

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

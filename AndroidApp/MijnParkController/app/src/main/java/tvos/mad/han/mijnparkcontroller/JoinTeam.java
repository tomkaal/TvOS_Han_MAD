package tvos.mad.han.mijnparkcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.emitter.Emitter;
import tvos.mad.han.mijnparkcontroller.model.Group;
import tvos.mad.han.mijnparkcontroller.model.Team;
import tvos.mad.han.mijnparkcontroller.model.User;

public class JoinTeam extends AppCompatActivity {

    private static final String TEAM_MAP_STRING = "team";
    private static final String TEAMMEMBER_MAP_STRING = "teammember";
    private UserGroupSingleton userGroupSingleton;
    private SocketSingleton socketSingleton;

    private ProgressBar progressBar;
    private ListView teamListView;
    private ListView groupListView;
    private TextView statusTextView;
    private TextView joinedGroupTextView;
    private TextView groupLeaderTextView;

    private ArrayList<Map<String, String>> teamList;
    private ArrayList<String> userList = new ArrayList<String>();

    //    private SimpleAdapter adapter;
    private ArrayAdapter<String> adapter;

    private String userName;
    private String userId;
    private Boolean isAcceptedInGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        userName = extras.getString("userName");
        userId = extras.getString("userId");
        String groupId = extras.getString("groupId");
        String group = extras.getString("group");
        String owner = extras.getString("owner");

        teamList = new ArrayList<>();

        isAcceptedInGroup = false;

        socketSingleton = SocketSingleton.getInstance();

        statusTextView = (TextView) findViewById(R.id.status_text);
        statusTextView.setText(getString(R.string.status_joining_group));

        joinedGroupTextView = (TextView) findViewById(R.id.joinedGroupTextView);
        joinedGroupTextView.setText(group);
        groupLeaderTextView = (TextView) findViewById(R.id.groupLeaderTextView);
        groupLeaderTextView.setText(owner);

        progressBar = (ProgressBar) findViewById(R.id.teamProgressBar);

        groupListView = (ListView) findViewById(R.id.groupListView);
        teamListView = (ListView) findViewById(R.id.teamListView);
        addUserToGroup(userName, userId, groupId, group);
        socketSingleton.emit("get_users_in_group", getIntent().getExtras().getString("groupId"));

        setUserListScreen();

        socketSingleton.on("users_in_group", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("response", "updatedUsers");
                JSONArray jsonArray = (JSONArray) args[0];// wanneer het een object is
                updateScreenWithUsers(jsonArray);
            }
        });

        socketSingleton.on("group_accepted", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("response", "group accepted");
                isAcceptedInGroup = true;
            }
        });

        socketSingleton.on("teams_created", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("response", "teams created");
                JSONObject teamObject = (JSONObject) args[0];
                setCreatedTeams(teamObject);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(JoinTeam.this, GroupOverviewActivity.class);
                        startActivity(intent);
//                        updateScreenTeamJoined();
                    }
                });
            }
        });

        socketSingleton.on("group_denied", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("response", "group denied");
                if (!isAcceptedInGroup) {
                    Intent intent = new Intent(JoinTeam.this, JoinGroup.class)
                            .putExtra("userName", userName)
                            .putExtra("userId", userId);
                    startActivity(intent);
                }
            }
        });
    }

    private void setCreatedTeams(JSONObject socketResponseObject) {
        try {
            userGroupSingleton.getCurrentGroup().removeInitTeam();
            Group group = userGroupSingleton.getCurrentGroup();
            group.setGroupId((String) socketResponseObject.get("groupId"));
            JSONArray teamsArray = (JSONArray) socketResponseObject.get("teams");

            for (int i = 0; i < teamsArray.length(); i++) {
                JSONObject teamObject = teamsArray.getJSONObject(i);
                String teamId = teamObject.getString("teamId");
                String teamName = teamObject.getString("teamName");

                JSONArray usersJSONArray = teamsArray.getJSONObject(i).getJSONArray("users");
                Team team = new Team(teamId, teamName);

                for (int j = 0; j < usersJSONArray.length(); j++) {
                    JSONObject userObject = usersJSONArray.getJSONObject(j);
                    String userId = userObject.getString("userId");
                    String userName = userObject.getString("userName");
                    User user = new User(userId, userName);
                    team.addTeamMember(user);
                    if (userGroupSingleton.getCurrentUser().getUserName().equals(userName))
                        userGroupSingleton.setCurrentTeam(team);
                }
                group.addTeam(team);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addUserToGroup(String userName, String userId, String groupId, String groupName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("groupId", groupId);
            jsonObject.put("groupName", groupName);
            jsonObject.put("userName", userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socketSingleton.emit("add_user_to_group", jsonObject.toString());

    }

    private void updateScreenWithUsers(JSONArray listOfUsers) {
        userList.clear();
        for (int i = 0; i < listOfUsers.length(); i++) {
            try {
                JSONObject curObj = listOfUsers.getJSONObject(i);
                String receivedUserName = (String) curObj.get("userName");
//                String receivedUserId = (String) curObj.get("userId");
                userList.add(receivedUserName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Use with sockets
    public void setUserListScreen() {
        statusTextView.setText(getString(R.string.status_joining_team));

        String groupName = String.valueOf(getIntent().getExtras().getString("group"));
        User owner = new User(getIntent().getExtras().getString("owner"));

        userGroupSingleton = UserGroupSingleton.getInstance();
        Group group = new Group(groupName, owner);
        userGroupSingleton.setCurrentGroup(group);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, userList);
        groupListView.setAdapter(adapter);
    }
}

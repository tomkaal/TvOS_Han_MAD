package tvos.mad.han.mijnparkcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinTeam extends AppCompatActivity {

    private static final String TEAM_MAP_STRING = "team";
    private static final String TEAMMEMBER_MAP_STRING = "teammember";
    private UserGroupSingleton userGroupSingleton;

    private ProgressBar progressBar;
    private ListView teamListView;
    private ListView groupListView;
    private TextView statusTextView;
    private TextView joinedGroupTextView;
    private TextView groupLeaderTextView;
    private TextView joinedTeamTextView;

    private ArrayList<Map<String, String>> teamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String userName = extras.getString("name");
        String group = extras.getString("group");
        String owner = extras.getString("owner");

        statusTextView = (TextView) findViewById(R.id.status_text);
        statusTextView.setText(getString(R.string.status_joining_group));

        joinedGroupTextView = (TextView) findViewById(R.id.joinedGroupTextView);
        joinedGroupTextView.setText(group);
        groupLeaderTextView = (TextView) findViewById(R.id.groupLeaderTextView);
        groupLeaderTextView.setText(owner);

        progressBar = (ProgressBar) findViewById(R.id.teamProgressBar);

        groupListView = (ListView) findViewById(R.id.groupListView);
        teamListView = (ListView) findViewById(R.id.teamListView);

        // TODO remove when sockets are implemented
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                updateScreenGroupJoined();
            }
        }, 3000);
    }

    // Use with sockets
    public void updateScreenGroupJoined() {
        statusTextView.setText(getString(R.string.status_joining_team));

        String groupName = String.valueOf(getIntent().getExtras().getString("group"));
        User owner = new User(getIntent().getExtras().getString("owner"));

        userGroupSingleton = UserGroupSingleton.getInstance();
        Group group = new Group(groupName, owner);
        userGroupSingleton.setCurrentGroup(group);

        ArrayList<String> values = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String username = "User " + i;
            group.addUserToGroup(new User(username));
            values.add(username);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        groupListView.setAdapter(adapter);

        // TODO remove when sockets are implemented
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                updateScreenTeamJoined();
            }
        }, 3000);
    }

    // Use with sockets
    private void updateScreenTeamJoined() {
        statusTextView.setText(getString(R.string.status_joined_group));
        joinedTeamTextView = (TextView) findViewById(R.id.joinedTeamTextView);

        joinedTeamTextView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        teamListView.setVisibility(View.VISIBLE);
        groupListView.setVisibility(View.GONE);

        teamList = new ArrayList<>();
        ArrayList<String> userStringList = new ArrayList<>();
        userStringList.add(userGroupSingleton.getCurrentUser().getUserName());

        ArrayList<User> users = userGroupSingleton.getCurrentGroup().getTeams().get(0).getTeamMembers();

        for (User user : users) {
            userStringList.add(user.getUserName());
        }

        int usersInTeam = 4;

        for (int i = 1; i <= 3; i++) {
            String usersString = "";

            for (int j = 0; j < usersInTeam; j++) {
                usersString += userStringList.get(0) + "\n";
                userStringList.remove(0);
            }

            char teamChar = (char) (64 + i);
            addTeamToList("Team-" + teamChar, usersString);
        }

        userGroupSingleton.setCurrentTeam(new Team("A"));

        SimpleAdapter adapter = new SimpleAdapter(this, teamList,
                android.R.layout.simple_list_item_2,
                new String[]{TEAM_MAP_STRING, TEAMMEMBER_MAP_STRING},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
        teamListView.setAdapter(adapter);

        Button continueButton = (Button) findViewById(R.id.btn_continue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                userGroupSingleton.getCurrentGroup().removeInitTeam();
                Intent intent = new Intent(JoinTeam.this, GroupOverviewActivity.class);
                startActivity(intent);
            }
        });
        continueButton.setVisibility(View.VISIBLE);
    }

    private void addTeamToList(String teamName, String usersString) {
        Map<String, String> teamMap = new HashMap<>(2);
        teamMap.put(TEAM_MAP_STRING, teamName);
        teamMap.put(TEAMMEMBER_MAP_STRING, usersString);
        teamList.add(teamMap);
    }
}

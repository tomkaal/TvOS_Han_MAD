package tvos.mad.han.mijnparkcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tvos.mad.han.mijnparkcontroller.model.Group;
import tvos.mad.han.mijnparkcontroller.model.Team;
import tvos.mad.han.mijnparkcontroller.model.User;

/**
 * Created by DDulos on 28-Nov-16.
 */

public class GroupOverviewActivity extends AppCompatActivity {
    private static final String TEAM_MAP_STRING = "team";
    private static final String TEAMMEMBER_MAP_STRING = "teammember";
    private UserGroupSingleton userGroupSingleton;
    private ListView teamListView;
    private ArrayList<Map<String, String>> teamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_overview);

        userGroupSingleton = UserGroupSingleton.getInstance();

        setupTextViews();
        setupListView();

        Button button = (Button) findViewById(R.id.btn_continue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupOverviewActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupTextViews() {
        Group group = userGroupSingleton.getCurrentGroup();
        Team team = userGroupSingleton.getCurrentTeam();

        String username = userGroupSingleton.getCurrentUser().getUserName();
        String groupname = group.getGroupName();
        String groupowner = group.getGroupOwner().getUserName();
        String teamname = team.getTeamName();
        int points = team.getTeamPoints();

        TextView userTextView = (TextView) findViewById(R.id.txt_username);
        TextView groupTextView = (TextView) findViewById(R.id.txt_groupname);
        TextView ownerTextView = (TextView) findViewById(R.id.txt_groupowner);
        TextView teamTextView = (TextView) findViewById(R.id.txt_teamname);
        TextView pointsTextView = (TextView) findViewById(R.id.txt_teampoints);

        userTextView.setText("Gebruikersnaam: " + username);
        groupTextView.setText("Groepsnaam: " + groupname);
        ownerTextView.setText("Groepsleider: " + groupowner);
        teamTextView.setText("Team: " + teamname);
        pointsTextView.setText("Punten: " + points);
    }

    private void setupListView() {
        teamListView = (ListView) findViewById(R.id.teamListView);
        ArrayList<Team> teams = userGroupSingleton.getCurrentGroup().getTeams();
        teamList = new ArrayList<>();

        for (Team team : teams) {
            String usersString = "";

            for (User user : team.getTeamMembers()) {
                usersString += user.getUserName() + "\n";
            }

            addTeamToList(team.getTeamName(), team.getTeamPoints(), usersString);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, teamList,
                android.R.layout.simple_list_item_2,
                new String[]{TEAM_MAP_STRING, TEAMMEMBER_MAP_STRING},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
        teamListView.setAdapter(adapter);
    }

    private void addTeamToList(String teamName, int teamPoints, String usersString) {
        Map<String, String> teamMap = new HashMap<>(2);
        teamMap.put(TEAM_MAP_STRING, teamName + " - " + teamPoints + " " + getString(R.string.points));
        teamMap.put(TEAMMEMBER_MAP_STRING, usersString);
        teamList.add(teamMap);
    }
}

package tvos.mad.han.mijnparkcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import tvos.mad.han.mijnparkcontroller.model.Team;
import tvos.mad.han.mijnparkcontroller.model.User;

public class CreateTeamActivity extends AppCompatActivity {
    private UserGroupSingleton userGroupSingleton;

    private ListView usersInTeamListView;

    private Spinner teamSpinner;
    private ArrayList<String> teamList;
    private ArrayList<UsersInTeamAdapter> usersInTeamAdapterList;

    private Team selectedTeam;
    private int selectedTeamPosition = 0;
    private int adapterCharIncrement = 65;

    private ListView usersInGroupListView;
    private UsersInGroupAdapter usersInGroupAdapter;

    private TextView groupNameText;
    private TextView groupOwnerText;
    private TextView userInTeamText;
    private TextView inGroupText;

    private Button cancelButton;
    private Button continueButton;
    private Button addTeamButton;
    private Button removeTeamButton;

    private String groupOwner;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        userGroupSingleton = UserGroupSingleton.getInstance();

        setupGroupInfoText();
        setupButtons();
        setupListAdapters();
        setupTeamSpinner();
        setupUserInfoTexts();
    }

    private void setupUserInfoTexts() {
        userInTeamText = (TextView) findViewById(R.id.lbl_inteam);
        inGroupText = (TextView) findViewById(R.id.lbl_ingroup);
    }

    private void setupListAdapters() {
        ArrayList<User> usersInGroupList = userGroupSingleton.getCurrentGroup().getTeams().get(0).getTeamMembers();

        usersInTeamListView = (ListView) findViewById(R.id.listview_inTeam);
        usersInTeamListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removeUserFromTeam(position);
            }
        });

        usersInGroupAdapter = new UsersInGroupAdapter(this, usersInGroupList);
        usersInGroupListView = (ListView) findViewById(R.id.listview_inGroup);
        usersInGroupListView.setAdapter(usersInGroupAdapter);
        usersInGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addUserInTeam(position);
            }
        });
    }

    private void setupTeamSpinner() {
        teamSpinner = (Spinner) findViewById(R.id.teamSpinner);
        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectTeam(teamSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        usersInTeamAdapterList = new ArrayList<>();
        teamList = new ArrayList<>();

        addTeam();
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teamList);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamSpinner.setAdapter(teamAdapter);
    }

    private void selectTeam(int position) {
        if (userGroupSingleton.getCurrentGroup().getTeams().size() == position + 1) {
            selectedTeam = userGroupSingleton.getCurrentGroup().getTeams().get(position);
        } else {
            selectedTeam = userGroupSingleton.getCurrentGroup().getTeams().get(position + 1);
        }
        selectedTeamPosition = position;
        if (selectedTeamPosition >= usersInTeamAdapterList.size()) {
            selectedTeamPosition = 0;
        }
        usersInTeamListView.setAdapter(usersInTeamAdapterList.get(selectedTeamPosition));
        updateUserListCount();
    }

    private void removeTeam() {
        if (teamList.size() != 1) {
            int lastIndex = teamList.size() - 1;
            selectTeam(lastIndex);

            UsersInTeamAdapter userAdapter = usersInTeamAdapterList.get(lastIndex);

            for (int i = userAdapter.getCount() - 1; i >= 0; i--) {
                removeUserFromTeam(i);
            }

            teamList.remove(lastIndex);
            usersInTeamAdapterList.remove(lastIndex);

            adapterCharIncrement--;
            if (selectedTeamPosition >= teamList.size())
                selectedTeamPosition--;
            teamSpinner.setSelection(selectedTeamPosition);
            selectTeam(selectedTeamPosition);
        }
    }

    private void addTeam() {
        if (adapterCharIncrement <= 90) {
            String teamName = String.valueOf((char) adapterCharIncrement);

            Team team = new Team(teamName);

            userGroupSingleton.getCurrentGroup().addTeam(team);
            selectedTeam = team;

            usersInTeamAdapterList.add(new UsersInTeamAdapter(this, new ArrayList<User>()));
            teamList.add(team.getTeamName());

            adapterCharIncrement++;
            selectedTeamPosition++;

            teamSpinner.setSelection(selectedTeamPosition);
            selectTeam(selectedTeamPosition);
        }
    }

    private void setupButtons() {
        cancelButton = (Button) findViewById(R.id.btn_cancel_grouping);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        continueButton = (Button) findViewById(R.id.btn_continue_grouping);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                userGroupSingleton.setCurrentTeam(locateGroupOwnerTeam());
                userGroupSingleton.getCurrentGroup().removeInitTeam();
                Intent intent = new Intent(CreateTeamActivity.this, GroupOverviewActivity.class);
                startActivity(intent);
            }
        });

        addTeamButton = (Button) findViewById(R.id.btn_add_team);
        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTeam();
            }
        });
        removeTeamButton = (Button) findViewById(R.id.btn_remove_team);
        removeTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTeam();
            }
        });
    }

//    private Team locateGroupOwnerTeam() {
//        return null;
//    }

    private void setupGroupInfoText() {
        groupOwner = userGroupSingleton.getCurrentGroup().getGroupOwner().getUserName();
        groupName = userGroupSingleton.getCurrentGroup().getGroupName();

        groupOwnerText = (TextView) findViewById(R.id.txt_groupowner);
        groupNameText = (TextView) findViewById(R.id.txt_groupname);
        groupOwnerText.setText(getString(R.string.lbl_groupowner) + ": " + groupOwner);
        groupNameText.setText(getString(R.string.lbl_groupname) + ": " + groupName);
    }

    private void addUserInTeam(int position) {
        if (userGroupSingleton.getCurrentUser().getUserName().equals(usersInGroupAdapter.getItem(position).getUserName()))
            userGroupSingleton.setCurrentTeam(selectedTeam);
        usersInTeamAdapterList.get(selectedTeamPosition).addUser(usersInGroupAdapter.getItem(position));
        selectedTeam.addTeamMember(usersInGroupAdapter.getItem(position));
        usersInGroupAdapter.removeItem(position);
        Log.d("ASDF", "team: " + selectedTeam.getTeamName() + " size: " + selectedTeam.getTeamMembers().size());
        updateUserListCount();
    }

    private void removeUserFromTeam(int position) {
        if (userGroupSingleton.getCurrentUser().getUserName().equals(usersInTeamAdapterList.get(selectedTeamPosition).getItem(position)))
            userGroupSingleton.setCurrentTeam(null);
        usersInGroupAdapter.addUser(usersInTeamAdapterList.get(selectedTeamPosition).getItem(position));
        selectedTeam.removeTeamMember(position);
        usersInTeamAdapterList.get(selectedTeamPosition).removeItem(position);
        Log.d("ASDF", "team: " + selectedTeam.getTeamName() + " size: " + selectedTeam.getTeamMembers().size());
        updateUserListCount();
    }

    private void updateUserListCount() {
        if (userInTeamText != null && inGroupText != null) {
            userInTeamText.setText(getString(R.string.lbl_inteam) + ": " + usersInTeamAdapterList.get(selectedTeamPosition).getCount());
            inGroupText.setText(getString(R.string.lbl_ingroup) + ": " + usersInGroupAdapter.getCount());
        }
    }
}

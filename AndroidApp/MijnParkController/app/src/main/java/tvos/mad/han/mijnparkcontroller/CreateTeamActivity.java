package tvos.mad.han.mijnparkcontroller;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class CreateTeamActivity extends AppCompatActivity {

    private ListView usersInTeamListView;

    private Spinner teamSpinner;
    private ArrayList<String> teamList;
    private ArrayList<UsersInTeamAdapter> usersInTeamAdapterList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

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
        ArrayList<User> usersInGroupList = setupUsersInGroupList();

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
        selectedTeamPosition = position;
        if (selectedTeamPosition >= usersInTeamAdapterList.size()) {
            selectedTeamPosition = 0;
        }
        usersInTeamListView.setAdapter(usersInTeamAdapterList.get(selectedTeamPosition));
        updateUserListCount();
    }

    /*
    for(int i=65;i<=90;i++) {
            System.out.println((char)i);
        }
     */

    private void removeTeam() {
        if (teamList.size() != 1) {
            teamList.remove(teamList.size() - 1);
            usersInTeamAdapterList.remove(teamList.size() - 1);

            adapterCharIncrement--;
            if (selectedTeamPosition >= teamList.size())
                selectedTeamPosition--;
            teamSpinner.setSelection(selectedTeamPosition);
        }
    }

    private void addTeam() {
        if (adapterCharIncrement <= 90) {
            usersInTeamAdapterList.add(new UsersInTeamAdapter(this, new ArrayList<User>()));
            teamList.add(String.valueOf((char) adapterCharIncrement));

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


    private void setupGroupInfoText() {
        final String groupOwner = getIntent().getExtras().getString("groupowner");
        final String groupName = getIntent().getExtras().getString("groupname");

        groupOwnerText = (TextView) findViewById(R.id.txt_groupowner);
        groupNameText = (TextView) findViewById(R.id.txt_groupname);
        groupOwnerText.setText(getString(R.string.lbl_groupowner) + ": " + groupOwner);
        groupNameText.setText(getString(R.string.lbl_groupname) + ": " + groupName);
    }

    @NonNull
    private ArrayList<User> setupUsersInGroupList() {
        ArrayList<User> usersInGroupList = new ArrayList<>();
        usersInGroupList.add(new User("User1"));
        usersInGroupList.add(new User("User2"));
        usersInGroupList.add(new User("User3"));
        usersInGroupList.add(new User("User4"));
        usersInGroupList.add(new User("User5"));
        usersInGroupList.add(new User("User6"));
        usersInGroupList.add(new User("User7"));
        usersInGroupList.add(new User("User8"));
        usersInGroupList.add(new User("User9"));
        usersInGroupList.add(new User("User10"));
        return usersInGroupList;
    }

    private void addUserInTeam(int position) {
        usersInTeamAdapterList.get(selectedTeamPosition).addUser(usersInGroupAdapter.getItem(position));
        usersInGroupAdapter.removeItem(position);
        updateUserListCount();
    }

    private void removeUserFromTeam(int position) {
        usersInGroupAdapter.addUser(usersInTeamAdapterList.get(selectedTeamPosition).getItem(position));
        usersInTeamAdapterList.get(selectedTeamPosition).removeItem(position);
        updateUserListCount();
    }

    private void updateUserListCount() {
        if (userInTeamText != null && inGroupText != null) {
            userInTeamText.setText(getString(R.string.lbl_inteam) + ": " + usersInTeamAdapterList.get(selectedTeamPosition).getCount());
            inGroupText.setText(getString(R.string.lbl_ingroup) + ": " + usersInGroupAdapter.getCount());
        }
    }
}
package tvos.mad.han.mijnparkcontroller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CreateGroupActivity extends AppCompatActivity {

    private ListView userRequestsListView;
    private UserRequestsAdapter userRequestsAdapter;

    private ListView usersInGroupListView;
    private UsersInGroupAdapter usersInGroupAdapter;

    private String groupOwner;
    private TextView groupOwnerText;
    private String groupName;
    private TextView groupNameText;
    private TextView userRequestsText;

    private TextView inGroupText;
    private Button cancelButton;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        setupGroupInfoText();
        setupButtons();
        setupListAdapters();
        setupUserInfoTexts();

        /*
        TODO: refactor methods; onclick user -> switch from listview
         */
    }

    private void setupUserInfoTexts() {
        userRequestsText = (TextView) findViewById(R.id.lbl_userrequests);
        inGroupText = (TextView) findViewById(R.id.lbl_ingroup);
        updateUserListCount();
    }

    private void setupListAdapters() {
        ArrayList<User> usersInRangeList = setupUsersInRangeList();
        ArrayList<User> usersInGroupList = setupUsersInGroupList();

        userRequestsAdapter = new UserRequestsAdapter(this, usersInRangeList);
        usersInGroupAdapter = new UsersInGroupAdapter(this, usersInGroupList);

        userRequestsListView = (ListView) findViewById(R.id.listview_usersrequests);
        userRequestsListView.setAdapter(userRequestsAdapter);
        userRequestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addUserInGroup(position);
            }
        });

        usersInGroupListView = (ListView) findViewById(R.id.listview_inGroup);
        usersInGroupListView.setAdapter(usersInGroupAdapter);
        usersInGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removeUserFromGroup(position);
            }
        });
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
                createConfirmationDialog();
            }
        });
    }

    private void setupGroupInfoText() {
        groupOwner = getIntent().getExtras().getString("groupowner");
        groupName = getIntent().getExtras().getString("groupname");

        groupNameText = (TextView) findViewById(R.id.txt_groupname);
        groupNameText.setText(getString(R.string.lbl_groupname) + ": " + groupName);
        groupOwnerText = (TextView) findViewById(R.id.txt_groupowner);
        groupOwnerText.setText(getString(R.string.lbl_groupowner) + ": " + groupOwner);
    }

    @NonNull
    private ArrayList<User> setupUsersInGroupList() {
        ArrayList<User> usersInGroupList = new ArrayList<>();
        usersInGroupList.add(new User("User5"));
        usersInGroupList.add(new User("User6"));
        return usersInGroupList;
    }

    @NonNull
    private ArrayList<User> setupUsersInRangeList() {
        ArrayList<User> usersInRangeList = new ArrayList<>();
        usersInRangeList.add(new User("User1"));
        usersInRangeList.add(new User("User2"));
        usersInRangeList.add(new User("User3"));
        usersInRangeList.add(new User("User4"));
        return usersInRangeList;
    }

    private void addUserInGroup(int position) {
        usersInGroupAdapter.addUser(userRequestsAdapter.getItem(position));
        userRequestsAdapter.removeItem(position);
        updateUserListCount();
    }

    private void removeUserFromGroup(int position) {
        userRequestsAdapter.addUser(usersInGroupAdapter.getItem(position));
        usersInGroupAdapter.removeItem(position);
        updateUserListCount();
    }

    private void updateUserListCount() {
        inGroupText.setText(getString(R.string.lbl_ingroup) + ": " + usersInGroupAdapter.getCount());
        userRequestsText.setText(getString(R.string.lbl_userrequests) + ": " + userRequestsListView.getCount());
    }

    private void createConfirmationDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(CreateGroupActivity.this).create();
        alertDialog.setTitle(getString(R.string.confirm_group_title));

        alertDialog.setMessage(getString(R.string.confirm_group_message));

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_button_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CreateGroupActivity.this, CreateTeamActivity.class)
                                .putExtra("groupowner", groupOwner)
                                .putExtra("groupname", groupName);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.dialog_button_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}

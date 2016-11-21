package tvos.mad.han.mijnparkcontroller;

import android.content.DialogInterface;
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

    private ListView usersInRangeListView;
    private UsersInRangeAdapter usersInRangeAdapter;

    private ListView usersInGroupListView;
    private UsersInGroupAdapter usersInGroupAdapter;

    private TextView groupNameText;
    private TextView groupOwnerText;
    private TextView inRangeText;
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
        inRangeText = (TextView) findViewById(R.id.lbl_inrange);
        inRangeText.setText(getString(R.string.lbl_inrange) + ": " + usersInRangeAdapter.getCount());

        inGroupText = (TextView) findViewById(R.id.lbl_ingroup);
        inGroupText.setText(getString(R.string.lbl_ingroup) + ": " + usersInGroupAdapter.getCount());
    }

    private void setupListAdapters() {
        ArrayList<User> usersInRangeList = setupUsersInRangeList();
        ArrayList<User> usersInGroupList = setupUsersInGroupList();

        usersInRangeAdapter = new UsersInRangeAdapter(this, usersInRangeList);
        usersInGroupAdapter = new UsersInGroupAdapter(this, usersInGroupList);

        usersInRangeListView = (ListView) findViewById(R.id.listview_inrange);
        usersInRangeListView.setAdapter(usersInRangeAdapter);
        usersInRangeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addUserInGroup(position);
            }
        });

        usersInGroupListView = (ListView) findViewById(R.id.listview_ingroup);
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

            }
        });
    }

    private void setupGroupInfoText() {
        final String userName = getIntent().getExtras().getString("groupowner");
        final String groupName = getIntent().getExtras().getString("groupname");

        groupNameText = (TextView) findViewById(R.id.txt_groupname);
        groupNameText.setText(getString(R.string.lbl_groupname) + ": " + groupName);
        groupOwnerText = (TextView) findViewById(R.id.txt_groupowner);
        groupOwnerText.setText(getString(R.string.lbl_groupowner) + ": " + userName);
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
        usersInGroupAdapter.addUser(usersInRangeAdapter.getItem(position));
        usersInRangeAdapter.removeItem(position);
    }

    private void removeUserFromGroup(int position) {
        usersInRangeAdapter.addUser(usersInGroupAdapter.getItem(position));
        usersInGroupAdapter.removeItem(position);
    }

    private void createConfirmationDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(CreateGroupActivity.this).create();
        alertDialog.setTitle("Alert");

        alertDialog.setMessage("Option not yet implemented");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}

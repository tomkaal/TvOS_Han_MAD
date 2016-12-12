package tvos.mad.han.mijnparkcontroller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import tvos.mad.han.mijnparkcontroller.model.User;

public class CreateGroupActivity extends AppCompatActivity {

    private UserGroupSingleton userGroupSingleton;
    private SocketSingleton socketSingleton;

    private ListView userRequestsListView;

    private UserRequestsAdapter userRequestsAdapter;
    private ListView usersInGroupListView;

    private UsersInGroupAdapter usersInGroupAdapter;
    private String groupOwner;
    private TextView groupOwnerText;
    private String groupName;
    private TextView groupNameText;

    private String userId;
    private String groupId;

    private TextView userRequestsText;
    private TextView inGroupText;
    private Button cancelButton;
    private Button continueButton;


    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        userGroupSingleton = UserGroupSingleton.getInstance();
        socketSingleton = SocketSingleton.getInstance();

        setupGroupInfoText();
        setupButtons();
        setupListAdapters();
        setupUserInfoTexts();
        createSocketConnection();
        addSocketListeners();

    }

    private void createSocketConnection(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("groupId", groupId);
            jsonObject.put("userName", groupOwner);
            jsonObject.put("groupName", groupName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socketSingleton.emit("add_user_to_group", jsonObject.toString());

    }

    private void setupUserInfoTexts() {
        userRequestsText = (TextView) findViewById(R.id.lbl_userrequests);
        inGroupText = (TextView) findViewById(R.id.lbl_ingroup);
        updateUserListCount();
    }

    private void setupListAdapters() {
        ArrayList<User> usersInRangeList = new ArrayList<>();
        ArrayList<User> usersInGroupList = new ArrayList<>();

        userRequestsAdapter = new UserRequestsAdapter(this, usersInRangeList);
        usersInGroupAdapter = new UsersInGroupAdapter(this, usersInGroupList);

//        for (int i = 1; i <= 8; i++) { // generate users for list
//            addUserGroupRequest("User" + i, "u" + i);
//        }

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

    private void addSocketListeners(){
        socketSingleton.on("user_list_changed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.v("response", "updatedUsers");
                JSONObject jsonObject = (JSONObject) args[0];// wanneer het een object is

                try {
                    String receivedUserName = (String) jsonObject.get("userName");
                    String receivedUserId = (String) jsonObject.get("userId");
                    user = new User(receivedUserId, receivedUserName);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!userRequestsAdapter.containsUser(user) && !usersInGroupAdapter.containsUser(user)){
                                userRequestsAdapter.addUser(user);
                            }
                        }
                    });

                    Log.v("usernameresponse", receivedUserName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private JSONObject createJsonUserObject(String userId, String groupId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("groupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

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
        groupId = getIntent().getExtras().getString("groupId");
        userId = getIntent().getExtras().getString("userId");

        groupNameText = (TextView) findViewById(R.id.txt_groupname);
        groupNameText.setText(getString(R.string.lbl_groupname) + ": " + groupName);
        groupOwnerText = (TextView) findViewById(R.id.txt_groupowner);
        groupOwnerText.setText(getString(R.string.lbl_groupowner) + ": " + groupOwner);

        userGroupSingleton.getCurrentGroup().setGroupId(groupId);
    }

    // Called from Socket
    private void addUserGroupRequest(String userName, String userId) {
        User user = new User(userId, userName);
        userRequestsAdapter.addUser(user);
    }

    // Called from Socket
    private void removeUserGroupRequest(String userId) {
        int position;
        for (position = 0; position < usersInGroupAdapter.getCount(); position++) {
            User user = usersInGroupAdapter.getItem(position);
            if (user.getUserId().equals(userId))
                break;
        }
        usersInGroupAdapter.removeItem(position);
    }

    private void addUserInGroup(int position) {
        User user = userRequestsAdapter.getItem(position);
        usersInGroupAdapter.addUser(user);
        userRequestsAdapter.removeItem(position);
        userGroupSingleton.getCurrentGroup().addUserToGroup(user);
        updateUserListCount();
        JSONObject userObject = createJsonUserObject(user.getUserId(), groupId);
        socketSingleton.emit("join_user_in_acceptance_group", userObject.toString());

    }



    private void removeUserFromGroup(int position) {
        User user = usersInGroupAdapter.getItem(position);
        userRequestsAdapter.addUser(user);
        usersInGroupAdapter.removeItem(position);
        userGroupSingleton.getCurrentGroup().removeUserFromGroup(position);
        updateUserListCount();
        JSONObject userObject = createJsonUserObject(user.getUserId(), groupId);
        socketSingleton.emit("remove_user_in_acceptance_group", userObject.toString());
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
                        socketSingleton.emit("accept_group_organizing", groupId);
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

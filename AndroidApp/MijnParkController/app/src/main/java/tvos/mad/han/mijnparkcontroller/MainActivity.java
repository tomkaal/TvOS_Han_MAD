package tvos.mad.han.mijnparkcontroller;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import tvos.mad.han.mijnparkcontroller.model.Group;
import tvos.mad.han.mijnparkcontroller.model.User;

public class MainActivity extends AppCompatActivity {


    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private UserGroupSingleton userGroupSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                1);

        userGroupSingleton = UserGroupSingleton.getInstance();

        final EditText userNameInput = (EditText) findViewById(R.id.userNameInput);
        final EditText groupOwnerInput = (EditText) findViewById(R.id.groupOwnerInput);
        final EditText groupNameInput = (EditText) findViewById(R.id.groupNameInput);

        Button addUserButton = (Button) findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String userName = userNameInput.getText().toString();

                if (!userName.equals("")) {
                    userGroupSingleton.setCurrentUser(new User(userName));

//                    socketSingleton.emit("Join group", userNameInput.getText().toString());
                    // Do http request, returns a userId and group id

                    Intent intent = new Intent(MainActivity.this, JoinGroup.class)
                            .putExtra("userName", userName)
                            .putExtra("userId", "u22");
                    startActivity(intent);
                } else {
                    createMissingDataDialog("username");
                }
            }
        });

        final Button addGroupButton = (Button) findViewById(R.id.addGroupButton);
        addGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String groupOwnerName = groupOwnerInput.getText().toString();
                String groupName = groupNameInput.getText().toString();


                if (!groupOwnerName.equals("")) {
                    if (!groupName.equals("")) {
                        User groupOwner = new User(groupOwnerName);
                        userGroupSingleton.setCurrentUser(groupOwner);
                        userGroupSingleton.setCurrentGroup(new Group(groupName, groupOwner));


                        String groupId = "aaaaa";
                        String userId = "u1";
                        Intent intent = new Intent(MainActivity.this, CreateGroupActivity.class)
                                .putExtra("groupId", groupId)
                                .putExtra("userId", userId)
                                .putExtra("groupowner", groupOwnerName)
                                .putExtra("groupname", groupName);
                        startActivity(intent);
//                socketSingleton.emit("Create group", groupName.toString());
                    } else {
                        createMissingDataDialog("groupname");
                    }
                } else {
                    createMissingDataDialog("groupowner");
                }
            }
        });


        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userId", "hetUserId1234");
        editor.commit();

        final Button testBeaconButton = (Button) findViewById(R.id.beaconTest);
        testBeaconButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                mSocket.emit("Join group", userNameInput.getText().toString());
                Intent intent = new Intent(MainActivity.this, BeaconSearchActivity.class);
                startActivity(intent);
            }
        });

    }

    private void createMissingDataDialog(String missingField) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");

        switch (missingField) {
            case "username":
                alertDialog.setMessage(getString(R.string.missing_username_message));
                break;
            case "groupowner":
                alertDialog.setMessage(getString(R.string.missing_groupowner_message));
                break;
            case "groupname":
                alertDialog.setMessage(getString(R.string.missing_groupname_message));
                break;
            default:
                alertDialog.setMessage(getString(R.string.misc_error_message));
                break;
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


}

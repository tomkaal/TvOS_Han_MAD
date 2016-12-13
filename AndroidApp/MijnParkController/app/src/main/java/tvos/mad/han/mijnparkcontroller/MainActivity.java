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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import tvos.mad.han.mijnparkcontroller.model.Group;
import tvos.mad.han.mijnparkcontroller.model.User;

public class MainActivity extends AppCompatActivity {

    public static final String API_URL =  "http://10.0.2.2:3000/api";

    /**
     * URL backend:
     * <ipadres>:3000/api/<route> - 10.0.2.2 voor android emulator (http://stackoverflow.com/questions/5495534/java-net-connectexception-localhost-127-0-0-18080-connection-refused)
     *      Routes:
     *          Group:
     *              /group
     *              .get                        (retrieve all)      GET
     *              .post                       (create one)        POST
     *          TV:
     *              /tv/:beaconId               (retrieve one)      GET
     *              /tv                         (create one)        POST
     *          Quiz:
     *              /quiz                       (create quiz)       POST
     *          Question:
     *              /question/answer/:answerId  (answer)            POST
     *          Team:
     *              /team                       (create teams)      POST
     *              /team/score/:teamId         (get score)         GET
     *          User:
     *              /user                       (create one)        POST
     *              /user/answer                (answer question)   POST
     *
     *
     * attributes in url start with '/:'
     *
     * JSON response example:
     * {
	 *   "doc": {		        // Container
	 *		  "key": "value"    // inside container attributes
	 *   },
	 *   "err": null		    // Error handling
     * }
     */

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
                    createUser(userName);
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
                        createUser(groupOwnerName, groupName);

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
        editor.apply();

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

    private void createUser(final String groupOwnerName, final String groupName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", groupOwnerName);
        } catch (Exception ignored) {
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        final String requestBody = jsonObject.toString();

        String BASE_URL = API_URL + "/user";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ASDF", "response: " + response);
                createGroup(groupOwnerName, groupName, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ASDF", "Error fetching JSON data " + error.getMessage());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.e("ASDF", "Unsupported Encoding while trying to get the bytes of %s using %s" +
                            requestBody + "utf-8");
                    return null;
                }
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void createGroup(final String groupOwnerName, String groupName, JSONObject jsonObjectResponse) {
        String ownerId = null;
        try {
            Log.d("ASDF", "jsonObjectResponse: " + jsonObjectResponse);
            JSONObject jsonObject = jsonObjectResponse.getJSONObject("doc");
            ownerId = jsonObject.getString("_id");
        } catch (JSONException ignored) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", groupName);
            jsonObject.put("owner", ownerId);
        } catch (Exception ignored) {
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        final String requestBody = jsonObject.toString();

        String BASE_URL = API_URL + "/group";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ASDF", "response: " + response);
                handleCreateGroupResponse(groupOwnerName, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ASDF", "Error fetching JSON data " + error.getMessage());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.e("ASDF", "Unsupported Encoding while trying to get the bytes of %s using %s" +
                            requestBody + "utf-8");
                    return null;
                }
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void createUser(final String userName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", userName);
        } catch (Exception ignored) {
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        final String requestBody = jsonObject.toString();

        String BASE_URL = API_URL + "/user";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ASDF", "response: " + response);
                handleCreateUserResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ASDF", "Error fetching JSON data " + error.getMessage());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/json; charset=utf-8");
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.e("ASDF", "Unsupported Encoding while trying to get the bytes of %s using %s" +
                            requestBody + "utf-8");
                    return null;
                }
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void handleCreateUserResponse(JSONObject jsonObjectResponse) {
        String userId = null;
        String userName = null;
        try {
            Log.d("ASDF", "jsonObjectResponse: " + jsonObjectResponse);
            JSONObject jsonUser = jsonObjectResponse.getJSONObject("doc");
            userId = jsonUser.getString("_id");
            userName = jsonUser.getString("name");
        } catch (JSONException e) {
            Log.e("ASDF", "JSON error: " + e.getMessage());
        }

        Log.d("ASDF", "userId: " + userId
                + "\nusername: " + userName
        );

        userGroupSingleton.setCurrentUser(new User(userId, userName));
        Intent intent = new Intent(MainActivity.this, JoinGroup.class)
                .putExtra("userId", userId)
                .putExtra("userName", userName);
        startActivity(intent);
    }

    private void handleCreateGroupResponse(String groupOwnerName, JSONObject jsonObjectResponse) {
        String groupId = "aaaaa";
        String userId = "u1";
        String groupName = "group1";
        try {
            Log.d("ASDF", "jsonObjectResponse: " + jsonObjectResponse);
            JSONObject jsonGroup = jsonObjectResponse.getJSONObject("doc");
            groupId = jsonGroup.getString("_id");
            userId = jsonGroup.getString("owner");
            groupName = jsonGroup.getString("name");
        } catch (JSONException e) {
            Log.e("ASDF", "JSON error: " + e.getMessage());
        }

        Log.d("ASDF", "userId: " + userId
                + "\ngroupowner: " + groupOwnerName
                + "\ngroupId: " + groupId
                + "\ngroupname: " + groupName
        );

        User groupOwner = new User(groupOwnerName);
        userGroupSingleton.setCurrentUser(groupOwner);
        userGroupSingleton.setCurrentGroup(new Group(groupName, groupOwner));

        Intent intent = new Intent(MainActivity.this, CreateGroupActivity.class)
                .putExtra("groupId", groupId)
                .putExtra("userId", userId)
                .putExtra("groupowner", groupOwnerName)
                .putExtra("groupname", groupName);
        startActivity(intent);
    }
}

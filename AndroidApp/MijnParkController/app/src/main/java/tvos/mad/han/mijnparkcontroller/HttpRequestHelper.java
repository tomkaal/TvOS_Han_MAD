package tvos.mad.han.mijnparkcontroller;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import tvos.mad.han.mijnparkcontroller.json_model.CreateGroupResponseJSON;
import tvos.mad.han.mijnparkcontroller.model.Team;
import tvos.mad.han.mijnparkcontroller.model.User;

/**
 * Created by DDulos on 06-Dec-16.
 */

public class HttpRequestHelper {

    // TODO: url vervangen
    private static String apiUrl = "http://10.0.2.2:3000/api";

    /*
     * URL backend:
     * <ipadres>:3000/api/<route> - 10.0.2.2 voor android emulator (http://stackoverflow.com/questions/5495534/java-net-connectexception-localhost-127-0-0-18080-connection-refused)
     *      Routes:
     *          Group:
     *              /group
     *              .get                        (retrieve all)
     *              .post                       (create one)
     *          TV:
     *              /tv/:beaconId               (retrieve one)
     *              /tv                         (create one)
     *          Question:
     *              /question/answer/:answerId  (answer)
     *          Team:
     *              /team                       (create teams)
     *              /team/getScore              (get score)
     *          User:
     *              /user                       (create one)
     *
     *
     * attributes start with '/:'
     */

    public static JSONObject sendRequest(Context context, String url, int method, JSONObject jsonObject) {
        RequestQueue queue = Volley.newRequestQueue(context);

        final String requestBody = jsonObject.toString();

        final JSONObject[] responseR = new JSONObject[1];

        String BASE_URL = apiUrl + url;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, BASE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // TODO think of a response return
                responseR[0] = response;
                Log.d("ASDF", "response: " + response);
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
        return responseR[0];
    }

    public static JSONObject sendRequest(Context context, String url, int method) {
        RequestQueue queue = Volley.newRequestQueue(context);

        final JSONObject[] responseR = new JSONObject[1];

        String BASE_URL = apiUrl + url;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, BASE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // TODO think of a response return
                responseR[0] = response;
                Log.d("ASDF", "response: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ASDF", "Error fetching JSON data " + error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);
        return responseR[0];
    }

    public static void createTv(Context context, String beaconId, String description) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("beaconId", beaconId);
            jsonObject.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONObject jsonObjectResponse = sendRequest(context, "/tv", Request.Method.POST, jsonObject);
        Log.d("ASDF", "jsonObjectResponse: " + jsonObjectResponse);
    }

    public static void setTv(Context context, String beaconId) {
        sendRequest(context, "/tv/" + beaconId, Request.Method.GET);
    }

    static CreateGroupResponseJSON createGroup(Context context, String groupName, String ownerName) {
        String ownerId = createUser(context, ownerName);

        JSONObject object = new JSONObject();
        try {
            object.put("name", groupName);
            object.put("owner", ownerId);
        } catch (Exception ignored) {
        }

        JSONObject jsonObject = sendRequest(context, "/group", Request.Method.POST, object);
        String groupId = null;
        try {
            groupId = jsonObject.getString("_id");
        } catch (JSONException ignored) {
        }
        return new CreateGroupResponseJSON(ownerId, groupId);
    }

    static String createUser(Context context, String userName) {
        JSONObject object = new JSONObject();
        try {
            object.put("name", userName);
        } catch (Exception ignored) {
        }

        JSONObject jsonObject = sendRequest(context, "/user", Request.Method.POST, object);
        String userId = null;
        try {
            userId = jsonObject.getString("_id");
        } catch (JSONException ignored) {
        }
        return userId;
    }

    // Done by sockets TODO implement with sockets????
    public static void getGroups(Context context) {
        sendRequest(context, "/group", Request.Method.POST);
    }

    public static void testTeamsForGroup(Context context) {
        String groupId = "584937e06b12781b0876f9ce";
        User user1 = new User("584937456b12781b0876f9cd", "dennis");
        User user2 = new User("584a742ecdb1e007d4b69ddd", "wesley");
        User user3 = new User("584a7433cdb1e007d4b69dde", "rick");
        User user4 = new User("584a743acdb1e007d4b69ddf", "arthur");
        User user5 = new User("584a743ecdb1e007d4b69de0", "tom");
        User user6 = new User("584a7443cdb1e007d4b69de1", "johan");

        ArrayList<Team> teams = new ArrayList<>();

        Team teamA = new Team("A");
        teamA.addTeamMember(user1);
        teamA.addTeamMember(user2);
        teamA.addTeamMember(user3);
        Team teamB = new Team("B");
        teamB.addTeamMember(user4);
        teamB.addTeamMember(user5);
        teamB.addTeamMember(user6);

        teams.add(teamA);
        teams.add(teamB);

        setTeamsOfGroup(context, groupId, teams);
    }

    /*
     *   {
     *       "groupId" : "58453dca2233f72b948a5255",
     *       "teams": [
     *           {
     *               "name": "Kaashaasjes",
     *               "users": [
     *                   {"id": "58453d182233f72b948a5250"},
     *                   {"id": "58453d532233f72b948a5251"}
     *               ]
     *           },
     *           {
     *               "name": "Lotusjes",
     *               "users": [
     *                   {"id": "58453d662233f72b948a5252"},
     *                   {"id": "58453d742233f72b948a5253"},
     *                   {"id": "58453d7d2233f72b948a5254"}
     *               ]
     *           }
     *       ]
     *   }
    */

    private static void setTeamsOfGroup(Context context, String groupId, ArrayList<Team> teams) {
        JSONObject groupJsonObject = new JSONObject();
        try {
            groupJsonObject.put("groupId", groupId);

            JSONArray teamJsonArray = new JSONArray();
            for (Team team : teams) {
                JSONObject teamJsonObject = new JSONObject();
                teamJsonObject.put("name", team.getTeamName());

                JSONArray userJsonArray = new JSONArray();
                for (User user : team.getTeamMembers()) {
                    JSONObject userJsonObject = new JSONObject();
                    userJsonObject.put("id", user.getUserId());
                    userJsonArray.put(userJsonObject);
                }
                teamJsonObject.put("users", userJsonArray);
                teamJsonArray.put(teamJsonObject);
            }
            groupJsonObject.put("teams", teamJsonArray);

        } catch (Exception ex) {
            Log.e("ASDF", "Error occured while generating object");
        }

        Log.d("ASDF", "======JSON======\n"
                + groupJsonObject.toString()
                + "\n====END-JSON===="
        );

        sendRequest(context, "/team", Request.Method.POST, groupJsonObject);
    }

    public static boolean answerQuestion(Context context, String answerId) {
        JSONObject jsonObject = sendRequest(context, "/question/answer/" + answerId, Request.Method.POST);
        if (jsonObject != null)
            return true;
        else
            return false;
    }
}

package tvos.mad.han.mijnparkcontroller;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by DDulos on 06-Dec-16.
 */

public class HttpRequestHelper {
    /*
     * URL backend:
     * ipadres:3000/api/<route>
     *      Routes:
     *          Group:
     *              /group
     *              .get                        (retrieve all)
     *              .post                       (create one)
     *          TVOS:
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
     *
     * https://www.javacodegeeks.com/2011/01/android-json-parsing-gson-tutorial.html
     *
     *  JSON for group with teams:
     *
        {
            "groupId" : "58453dca2233f72b948a5255",
            "teams": [
                {
                    "name": "Kaashaasjes",
                    "users": [
                        {"id": "58453d182233f72b948a5250"},
                        {"id": "58453d532233f72b948a5251"}
                    ]
                },
                {
                    "name": "Lotusjes",
                    "users": [
                        {"id": "58453d662233f72b948a5252"},
                        {"id": "58453d742233f72b948a5253"},
                        {"id": "58453d7d2233f72b948a5254"}
                    ]
                }
            ]
        }
     */


    public void sendRequest(Context context, String[] args) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        // TODO add backend url
        String url = "http://www.google.com";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("ASDF", "Response received: \n" + response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ASDF", "That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}

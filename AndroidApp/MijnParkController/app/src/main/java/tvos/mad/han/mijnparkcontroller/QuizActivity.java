package tvos.mad.han.mijnparkcontroller;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import io.socket.emitter.Emitter;

public class QuizActivity extends AppCompatActivity {

    private ProgressDialog progDialog;

    private RelativeLayout answerLayout;
    private LinearLayout quizLayout;

    private UserGroupSingleton userGroupSingleton;
    private SocketSingleton socketSingleton;

    private String questionId;

    private TextView quizNumberTextView;
    private TextView yourAnswerTextView;
    private TextView correctAnswerTextView;
    private String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        userGroupSingleton = UserGroupSingleton.getInstance();

        addSocketListeners();

        quizNumberTextView = (TextView) findViewById(R.id.txt_quizanswer_title);
        yourAnswerTextView = (TextView) findViewById(R.id.txt_youranswer);
        correctAnswerTextView = (TextView) findViewById(R.id.txt_correctanswer);

        answerLayout = (RelativeLayout) findViewById(R.id.answer_layout);
        quizLayout = (LinearLayout) findViewById(R.id.quiz_layout);

        quizLayout.setVisibility(View.VISIBLE);
        answerLayout.setVisibility(View.GONE);

        Button buttonA = (Button) findViewById(R.id.btn_answer_a);
        Button buttonB = (Button) findViewById(R.id.btn_answer_b);
        Button buttonC = (Button) findViewById(R.id.btn_answer_c);
        Button buttonD = (Button) findViewById(R.id.btn_answer_d);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = String.valueOf(((Button) v).getText());
                createConfirmationDialog();
            }
        };

        buttonA.setOnClickListener(clickListener);
        buttonB.setOnClickListener(clickListener);
        buttonC.setOnClickListener(clickListener);
        buttonD.setOnClickListener(clickListener);
    }

    private void createConfirmationDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(QuizActivity.this).create();
        alertDialog.setTitle(getString(R.string.confirm_group_title));

        alertDialog.setMessage(getString(R.string.confirm_message));

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dialog_button_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        answerQuestion();
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

    private void answerQuestion() {
        Log.d("ASDF", "Answer: " + answer);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("questionId", questionId);
            jsonObject.put("answerId", answer);
            jsonObject.put("userId", userGroupSingleton.getCurrentUser().getUserId());
        } catch (JSONException ignored) {
        }

        progDialog = ProgressDialog.show(this, "Antwoord gegeven", "Verzenden van antwoord...", true, false);

//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        final String requestBody = jsonObject.toString();
//
//        String BASE_URL = MainActivity.API_URL + "/user/answer/";
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d("ASDF", "response: " + response);
//                handleAnswerQuestionResponse(answer, response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("ASDF", "Error fetching JSON data " + error.getMessage());
//            }
//        }) {
//            @Override
//            public String getBodyContentType() {
//                return String.format("application/json; charset=utf-8");
//            }
//
//            @Override
//            public byte[] getBody() {
//                try {
//                    return requestBody == null ? null : requestBody.getBytes("utf-8");
//                } catch (UnsupportedEncodingException uee) {
//                    Log.e("ASDF", "Unsupported Encoding while trying to get the bytes of %s using %s" +
//                            requestBody + "utf-8");
//                    return null;
//                }
//            }
//        };
//        queue.add(jsonObjectRequest);

        // TODO remove when sockets are implemented
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                handleAnswerQuestionResponse(null);

            }
        }, 3000);
    }

    private void handleAnswerQuestionResponse(JSONObject response) {
        Random random = new Random();

        boolean allUsersAnswered = false;
        try {
            JSONObject jsonBoolean = response.getJSONObject("doc");
            allUsersAnswered = jsonBoolean.getBoolean("allUsersAnswered");
        } catch (Exception ignored) {
        }

        allUsersAnswered = random.nextBoolean();

        if (!allUsersAnswered) {
            progDialog.setMessage("Wachten op andere spelers...");

            // TODO remove when sockets are implemented
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    handleAllUsersAnswered();
                }
            }, 3000);
        } else {
            socketSingleton.emit("all_users_answered", userGroupSingleton.getCurrentTeam().getTeamId());

            // TODO remove when sockets are implemented
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    handleAllUsersAnswered();
                }
            }, 3000);
        }
    }

    private void addSocketListeners() {
        socketSingleton = SocketSingleton.getInstance();

        socketSingleton.on("all_users_answered", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleAllUsersAnswered();
            }
        });

        socketSingleton.on("answer_received", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleAnswerReceived();
            }
        });
    }

    private void handleAnswerReceived() {

    }

    private void handleAllUsersAnswered() {
        Random random = new Random();
        final String randomChar = String.valueOf((char) (random.nextInt(68 - 65) + 65));
        Log.d("ASDF", "randomChar: " + randomChar);

        if (answer.equals(randomChar)) {
            setScreenCorrectAnswer();
        } else {
            setScreenWrongAnswer(randomChar);
        }

        answerLayout.setVisibility(View.VISIBLE);
        quizLayout.setVisibility(View.GONE);

        progDialog.dismiss();
        // Actions to do after 10 seconds

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 5000);
    }

    private void setScreenWrongAnswer(String correctAnswer) {
        yourAnswerTextView.setText("Uw antwoord: " + answer);
        correctAnswerTextView.setText("Het antwoord was: " + correctAnswer);
        answerLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
    }

    private void setScreenCorrectAnswer() {
        yourAnswerTextView.setText("Uw antwoord: " + answer);
        correctAnswerTextView.setText("U heeft correct geantwoord");
        answerLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
    }
}

package tvos.mad.han.mijnparkcontroller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private ProgressDialog progDialog;

    private RelativeLayout answerLayout;
    private LinearLayout quizLayout;

    private TextView quizNumberTextView;
    private TextView yourAnswerTextView;
    private TextView correctAnswerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


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
                answerQuestion(String.valueOf(((Button) v).getText()));
            }
        };

        buttonA.setOnClickListener(clickListener);
        buttonB.setOnClickListener(clickListener);
        buttonC.setOnClickListener(clickListener);
        buttonD.setOnClickListener(clickListener);
    }

//    public static boolean answerQuestion (Context context, String answerId){
//        JSONObject jsonObject = sendRequest(context, "/question/answer/" + answerId, Request.Method.POST);
//        if (jsonObject != null)
//            return true;
//        else
//            return false;
//    }

    private void answerQuestion(final String answer) {
        Log.d("ASDF", "Answer: " + answer);

        progDialog = ProgressDialog.show(this, "Quiz beantwoord...", "Wacht op de anderen", true, false);

//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        final String requestBody = jsonObject.toString();
//
//        String BASE_URL = MainActivity.API_URL + "/question/answer/";
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
                handleAnswerQuestionResponse(answer, null);

            }
        }, 3000);
    }

    private void handleAnswerQuestionResponse(String answer, JSONObject response) {
        Random random = new Random();
        final String randomChar = String.valueOf((char) (random.nextInt(68 - 65) + 65));
        Log.d("ASDF", "randomChar: " + randomChar);

        if (answer.equals(randomChar)) {
            setScreenCorrectAnswer(answer);
        } else {
            setScreenWrongAnswer(answer, randomChar);
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

    private void setScreenWrongAnswer(String yourAnswer, String correctAnswer) {
        yourAnswerTextView.setText("Uw antwoord: " + yourAnswer);
        correctAnswerTextView.setText("Het antwoord was: " + correctAnswer);
        answerLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
    }

    private void setScreenCorrectAnswer(String answer) {
        yourAnswerTextView.setText("Uw antwoord: " + answer);
        correctAnswerTextView.setText("U heeft correct geantwoord");
        answerLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
    }
}

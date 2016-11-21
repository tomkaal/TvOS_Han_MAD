package tvos.mad.han.mijnparkcontroller;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class JoinTeam extends AppCompatActivity {

    private ProgressBar progressBar;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String userName = extras.getString("name");
        String group = extras.getString("group");

        progressBar = (ProgressBar) findViewById(R.id.teamProgressBar);
        listView = (ListView) findViewById(R.id.teamListView);
        ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < 15; i++) {
            values.add("Team " + (i + 1));
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                progressBar.setVisibility(View.GONE);
                listView.setAdapter(adapter);

            }
        }, 3000);
    }
}

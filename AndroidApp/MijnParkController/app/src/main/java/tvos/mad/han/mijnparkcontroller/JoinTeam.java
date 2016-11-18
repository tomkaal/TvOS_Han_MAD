package tvos.mad.han.mijnparkcontroller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class JoinTeam extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_team);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String userName = extras.getString("name");
        String group = extras.getString("group");

    }
}

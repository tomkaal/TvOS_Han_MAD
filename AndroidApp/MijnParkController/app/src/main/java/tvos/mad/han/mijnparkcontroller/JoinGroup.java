package tvos.mad.han.mijnparkcontroller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class JoinGroup extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        final String userName = getIntent().getExtras().getString("name");
        Log.v("Username", userName);

        updateGreetingMessageWithUserName(userName);
// 1. Instantiate an AlertDialog.Builder with its constructor


        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.groupListView);
        ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < 15; i++) {
            values.add("Klas " + (i + 1));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(position);
                Intent intent = new Intent(JoinGroup.this, JoinTeam.class);
                Bundle extras = new Bundle();
                extras.putString("name", userName);
                extras.putString("group", itemValue);
                intent.putExtras(extras);

                startActivity(intent);
                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();
            }

        });
    }

    public void updateGreetingMessageWithUserName(String userName) {
        TextView greetText = (TextView) findViewById(R.id.joinGroupGreetText);
        greetText.setText("Dag " + userName + ", selecteer een groep");
    }

}

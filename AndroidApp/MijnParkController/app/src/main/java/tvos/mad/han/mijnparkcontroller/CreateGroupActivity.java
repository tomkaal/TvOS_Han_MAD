package tvos.mad.han.mijnparkcontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CreateGroupActivity extends AppCompatActivity {

    private ListView usersInRangeListView;
    private UsersInRangeAdapter usersInRangeAdapter;

    private ListView usersInGroupListView;
    private UsersInGroupAdapter usersInGroupAdapter;


    private String groupName = "slb-2b";
    private String groupOwner = "Owner";
    private TextView groupNameText;
    private TextView groupOwnerText;
    private TextView inRangeText;
    private TextView inGroupText;

    private Button cancelButton;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);


        groupNameText = (TextView) findViewById(R.id.txt_groupname);
        groupNameText.setText(getString(R.string.lbl_groupname) + ": " + groupName);
        groupOwnerText = (TextView) findViewById(R.id.txt_groupowner);
        groupOwnerText.setText(getString(R.string.lbl_groupowner) + ": " + groupOwner);

        cancelButton = (Button) findViewById(R.id.btn_cancel_grouping);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        continueButton = (Button) findViewById(R.id.btn_continue_grouping);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        usersInGroupListView = (ListView) findViewById(R.id.listview_ingroup);
        usersInRangeListView = (ListView) findViewById(R.id.listview_inrange);

        ArrayList<User> usersInRangeList = new ArrayList<>();
        usersInRangeList.add(new User("User1"));
        usersInRangeList.add(new User("User2"));
        usersInRangeList.add(new User("User3"));
        usersInRangeList.add(new User("User4"));
        ArrayList<User> usersInGroupList = new ArrayList<>();
        usersInGroupList.add(new User("User5"));
        usersInGroupList.add(new User("User6"));

        usersInGroupAdapter = new UsersInGroupAdapter(this, usersInGroupList);
        usersInRangeAdapter = new UsersInRangeAdapter(this, usersInRangeList);

        inRangeText = (TextView) findViewById(R.id.lbl_inrange);
        inRangeText.setText(getString(R.string.lbl_inrange) + ": " + usersInRangeAdapter.getCount());

        inGroupText = (TextView) findViewById(R.id.lbl_ingroup);
        inGroupText.setText(getString(R.string.lbl_ingroup) + ": " + groupOwner);

        /*
        TODO: refactor methods; onclick user -> switch from listview
         */
    }
}

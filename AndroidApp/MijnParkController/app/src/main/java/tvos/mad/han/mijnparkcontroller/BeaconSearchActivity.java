package tvos.mad.han.mijnparkcontroller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.socket.emitter.Emitter;
import tvos.mad.han.mijnparkcontroller.model.CostumBeacon;

public class BeaconSearchActivity extends AppCompatActivity {

    public Region region;
    public BeaconManager beaconManager;
    private LinearLayout linearLayout;
    private TextView textView;
    private CostumBeacon costumBeacon;
    private UserGroupSingleton userGroupSingleton;
    private SocketSingleton socketSingleton;

    private int lastMinor;
    private int lastMajor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_search);

        beaconManager = new BeaconManager(this);
        linearLayout = (LinearLayout) findViewById(R.id.testLayoutToChange);
        textView = (TextView) findViewById(R.id.colorTextView);
        userGroupSingleton = UserGroupSingleton.getInstance();

        addSocketListeners();

        final ArrayList<CostumBeacon> beaconList = new ArrayList<CostumBeacon>();
        beaconList.add(new CostumBeacon(13348, 21514, "Yellow"));
        beaconList.add(new CostumBeacon(15803, 26551, "Candy"));

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    boolean isSet = false;
                    for (int i = 0; i < list.size(); i++) {
                        int major = list.get(i).getMajor();
                        int minor = list.get(i).getMinor();
                        String beaconKey = String.format("%d:%d", major, minor);
//                        if(major == 13348 && minor == 21514){
//                            Log.v("beacons", "Candy " + beaconKey);
//                            if(!isSet) {
//                                isSet = true;
//                                linearLayout.setBackgroundColor(Color.parseColor("#FFBAD2"));
//                                textView.setText("Candy");
//                            }
//
//                        } else if(major == 15803 && minor == 26551){
//                            Log.v("beacons", "Lime " + beaconKey);
//                            if(!isSet){
//                                isSet = true;
//                                linearLayout.setBackgroundColor(Color.parseColor("yellow"));
//                                textView.setText("Yellow");
//                            }
//                        } else {
//                            Log.v("beacons", beaconKey);
//                        }
                        Log.v("beacons", beaconKey);

                        for (int beaconCount = 0; beaconCount < beaconList.size(); beaconCount++) {

                            if (major == beaconList.get(beaconCount).major && minor == beaconList.get(beaconCount).minor) {
                                linearLayout.setBackgroundColor(Color.parseColor(getColorCode(beaconList.get(beaconCount).beaconName)));
                                textView.setText(beaconList.get(beaconCount).beaconName);
                                if(major != lastMajor && minor != lastMinor){
                                    sendNotifyTv();
                                }
                                lastMajor = major;
                                lastMinor = minor;
                            }
                        }

                    }
                    Log.v("beacons", "-------------------------");

                }
            }
        });
        region = new Region("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);


    }

    private void addSocketListeners(){
        socketSingleton = SocketSingleton.getInstance();

        socketSingleton.on("tv_notified", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String message = (String) args[0];
                Log.v("TV_Notification", "Tv Notified");
                Intent intent = new Intent(BeaconSearchActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendNotifyTv(){
//        String teamId = userGrotv_upSingleton.getCurrentTeam().getTeamId();
        String teamId = "fakeTeamId";
        Log.v("Emit event", "Emit notify_tv with teamId");
        socketSingleton.emit("notify_tv", teamId);

    }

    private String getColorCode(String colorName) {
        switch (colorName) {
            case "Yellow":
                return "yellow";
            case "Candy":
                return "#FFBAD2";
            default:
                return "white";
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    @Override
    protected void onStop() {
        beaconManager.stopRanging(region);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        beaconManager.stopRanging(region);
        super.onDestroy();
    }
}

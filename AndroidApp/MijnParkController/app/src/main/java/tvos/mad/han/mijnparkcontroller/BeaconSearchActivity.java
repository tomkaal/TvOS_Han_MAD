package tvos.mad.han.mijnparkcontroller;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import tvos.mad.han.mijnparkcontroller.model.CostumBeacon;

public class BeaconSearchActivity extends AppCompatActivity {

    public Region region;
    public BeaconManager beaconManager;
    private LinearLayout linearLayout;
    private TextView textView;
    private CostumBeacon costumBeacon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_search);

        beaconManager = new BeaconManager(this);
        linearLayout = (LinearLayout) findViewById(R.id.testLayoutToChange);
        textView = (TextView) findViewById(R.id.colorTextView);

        final ArrayList<CostumBeacon> beaconList = new ArrayList<CostumBeacon>();
        beaconList.add(new CostumBeacon(13348, 21514, "Yellow" ));
        beaconList.add(new CostumBeacon(15803, 26551, "Candy" ));



        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    boolean isSet = false;
                    for (int i = 0; i < list.size(); i ++){
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

                        for (int beaconCount = 0; beaconCount < beaconList.size(); beaconCount ++){

                            if(major == beaconList.get(beaconCount).major && minor == beaconList.get(beaconCount).minor){
                                linearLayout.setBackgroundColor(Color.parseColor(getColorCode(beaconList.get(beaconCount).beaconName)));
                                textView.setText(beaconList.get(beaconCount).beaconName);
                            }
                        }

                    }
                    Log.v("beacons", "-------------------------");

                }
            }
        });
        region = new Region("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

    }

    private String getColorCode(String colorName){
        switch (colorName){
            case "Yellow": return "yellow";
            case "Candy": return "#FFBAD2";
            default: return "white";
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
}

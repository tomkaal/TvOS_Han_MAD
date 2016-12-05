package tvos.mad.han.mijnparkcontroller.model;

/**
 * Created by tommi on 21-11-2016.
 */
public class CostumBeacon {

    public String beaconName;
    public int major, minor;

    public CostumBeacon(int major, int minor, String beaconName){
        this.major = major;
        this.minor = minor;
        this.beaconName = beaconName;
    }
}

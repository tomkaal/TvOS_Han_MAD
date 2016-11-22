package tvos.mad.han.mijnparkcontroller;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by DDulos on 22-Nov-16.
 */

public class Group {
    private String groupId;
    private String groupName;
    private User groupOwner;
    private ArrayList<Team> teams;

    public Group(String groupName, User groupOwner){
        this.groupName = groupName;
        this.groupOwner = groupOwner;
        teams = new ArrayList<>();
        teams.add(new Team("0"));
        Log.v("ASDF", "Connect");
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public User getGroupOwner() {
        return groupOwner;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void addUserToGroup(User user) {
        getTeams().get(0).addTeamMember(user);
    }

    public void removeUserFromGroup(User user) {
        getTeams().get(0).removeTeamMember(user);
    }

    public void removeUserFromGroup(int position) {
        getTeams().get(0).removeTeamMember(position);
    }

}

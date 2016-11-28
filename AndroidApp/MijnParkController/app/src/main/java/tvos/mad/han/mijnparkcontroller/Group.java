package tvos.mad.han.mijnparkcontroller;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by DDulos on 22-Nov-16.
 */

public class Group {
    public static final String INIT_TEAM_NAME = "0";
    private String groupId;
    private String groupName;
    private User groupOwner;
    private ArrayList<Team> teams;

    public Group(String groupName, User groupOwner) {
        this.groupName = groupName;
        this.groupOwner = groupOwner;
        teams = new ArrayList<>();
        teams.add(new Team(INIT_TEAM_NAME));
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

    public void setTeams() {
        this.teams = teams;
    }

    public void addTeam(String teamName) {
        teams.add(new Team(teamName));
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

    public void removeInitTeam() {
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            if (team.getTeamName().equals(INIT_TEAM_NAME)) {
                teams.remove(i);
                break;
            }
        }
    }
}

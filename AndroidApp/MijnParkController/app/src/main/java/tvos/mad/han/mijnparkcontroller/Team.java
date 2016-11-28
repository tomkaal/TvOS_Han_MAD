package tvos.mad.han.mijnparkcontroller;

import java.util.ArrayList;

/**
 * Created by DDulos on 22-Nov-16.
 */
public class Team {
    private String teamName;
    private ArrayList<User> teamMembers;
    private int teamPoints;

    public Team(String teamName) {
        this.teamName = teamName;
        teamMembers = new ArrayList<>();

        teamPoints = 0;
    }

    public void removeTeamMember(User teamMember) {
        teamMembers.remove(teamMember);
    }

    public void removeTeamMember(int position) {
        teamMembers.remove(position);
    }

    public void addTeamMember(User teamMember) {
        teamMembers.add(teamMember);
    }

    public ArrayList<User> getTeamMembers() {
        return teamMembers;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getTeamPoints() {
        return teamPoints;
    }

    public void setTeamPoints(int teamPoints) {
        this.teamPoints = teamPoints;
    }
}

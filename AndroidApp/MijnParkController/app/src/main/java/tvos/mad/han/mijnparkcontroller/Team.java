package tvos.mad.han.mijnparkcontroller;

import java.util.ArrayList;

/**
 * Created by DDulos on 22-Nov-16.
 */
public class Team {
    private String teamName;
    private ArrayList<User> teamMembers;

    public Team(String teamName) {
        this.teamName = teamName;
        teamMembers = new ArrayList<>();
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
}

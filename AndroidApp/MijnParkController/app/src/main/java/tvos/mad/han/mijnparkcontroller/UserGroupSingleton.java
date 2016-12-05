package tvos.mad.han.mijnparkcontroller;

import tvos.mad.han.mijnparkcontroller.model.Group;
import tvos.mad.han.mijnparkcontroller.model.Team;
import tvos.mad.han.mijnparkcontroller.model.User;

/**
 * Created by DDulos on 22-Nov-16.
 */

public class UserGroupSingleton {
    private static UserGroupSingleton instance;
    private Group currentGroup;
    private Team currentTeam;
    private User currentUser;

    private UserGroupSingleton() {
    }

    static UserGroupSingleton getInstance() {
        return (instance == null) ? instance = new UserGroupSingleton() : instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Group group) {
        this.currentGroup = group;
    }

    public Team getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(Team currentTeam) {
        this.currentTeam = currentTeam;
    }
}

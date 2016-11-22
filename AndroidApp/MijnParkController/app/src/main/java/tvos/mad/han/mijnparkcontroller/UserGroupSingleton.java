package tvos.mad.han.mijnparkcontroller;

/**
 * Created by DDulos on 22-Nov-16.
 */

public class UserGroupSingleton {
    private static UserGroupSingleton instance;
    private Group currentGroup;
    private User currentUser;

    public UserGroupSingleton() {
    }

    static UserGroupSingleton getInstance(){
        return (instance == null) ? instance = new UserGroupSingleton() : instance;
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(Group group) {
        this.currentGroup = group;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}

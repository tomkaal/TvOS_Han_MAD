package tvos.mad.han.mijnparkcontroller;

/**
 * Created by DDulos on 18-Nov-16.
 */

public class User {
    private String userId;
    private String userName;

    public User(String userName) {
        this.userName = userName;
    }

    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

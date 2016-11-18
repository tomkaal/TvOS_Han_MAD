package tvos.mad.han.mijnparkcontroller;

/**
 * Created by DDulos on 18-Nov-16.
 */

public class User {
    private String userId;
    private String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

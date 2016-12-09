package tvos.mad.han.mijnparkcontroller.json_model;

/**
 * Created by DDulos on 06-Dec-16.
 */

public class CreateGroupResponseJSON {
    public String ownerId;
    public String groupId;

    public CreateGroupResponseJSON(String ownerId, String groupId) {
        this.ownerId = ownerId;
        this.groupId = groupId;
    }
}

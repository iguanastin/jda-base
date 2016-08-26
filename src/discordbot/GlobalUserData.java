package discordbot;

import net.dv8tion.jda.entities.User;
import org.json.JSONObject;

/**
 *
 * @author austinbt
 */
public class GlobalUserData extends ServerData {
    private String id;
    
    /**
     *
     * @param user
     */
    public GlobalUserData(User user) {
        id = user.getId();
    }
    
    /**
     *
     * @param json
     */
    public GlobalUserData(JSONObject json) {
        super(json);
    }

    /**
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return
     */
    @Override
    public JSONObject saveState() {
        JSONObject json = new JSONObject();
        
        json.append("id", id);
        
        return json;
    }

    /**
     *
     * @param json
     */
    @Override
    public void loadState(JSONObject json) {
        id = json.getString("id");
    }
}

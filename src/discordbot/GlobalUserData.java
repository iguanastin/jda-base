package discordbot;

import net.dv8tion.jda.entities.User;
import org.json.JSONObject;

/**
 * A simple data class that describes all data for a given User that should be maintained between guilds that both this bot and the user are in.
 * 
 * @author austinbt
 */
public class GlobalUserData extends ServerData {
    
    private String id;
    
    /**
     * Constructs this data object with a given User
     * 
     * @param user User to construct this object with
     */
    public GlobalUserData(User user) {
        id = user.getId();
    }
    
    /**
     * Constructs this data object with a given JSON.
     * 
     * This JSON is assumed to be properly formatted for this object.
     * 
     * @param json JSONObject to construct this object from
     */
    public GlobalUserData(JSONObject json) {
        super(json);
    }

    /**
     * 
     * @return The Discord UserID for this User
     */
    public String getId() {
        return id;
    }

    /**
     * Saves this object's current state to a JSON
     * 
     * @return JSON representation of this object
     */
    @Override
    public JSONObject saveState() {
        JSONObject json = new JSONObject();
        
        json.append("id", id);
        
        return json;
    }

    /**
     * Loads this object from a JSON representation of it.
     * 
     * JSON is assumed to be properly formatted for this object.
     * 
     * @param json JSON to retrieve data from
     */
    @Override
    public void loadState(JSONObject json) {
        id = json.getString("id");
    }
}

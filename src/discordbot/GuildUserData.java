package discordbot;

import net.dv8tion.jda.entities.User;
import org.json.JSONObject;

/**
 * An object intended for storing user information that is unique to a certain Guild
 * 
 * @author austinbt
 */
public class GuildUserData extends ServerData {

    private String id;
    
    /**
     * Initializes data from a given User
     * 
     * @param user User to associate with this data
     */
    public GuildUserData(User user) {
        id = user.getId();
    }

    /**
     * Loads data into this object from a given JSONObject.
     * 
     * *JSON IS EXPECTED TO BE PROPER FORMAT*
     * 
     * @param json JSONObject to load data from
     */
    public GuildUserData(JSONObject json) {
        super(json);
    }

    /**
     * 
     * @return The Discord UserID associated with this data
     */
    public String getId() {
        return id;
    }
    
    /**
     * Saves the current state of this object to a JSONObject to be saved to file. Can be loaded with 'loadState(JSONObject)'
     * 
     * @return A JSONObject representation of the current state of this object
     */
    @Override
    public JSONObject saveState() {
        JSONObject json = new JSONObject();
        
        json.append("id", id);
        
        return json;
    }

    /**
     * Loads data into this object from a given JSONObject
     * 
     * *JSON IS EXPECTED TO BE PROPER FORMAT*
     * 
     * @param json JSONObject to load from
     */
    @Override
    public void loadState(JSONObject json) {
        id = json.getString("id");
    }
    
}

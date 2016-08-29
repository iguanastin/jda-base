package discordbot;

import java.util.ArrayList;
import net.dv8tion.jda.entities.Guild;
import org.json.JSONObject;

/**
 * A simple data-holding object that holds data that is unique to guilds. Also contains user-specific data.
 * 
 * ***TODO***
 * Create functionality to allow control of GuildUserData objects
 * ***TODO***
 * 
 * @author austinbt
 */
public class GuildData extends ServerData {

    private ArrayList<GuildUserData> userData = new ArrayList();
    private String id;

    /**
     * Initializes this object from a Guild
     * 
     * @param guild Guild to initialize from
     */
    public GuildData(Guild guild) {
        id = guild.getId();
    }

    /**
     * Initializes this object with a JSONObject representation of it
     * 
     * *JSON IS EXPECTED TO BE PROPER FORMAT*
     * 
     * @param json JSONObject to initialize from
     */
    public GuildData(JSONObject json) {
        super(json);
    }
    
    /**
     * 
     * @return The Discord GuildID associated with this object
     */
    public String getId() {
        return id;
    }
    
    /**
     * Creates a JSONObject representation of this object in its current state
     * 
     * @return A JSONObject representation of this object
     */
    @Override
    public JSONObject saveState() {
        JSONObject json = new JSONObject();
        
        json.append("id", id);
        
        for (GuildUserData data : userData) {
            json.append("userData", data.saveState());
        }
        
        return json;
    }

    /**
     * Loads data into this object from a JSONObject
     * 
     * *JSON IS EXPECTED TO BE IN PROPER FORMAT*
     * 
     * @param json JSONObject to load from
     */
    @Override
    public void loadState(JSONObject json) {
        userData.clear();
        
        id = json.getString("id");
        
        if (json.has("userData")) {
            json.getJSONArray("userData").forEach((Object t) -> {
                JSONObject obj = (JSONObject) t;
                
                userData.add(new GuildUserData(obj));
            });
        }
    }
    
}

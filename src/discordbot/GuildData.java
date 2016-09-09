package discordbot;

import java.util.ArrayList;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
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
     * Initializes this object with a JSONObject representation of it.
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
     * Retrieves userdata for this guild if it exists
     * 
     * @param userId User id to find data for
     * @return GuildUserData object for the given user. Null if user is null.
     */
    public GuildUserData getUserData(String userId) {
        if (userId == null) {
            return null;
        }
        
        //Find data if it exists
        for (GuildUserData data : userData) {
            if (data.getId().equals(userId)) {
                return data;
            }
        }
        
        return null;
    }
    
    /**
     * Retrieves userdata for this guild if it exists
     * 
     * @param user User to find data for
     * @return GuildUserData object for the given user. Null if user is null.
     */
    public GuildUserData getUserData(User user) {
        return getUserData(user.getId());
    }
    
    /**
     * Finds if data for a given user is present in this server
     * 
     * @param userId Id of user to compare
     * @return true if userdata is present for given id, false otherwise
     */
    public boolean hasUserData(String userId) {
        for (GuildUserData data : userData) {
            if (data.getId().equals(userId)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Finds if data for a given user is present in this server
     * 
     * @param user User to compare
     * @return true if userdata is present for given user, false otherwise
     */
    public boolean hasUserData(User user) {
        return hasUserData(user.getId());
    }
    
    /**
     * Adds a give userdata to this guild if it is not already present.
     * 
     * @param data Data to be added
     * @return true if successfully added, false if already present
     */
    public boolean addUserData(GuildUserData data) {
        if (data != null && !userData.contains(data)) {
            userData.add(data);
            return true;
        }
        
        return false;
    }
    
    /**
     * Removes userdata if it is present
     * 
     * @param userId User id to find data for
     * @return Userdata for the given user id if it exists and has been removed. Null if data was not present, no action was taken.
     */
    public GuildUserData removeUserData(String userId) {
        if (!hasUserData(userId)) {
            return null;
        }
        
        GuildUserData data = getUserData(userId);
        userData.remove(data);
        return data;
    }
    
    /**
     * Removes userdata if it is present
     * 
     * @param user User to find data for
     * @return Userdata for the given user if it exists and has been removed. Null if data was not present, no action was taken.
     */
    public GuildUserData removeUserData(User user) {
        return removeUserData(user.getId());
    }
    
    /**
     * Creates userdata for the given user and adds it to this guild
     * 
     * @param user User to create data for and add
     * @return true if userdata was created and added, false if data for user was already present
     */
    public boolean createUserData(User user) {
        if (hasUserData(user)) {
            return false;
        }
        
        addUserData(new GuildUserData(user));
        
        return true;
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

package discordbot;

import org.json.JSONObject;

/**
 * A general superclass to provide a basic framework for all server database data.
 * 
 * @author austinbt
 */
public abstract class ServerData {
    
    /**
     * Construct this data from a JSON representation of it.
     * 
     * JSON is expected to be in proper format.
     * 
     * @param json JSON To build from
     */
    public ServerData(JSONObject json) {
        loadState(json);
    }
    
    /**
     * Default constructor
     */
    public ServerData() {
        //Do nothing
    }
    
    /**
     * Save the current state of this data to a JSON representation
     * 
     * **MUST BE IMPLEMENTED BY SUBCLASSES**
     * @return A JSON representation of this data
     */
    public JSONObject saveState() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    /**
     * Loads this data from a JSON representation of it.
     * 
     * JSON is expected to be in proper format.
     * 
     * **MUST BE IMPLEMENTED BY SUBCLASSES**
     * @param json The JSON representation to build from
     */
    public void loadState(JSONObject json) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

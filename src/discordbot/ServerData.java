package discordbot;

import org.json.JSONObject;

/**
 *
 * @author austinbt
 */
public abstract class ServerData {
    
    /**
     *
     * @param json
     */
    public ServerData(JSONObject json) {
        loadState(json);
    }
    
    /**
     *
     */
    public ServerData() {
        //Do nothing
    }
    
    /**
     *
     * @return
     */
    public JSONObject saveState() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    /**
     *
     * @param json
     */
    public void loadState(JSONObject json) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

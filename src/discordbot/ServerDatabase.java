package discordbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import org.json.JSONObject;

/**
 * A simple database that stores GuildData and GlobalUserData with no duplicates
 *
 * @author austinbt
 */
public class ServerDatabase {

    private final ArrayList<GuildData> guildData = new ArrayList();
    private final ArrayList<GlobalUserData> globalUserData = new ArrayList();

    /**
     * Creates and attempts to load the database from a given JSON data file
     *
     * @param dataFile Path to database data file
     * @throws java.io.FileNotFoundException
     */
    public ServerDatabase(String dataFile) throws FileNotFoundException {
        loadDatabase(dataFile);
    }
    
    /**
     * Creates an empty database
     */
    public ServerDatabase() {
    }

    /**
     * Attempts to retrieve a GlobalUserData based on the given Discord UserID
     * string
     *
     * @param id Discord UserID to match
     * @return The GlobalUserData associated with the given ID. Null if no such
     * data exists
     */
    public GlobalUserData getGlobalUserData(String id) {
        for (GlobalUserData data : globalUserData) {
            if (data.getId().equals(id)) {
                return data;
            }
        }

        return null;
    }

    /**
     * Attempts to retrieve a GlobalUserData in the database from a given User
     *
     * @param user User to match with GlobalUserData
     * @return The GlobalUserData attached to the User. Null if no such
     * GlobalUserData exists
     */
    public GlobalUserData getGlobalUserData(User user) {
        return getGlobalUserData(user.getId());
    }

    /**
     * Attempts to create and add a GlobalUserData if there is not already one
     *
     * @param user User to create GlobalUserData from
     * @return True if successfully added, false otherwise
     */
    public boolean addGlobalUser(User user) {
        if (getGlobalUserData(user) == null) {
            globalUserData.add(new GlobalUserData(user));

            return true;
        }

        return false;
    }

    /**
     * Attempts to add a given GlobalUserData if it is not already in the
     * database
     *
     * @param data GlobalUserData to be added
     * @return True if successful, false otherwise
     */
    public boolean addGlobalUserData(GlobalUserData data) {
        if (getGlobalUserData(data.getId()) == null) {
            globalUserData.add(data);

            return true;
        }

        return false;
    }

    /**
     * Attempts to remove a given GlobalUserData if it exists.
     *
     * @param id Discord UserID to be matched
     * @return The GlobalUserData that was removed. Null if not found
     */
    public GlobalUserData removeGlobalUserData(String id) {
        GlobalUserData data;
        if ((data = getGlobalUserData(id)) != null) {
            globalUserData.remove(data);

            return data;
        }

        return null;
    }

    /**
     * Attempts to retrieve GuildData for a guild with a given Discord GuildID
     *
     * @param id Discord GuildID to be matched to GuildData
     * @return GuildData for the given Discord GuildID if it exists
     */
    public GuildData getGuildData(String id) {
        for (GuildData data : guildData) {
            if (data.getId().equals(id)) {
                return data;
            }
        }

        return null;
    }

    /**
     * Attempts to retrieve GuildData for a given Guild
     *
     * @param guild Guild to match ID to
     * @return Null if no such GuildData found, otherwise GuildData for the
     * given Guild
     */
    public GuildData getGuildData(Guild guild) {
        return getGuildData(guild.getId());
    }

    /**
     * Attempts to create GuildData and add it to the database.
     *
     * @param guild Guild to build GuildData from
     * @return False if GuildData with the same Discord GuildID exists, true if
     * successfully added.
     */
    public boolean addGuild(Guild guild) {
        if (getGuildData(guild) == null) {
            guildData.add(new GuildData(guild));

            return true;
        }

        return false;
    }

    /**
     * Attempts to add guild data to the database.
     *
     * @param data Data to be added
     * @return False if there is already data with the same Discord GuildID,
     * true when successfully added.
     */
    public boolean addGuildData(GuildData data) {
        if (getGuildData(data.getId()) != null) {
            guildData.add(data);

            return true;
        }

        return false;
    }

    /**
     * Attempts to remove data for a given guild.
     *
     * @param id Discord GuildID representing the guild
     * @return The data that was removed if it exists. Null otherwise.
     */
    public GuildData removeGuild(String id) {
        GuildData data;
        if ((data = getGuildData(id)) != null) {
            guildData.remove(data);
            return data;
        }

        return null;
    }

    /**
     * Attempts to remove GuildData associated with the given Guild
     * 
     * @param guild Guild to match to data
     * @return GuildData of the associated Guild if it was removed, null if it did not exist
     */
    public GuildData removeGuild(Guild guild) {
        return removeGuild(guild.getId());
    }

    /**
     * Loads data into this object from a JSON string read from the given file.
     * All data is cleared before loading.
     *
     * @param dataFile Path to file to be loaded from
     * @throws java.io.FileNotFoundException
     */
    public void loadDatabase(String dataFile) throws FileNotFoundException {
        clear();

        //Load JSON from file
        String jsonString = "";
        Scanner scan = new Scanner(new File(dataFile));
        while (scan.hasNextLine()) {
            jsonString += scan.nextLine();
        }
        scan.close();

        //Load data from JSON
        JSONObject json = new JSONObject(jsonString);

        if (json.has("guildData")) {
            json.getJSONArray("guildData").forEach((Object t) -> {
                JSONObject data = (JSONObject) t;
                addGuildData(new GuildData(json));
            });
        }

        if (json.has("globalUserData")) {
            json.getJSONArray("globalUserData").forEach((Object t) -> {
                JSONObject data = (JSONObject) t;
                addGlobalUserData(new GlobalUserData(data));
            });
        }
    }

    /**
     * Clears the contents this database
     */
    public void clear() {
        guildData.clear();
        globalUserData.clear();
    }

    /**
     * Saves a JSON representation of the database in it's current state to a
     * specified file. If the file already exists, it will be renamed to
     * "[dataFile].old".
     *
     * @param dataFile The path to the file to be saved
     */
    public void saveDatabase(String dataFile) {
        JSONObject json = new JSONObject();

        //Build JSON
        for (GuildData data : guildData) {
            json.append("guildData", data);
        }
        for (GlobalUserData data : globalUserData) {
            json.append("globalUserData", data);
        }

        try {
            //Backup current file if it exists
            File file = new File(dataFile);
            if (file.exists()) {
                File moveTo = new File(dataFile + ".old");
                if (moveTo.exists()) {
                    moveTo.delete();
                }

                file.renameTo(moveTo);
            }
            file = new File(dataFile);

            //Save to file
            PrintWriter writer = new PrintWriter(file);

            writer.println(json.toString());

            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

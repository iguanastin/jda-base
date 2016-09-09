package discordbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple Discord Bot framework with minimal implementation. Intended to be used as a base to build on.
 * 
 * @author austinbt
 */
public class DiscordBot extends ListenerAdapter {

    private static final String BOT_CONFIG_FILE = "bot.cfg";
    
    private static String TOKEN = "INVALID";
    private static char COMMAND = '!';
    private static String DATABASE_SAVE_FILE = "bot.db";
    private static long DATABASE_SAVE_RATE_SECONDS = 60;
    
    /**
     * The name of this bot
     */
    public static String BOT_NAME = "Default_Bot_Name";
    
    private static String JOIN_MESSAGE = "Ayy";
    private static String HELP_MESSAGE = "**Help:**\n```Nothing to help with!```";
    private static String INFO_MESSAGE = "**Info**\n```Info about bot goes here```";
    
    private ServerDatabase database;
    private Timer databaseSaver;
    
    /**
     * Builds a JDA instance and adds an instance of this class as an EventListener
     * 
     * @param args Arguments have no effect
     */
    public static void main(String[] args) {
        try {
            JDA jda = new JDABuilder().setBotToken(TOKEN).buildBlocking();
            jda.addEventListener(new DiscordBot());
        } catch (LoginException | IllegalArgumentException | InterruptedException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initializes bot configuration
     * 
     * Initializes the database and database save timer for this bot
     */
    public DiscordBot() {
        loadConfigFromFile();
        
        //Initialize databse
        try {
            database = new ServerDatabase(DATABASE_SAVE_FILE);
        } catch (FileNotFoundException ex) {
            database = new ServerDatabase();
        }
        
        //Initialize save timer to save the database to SAVE_FILE at a fixed rate SAVE_RATE
        databaseSaver = new Timer();
        databaseSaver.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                saveDatabase();
            }
        }, DATABASE_SAVE_RATE_SECONDS * 1000, DATABASE_SAVE_RATE_SECONDS * 1000);
    }

    /**
     * Loads bot configuration from JSON file
     */
    public void loadConfigFromFile() {
        JSONObject json = getJSONFromFile(BOT_CONFIG_FILE);
        
        if (json.has("database_file")) {
            DATABASE_SAVE_FILE = json.getString("database_file");
        }
        if (json.has("bot_name")) {
            BOT_NAME = json.getString("bot_name");
        }
        if (json.has("bot_token")) {
            TOKEN = json.getString("bot_token");
        }
        if (json.has("command_char")) {
            COMMAND = json.getString("bot_command_char").charAt(0);
        }
        if (json.has("database_save_rate")) {
            DATABASE_SAVE_RATE_SECONDS = json.getLong("database_save_rate");
        }
        if (json.has("join_guild_message")) {
            JOIN_MESSAGE = json.getString("join_guild_message");
        }
        if (json.has("help_message")) {
            HELP_MESSAGE = json.getString("help_message");
        }
        if (json.has("info_message")) {
            INFO_MESSAGE = json.getString("info_message");
        }
    }

    /**
     * Attempt to load JSON from a file.
     * 
     * @param filepath Path of file to read
     * @return JSON read from file if successful and file exists. Null if file does not exist, or file does not contain JSON
     */
    public static JSONObject getJSONFromFile(String filepath) {
        
        String jsonString = "";
        
        //Read from file into string
        try {
            Scanner scan = new Scanner(new File(filepath));
            
            while (scan.hasNextLine()) {
                jsonString += scan.nextLine();
            }
            
            scan.close();
        } catch (FileNotFoundException ex) {
            //Return if file does not exist
            return null;
        }
        
        //If file was read from, attempt to create JSON
        try {
            return new JSONObject(jsonString);
        } catch (JSONException ex) {
            //Return if string is not JSON
            return null;
        }
    }

    /**
     * Saves the bot's database to file
     */
    private void saveDatabase() {
        database.saveDatabase(DATABASE_SAVE_FILE);
    }
    
    //This method is only called when a message is recieved that begins with the COMMAND char
    private void onCommandMessage(MessageReceivedEvent event) {
        //Cut off command identifier and split into command and arguments
        String cmd = event.getMessage().getContent().substring(1).toLowerCase();
        String args = "";
        if (cmd.contains(" ")) {
            args = cmd.substring(cmd.indexOf(" ") + 1);
            cmd = cmd.substring(0, cmd.indexOf(" "));
        }
        
        //Command checks
        if (cmd.equals("help")) {
            event.getTextChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + " " + HELP_MESSAGE);
        } else if (cmd.equals("info")) {
            event.getTextChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + " " + INFO_MESSAGE);
        }
    }
    
    //This method is only called when a message is recieved and does not begin with the COMMAND char
    private void onNonCommandMessage(MessageReceivedEvent event) {
        
    }

    /**
     * Is called by the JDA when a message has been received in any Guild
     * 
     * @param event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //Ensure user is added to databsae
        database.addGlobalUser(event.getAuthor());
        
        //Dispatch event to private methds
        if (event.getMessage().getContent().charAt(0) == COMMAND) {
            onCommandMessage(event);
        } else {
            onNonCommandMessage(event);
        }
    }

    /**
     * Is called by the JDA when a private message has been received
     * 
     * @param event
     */
    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        
    }

    /**
     * Is called by the JDA when this bot has been added to a Guild.
     * 
     * This event is only fired once per Guild, unless the guild kicks this bot and adds them again.
     * 
     * @param event
     */
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        //Send join message
        event.getGuild().getPublicChannel().sendMessage(JOIN_MESSAGE);
        
        //Create bot commander role
        if (event.getGuild().getRolesByName(BOT_NAME + " Commander").isEmpty()) {
            event.getGuild().createRole().setName(BOT_NAME + " Commander");
        }
        
        //Create GuildData for database
        database.addGuild(event.getGuild());
    }

    /**
     * Is called by the JDA when this bot has been removed from a Guild
     * 
     * @param event
     */
    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        //Remove GuildData from database
        database.removeGuild(event.getGuild());
    }
    
}

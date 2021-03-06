package discordbot;

import java.io.FileNotFoundException;
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

/**
 * A simple Discord Bot framework with minimal implementation. Intended to be used as a base to build on.
 * 
 * @author austinbt
 */
public class DiscordBot extends ListenerAdapter {

    private static final String TOKEN = "ayylmao";
    private static final char COMMAND = '!';
    private static final String SAVE_FILE = "server.data";
    private static final long SAVE_RATE = 60 * 1000;
    
    /**
     * The name of this bot
     */
    public final String BOT_NAME = "BOTTY";
    
    private static final String JOIN_MESSAGE = "Ayy";
    private static final String HELP_MESSAGE = "**Help:**\n```Nothing to help with!```";
    private static final String INFO_MESSAGE = "**Info**\n```Info about bot goes here```";
    
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
     * Initializes the database and database save timer for this bot
     */
    public DiscordBot() {
        //Initialize database from the given SAVE_FILE
        try {
            database = new ServerDatabase(SAVE_FILE);
        } catch (FileNotFoundException ex) {
            database = new ServerDatabase();
        }
        
        //Initialize save timer to save the database to SAVE_FILE at a fixed rate SAVE_RATE
        databaseSaver = new Timer();
        databaseSaver.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                database.saveDatabase(SAVE_FILE);
            }
        }, SAVE_RATE, SAVE_RATE);
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

Discord Bot Development Tutorial - Java Version
Table of Contents

    Introduction

    Prerequisites

    Setting Up Your Project

    Basic Bot Structure

    Event Listeners

    Commands

    Advanced Features

    Best Practices

Introduction

This tutorial covers creating Discord bots using Java with JDA (Java Discord API). Java provides strong typing and better performance for complex bot applications.
Prerequisites

    Java 17 or higher

    Maven or Gradle build tool

    A Discord account

    Basic Java knowledge

Setting Up Your Project
Maven Configuration (pom.xml)
xml

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.yourdomain</groupId>
    <artifactId>discord-bot</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <jda.version>5.0.0-beta.20</jda.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>net.dv8tion</groupId>
            <artifactId>JDA</artifactId>
            <version>${jda.version}</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.14</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

Gradle Configuration (build.gradle)
gradle

plugins {
    id 'java'
    id 'application'
}

group = 'com.yourdomain'
version = '1.0.0'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'net.dv8tion:JDA:5.0.0-beta.20'
    implementation 'ch.qos.logback:logback-classic:1.4.14'
}

application {
    mainClass = 'com.yourdomain.Bot'
}

Basic Bot Structure
Simple Bot Example (BasicBot.java)
java

package com.yourdomain;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class BasicBot extends ListenerAdapter {
    
    public static void main(String[] args) throws LoginException {
        String token = System.getenv("DISCORD_TOKEN");
        
        if (token == null) {
            System.out.println("Please set DISCORD_TOKEN environment variable");
            return;
        }
        
        JDA jda = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("tutorial"))
                .addEventListeners(new BasicBot())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore messages from bots
        if (event.getAuthor().isBot()) return;
        
        String message = event.getMessage().getContentRaw();
        
        // Simple command handling
        if (message.equalsIgnoreCase("!ping")) {
            event.getChannel().sendMessage("Pong!").queue();
        } else if (message.equalsIgnoreCase("!hello")) {
            event.getChannel().sendMessage(
                "Hello " + event.getAuthor().getAsMention() + "!"
            ).queue();
        }
    }
}

Event Listeners
Comprehensive Event Handling
java

package com.yourdomain;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class EventBot extends ListenerAdapter {
    
    public static void main(String[] args) throws LoginException {
        String token = System.getenv("DISCORD_TOKEN");
        
        JDA jda = JDABuilder.createDefault(token)
                .setActivity(Activity.playing("with Java"))
                .addEventListeners(new EventBot())
                .enableIntents(
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.GUILD_MEMBERS
                )
                .build();
    }
    
    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Bot is ready! Logged in as: " + 
            event.getJDA().getSelfUser().getAsTag());
    }
    
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();
        event.getGuild().getSystemChannel().sendMessage(
            "Welcome " + member.getAsMention() + " to the server!"
        ).queue();
    }
    
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        System.out.println("Member left: " + event.getUser().getAsTag());
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        
        String message = event.getMessage().getContentRaw();
        
        // Echo command
        if (message.startsWith("!echo ")) {
            String echoMessage = message.substring(6);
            event.getChannel().sendMessage(echoMessage).queue();
        }
        
        // User info command
        if (message.equalsIgnoreCase("!userinfo")) {
            Member member = event.getMember();
            String response = String.format(
                "User: %s\nID: %s\nJoined: %s",
                member.getEffectiveName(),
                member.getId(),
                member.getTimeJoined().toString()
            );
            event.getChannel().sendMessage(response).queue();
        }
    }
}

Commands
Advanced Command System
java

package com.yourdomain;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class CommandBot extends ListenerAdapter {
    
    public static void main(String[] args) throws LoginException {
        String token = System.getenv("DISCORD_TOKEN");
        
        JDA jda = JDABuilder.createDefault(token)
                .setActivity(Activity.listening("commands"))
                .addEventListeners(new CommandBot())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
        
        // Register slash commands
        jda.updateCommands().addCommands(
            Commands.slash("ping", "Check bot latency"),
            Commands.slash("server", "Get server information"),
            Commands.slash("user", "Get user information")
                .addOption(OptionType.USER, "user", "The user to get info about", false),
            Commands.slash("clear", "Clear messages")
                .addOption(OptionType.INTEGER, "amount", "Number of messages to clear", true)
        ).queue();
    }
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ping":
                handlePingCommand(event);
                break;
            case "server":
                handleServerCommand(event);
                break;
            case "user":
                handleUserCommand(event);
                break;
            case "clear":
                handleClearCommand(event);
                break;
            default:
                event.reply("Unknown command").setEphemeral(true).queue();
        }
    }
    
    private void handlePingCommand(SlashCommandInteractionEvent event) {
        long latency = event.getJDA().getGatewayPing();
        event.replyFormat("Pong! Gateway ping: %dms", latency).queue();
    }
    
    private void handleServerCommand(SlashCommandInteractionEvent event) {
        MessageEmbed embed = new net.dv8tion.jda.api.entities.MessageEmbed(
            null,
            "Server Information",
            null,
            null,
            null,
            Color.GREEN.getRGB(),
            null,
            null,
            null,
            null,
            new net.dv8tion.jda.api.entities.MessageEmbed.Footer(
                "Requested by " + event.getUser().getEffectiveName(),
                event.getUser().getEffectiveAvatarUrl(),
                null
            ),
            null,
            new net.dv8tion.jda.api.entities.MessageEmbed.Field[]{
                new net.dv8tion.jda.api.entities.MessageEmbed.Field(
                    "Server Name",
                    event.getGuild().getName(),
                    true
                ),
                new net.dv8tion.jda.api.entities.MessageEmbed.Field(
                    "Members",
                    String.valueOf(event.getGuild().getMemberCount()),
                    true
                ),
                new net.dv8tion.jda.api.entities.MessageEmbed.Field(
                    "Created",
                    event.getGuild().getTimeCreated().toString(),
                    false
                )
            }
        );
        
        event.replyEmbeds(embed).queue();
    }
    
    private void handleUserCommand(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user") != null 
            ? event.getOption("user").getAsMember()
            : event.getMember();
        
        if (member == null) {
            event.reply("User not found in this server").setEphemeral(true).queue();
            return;
        }
        
        MessageEmbed embed = new net.dv8tion.jda.api.entities.MessageEmbed(
            null,
            "User Information: " + member.getEffectiveName(),
            null,
            null,
            null,
            Color.BLUE.getRGB(),
            null,
            null,
            null,
            null,
            null,
            null,
            new net.dv8tion.jda.api.entities.MessageEmbed.Field[]{
                new net.dv8tion.jda.api.entities.MessageEmbed.Field(
                    "User ID",
                    member.getId(),
                    true
                ),
                new net.dv8tion.jda.api.entities.MessageEmbed.Field(
                    "Account Created",
                    member.getTimeCreated().toString(),
                    true
                ),
                new net.dv8tion.jda.api.entities.MessageEmbed.Field(
                    "Joined Server",
                    member.getTimeJoined().toString(),
                    true
                )
            }
        );
        
        event.replyEmbeds(embed).queue();
    }
    
    private void handleClearCommand(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(net.dv8tion.jda.api.Permission.MESSAGE_MANAGE)) {
            event.reply("You don't have permission to manage messages").setEphemeral(true).queue();
            return;
        }
        
        int amount = event.getOption("amount").getAsInt();
        
        if (amount < 1 || amount > 100) {
            event.reply("Please provide a number between 1 and 100").setEphemeral(true).queue();
            return;
        }
        
        TextChannel channel = event.getChannel().asTextChannel();
        channel.getHistory().retrievePast(amount + 1).queue(messages -> {
            channel.purgeMessages(messages);
            event.reply("Deleted " + amount + " messages").setEphemeral(true).queue();
        });
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        
        String message = event.getMessage().getContentRaw();
        
        // Legacy command support
        if (message.equalsIgnoreCase("!legacyping")) {
            long latency = event.getJDA().getGatewayPing();
            event.getChannel().sendMessage("Legacy ping: " + latency + "ms").queue();
        }
    }
}

Advanced Features
Database Integration with SQLite
java

package com.yourdomain;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseBot extends ListenerAdapter {
    private Connection connection;
    private Map<String, Integer> messageCountCache = new HashMap<>();
    
    public static void main(String[] args) throws LoginException {
        String token = System.getenv("DISCORD_TOKEN");
        
        JDA jda = JDABuilder.createDefault(token)
                .setActivity(Activity.playing("with databases"))
                .addEventListeners(new DatabaseBot())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();
    }
    
    @Override
    public void onReady(ReadyEvent event) {
        initializeDatabase();
        System.out.println("Database bot is ready!");
    }
    
    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:bot_data.db");
            Statement statement = connection.createStatement();
            
            // Create tables if they don't exist
            statement.execute("""
                CREATE TABLE IF NOT EXISTS user_stats (
                    user_id TEXT PRIMARY KEY,
                    message_count INTEGER DEFAULT 0,
                    last_active TEXT
                )
            """);
            
            statement.close();
            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        
        String userId = event.getAuthor().getId();
        updateUserStats(userId);
        
        // Command handling
        String message = event.getMessage().getContentRaw();
        
        if (message.equalsIgnoreCase("!mystats")) {
            displayUserStats(event, userId);
        } else if (message.equalsIgnoreCase("!leaderboard")) {
            displayLeaderboard(event);
        }
    }
    
    private void updateUserStats(String userId) {
        try {
            // Update or insert user stats
            String query = """
                INSERT OR REPLACE INTO user_stats (user_id, message_count, last_active)
                VALUES (?, COALESCE((SELECT message_count FROM user_stats WHERE user_id = ?), 0) + 1, datetime('now'))
            """;
            
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            statement.setString(2, userId);
            statement.executeUpdate();
            statement.close();
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
    
    private void displayUserStats(MessageReceivedEvent event, String userId) {
        try {
            String query = "SELECT message_count FROM user_stats WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                int count = result.getInt("message_count");
                event.getChannel().sendMessage(
                    "You have sent " + count + " messages!"
                ).queue();
            } else {
                event.getChannel().sendMessage("No stats found for you").queue();
            }
            
            statement.close();
        } catch (SQLException e) {
            event.getChannel().sendMessage("Error retrieving stats").queue();
        }
    }
    
    private void displayLeaderboard(MessageReceivedEvent event) {
        try {
            String query = """
                SELECT user_id, message_count 
                FROM user_stats 
                ORDER BY message_count DESC 
                LIMIT 10
            """;
            
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            
            StringBuilder leaderboard = new StringBuilder("**Message Leaderboard:**\n");
            int rank = 1;
            
            while (result.next()) {
                String userId = result.getString("user_id");
                int count = result.getInt("message_count");
                
                // Get user mention
                String mention = "<@" + userId + ">";
                leaderboard.append(rank).append(". ").append(mention)
                          .append(" - ").append(count).append(" messages\n");
                rank++;
            }
            
            if (rank == 1) {
                event.getChannel().sendMessage("No leaderboard data available").queue();
            } else {
                event.getChannel().sendMessage(leaderboard.toString()).queue();
            }
            
            statement.close();
        } catch (SQLException e) {
            event.getChannel().sendMessage("Error retrieving leaderboard").queue();
        }
    }
}

Configuration Management
java

package com.yourdomain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfig {
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = BotConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
    }
    
    public static String getToken() {
        return System.getenv("DISCORD_TOKEN");
    }
    
    public static String getPrefix() {
        return properties.getProperty("bot.prefix", "!");
    }
    
    public static String getActivity() {
        return properties.getProperty("bot.activity", "Java Development");
    }
    
    public static boolean isDebug() {
        return Boolean.parseBoolean(properties.getProperty("bot.debug", "false"));
    }
}

Best Practices
1. Proper Resource Management
java

package com.yourdomain;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class RobustBot extends ListenerAdapter {
    private JDA jda;
    
    public static void main(String[] args) {
        new RobustBot().start();
    }
    
    public void start() {
        try {
            String token = System.getenv("DISCORD_TOKEN");
            
            jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing("safely"))
                    .addEventListeners(this)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .build();
            
            // Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
            
        } catch (LoginException e) {
            System.err.println("Failed to login: " + e.getMessage());
        }
    }
    
    @Override
    public void onShutdown(ShutdownEvent event) {
        System.out.println("Bot is shutting down...");
        // Clean up resources
    }
    
    private void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
    }
}

2. Error Handling
java

package com.yourdomain;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class ErrorHandlingListener implements EventListener {
    
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        try {
            if (event instanceof MessageReceivedEvent) {
                handleMessage((MessageReceivedEvent) event);
            }
        } catch (Exception e) {
            System.err.println("Error handling event: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleMessage(MessageReceivedEvent event) {
        // Your message handling logic here
        if (event.getMessage().getContentRaw().equals("!error")) {
            throw new RuntimeException("Test error");
        }
    }
}


Next Steps

    Join JDA Community: https://discord.gg/jda

    Learn about:

        Event systems in JDA

        Rate limiting and best practices

        Advanced features like audio support

        Webhook integration

Remember to handle tokens securely and follow Discord's Developer Terms of Service







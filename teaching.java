package com.yourdomain;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.Instant;

public class CompleteBot extends ListenerAdapter {
    
    public static void main(String[] args) throws LoginException {
        String token = System.getenv("DISCORD_TOKEN");
        
        if (token == null || token.isEmpty()) {
            System.err.println("DISCORD_TOKEN environment variable is required");
            System.exit(1);
        }
        
        JDA jda = JDABuilder.createDefault(token)
                .setActivity(Activity.watching("for commands"))
                .addEventListeners(new CompleteBot())
                .enableIntents(
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.GUILD_MEMBERS
                )
                .build();
        
        // Register slash commands
        jda.updateCommands().addCommands(
            Commands.slash("ping", "Check bot latency"),
            Commands.slash("info", "Get bot information"),
            Commands.slash("help", "Show help information")
        ).queue();
    }
    
    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("✅ Bot is ready! Connected to " + 
            event.getJDA().getGuilds().size() + " servers.");
    }
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "ping":
                handlePing(event);
                break;
            case "info":
                handleInfo(event);
                break;
            case "help":
                handleHelp(event);
                break;
            default:
                event.reply("Unknown command").setEphemeral(true).queue();
        }
    }
    
    private void handlePing(SlashCommandInteractionEvent event) {
        long ping = event.getJDA().getGatewayPing();
        MessageCreateData message = new MessageCreateBuilder()
            .setContent(String.format("🏓 Pong! Latency: %dms", ping))
            .build();
        event.reply(message).queue();
    }
    
    private void handleInfo(SlashCommandInteractionEvent event) {
        JDA jda = event.getJDA();
        
        MessageEmbed embed = new net.dv8tion.jda.api.entities.MessageEmbed(
            null,
            "Bot Information",
            "A Java Discord bot built with JDA",
            null,
            null,
            Color.CYAN.getRGB(),
            new net.dv8tion.jda.api.entities.MessageEmbed.Thumbnail(
                jda.getSelfUser().getEffectiveAvatarUrl(),
                null,
                128,
                128
            ),
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
                    "Servers",
                    String.valueOf(jda.getGuilds().size()),
                    true
                ),
                new net.dv8tion.jda.api.entities.MessageEmbed.Field(
                    "Users",
                    String.valueOf(jda.getUsers().size()),
                    true
                ),
                new net.dv8tion.jda.api.entities.MessageEmbed.Field(
                    "Uptime",
                    "Online since " + jda.getStatus().toString(),
                    false
                )
            }
        );
        
        event.replyEmbeds(embed).queue();
    }
    
    private void handleHelp(SlashCommandInteractionEvent event) {
        MessageEmbed embed = new net.dv8tion.jda.api.entities.MessageEmbed(
            null,
            "Help Guide",
            "Available commands for this bot",
            null,
            null,
            Color.ORANGE.getRGB(),
            null,
            null,
            null,
            null,
            null,
            null,
            new net.dv8tion.jda.api.entities.MessageEmbed.Field[]{
                new net.dv8tion.jda.api.entities.MessageEmbed.Field(
                    "/ping",
                    "Check bot latency and response time",
                    false
                ),
                new net.dv8tion.jda.api.entities.MessageEmbed.Field(
                    "/info",
                    "Get information about the bot",
                    false
                ),
                new net.dv8tion.jda.api.entities.MessageEmbed.Field(
                    "/help",
                    "Show this help message",
                    false
                )
            }
        );
        
        event.replyEmbeds(embed).setEphemeral(true).queue();
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        
        String message = event.getMessage().getContentRaw();
        
        // Simple response to mentions
        if (message.contains(event.getJDA().getSelfUser().getAsMention())) {
            event.getChannel().sendMessage(
                "Hello! Use `/help` to see my commands."
            ).queue();
        }
    }
}

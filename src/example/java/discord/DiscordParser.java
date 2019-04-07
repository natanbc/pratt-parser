package discord;

import com.github.natanbc.pratt.Parser;
import discord.entity.Entity;

import javax.annotation.Nonnull;
import java.io.PushbackReader;
import java.io.StringReader;

public class DiscordParser extends Parser<Void, Entity> {
    public DiscordParser(@Nonnull String expression) {
        super(new DiscordLexer(new PushbackReader(new StringReader(expression))));
        for(DiscordTokenKind kind : DiscordTokenKind.values()) {
            register(kind, DiscordParselets.DEFAULT);
        }
        register(DiscordTokenKind.LEFT_ANGLE_BRACKET, DiscordParselets.LEFT_ANGLE_BRACKET);
        register(DiscordTokenKind.ID, DiscordParselets.ID);
        register(DiscordTokenKind.COLON, DiscordParselets.COLON);
        register(DiscordTokenKind.TEXT, DiscordParselets.TEXT);
        register(DiscordTokenKind.AT, DiscordParselets.AT);
        register(DiscordTokenKind.HASH, DiscordParselets.HASH);
        register(DiscordTokenKind.EXCLAMATION, DiscordParselets.EXCLAMATION_OR_AMPERSAND);
        register(DiscordTokenKind.AMPERSAND, DiscordParselets.EXCLAMATION_OR_AMPERSAND);
    }
    
    public static void main(String[] args) {
        System.out.println(new DiscordParser("<@!1234>").parseExpression(null));
    }
}

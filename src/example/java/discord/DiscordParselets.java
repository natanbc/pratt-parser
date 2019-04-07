package discord;

import com.github.natanbc.pratt.Position;
import com.github.natanbc.pratt.PrefixParselet;
import com.github.natanbc.pratt.Token;
import discord.entity.*;

public class DiscordParselets {
    // pure ID
    
    public static final PrefixParselet<Void, Entity> ID = (__1, __2, token) -> new ID(Long.parseUnsignedLong(token.value()));
    
    // error messages
    
    public static final PrefixParselet<Void, Entity> EXCLAMATION_OR_AMPERSAND = (__, parser, token) -> {
        Position pos = token.position();
        throw new IllegalArgumentException("Expected '@', got " +
                token.kind() + " (" + token.value() +
                ") at line " + pos.line() + ", column " + pos.column() +
                "\n" + parser.lexer().prettyContextFor(token));
    };
    
    public static final PrefixParselet<Void, Entity> DEFAULT = (__, parser, token) -> {
        Position pos = token.position();
        throw new IllegalArgumentException("Expected ':', 'a', '@' or '#', got " +
                token.kind() + " (" + token.value() +
                ") at line " + pos.line() + ", column " + pos.column() +
                "\n" + parser.lexer().prettyContextFor(token));
    };
    
    // <a:name:id>
    // <:name:id>
    public static final PrefixParselet<Void, Entity> COLON = (__1, parser, __2) -> {
        Token nameToken = parser.peek().kind() == DiscordTokenKind.ID ?
                parser.consume(DiscordTokenKind.ID) : parser.consume(DiscordTokenKind.TEXT);
        parser.expect(DiscordTokenKind.COLON);
        String id = parser.consume(DiscordTokenKind.ID).value();
        return new Emote(nameToken.value(), Long.parseUnsignedLong(id));
    };
    
    public static final PrefixParselet<Void, Entity> TEXT = (ctx, parser, token) -> {
        if(!token.value().equals("a")) {
            if(parser.peek().kind() == DiscordTokenKind.COLON) {
                Position pos = token.position();
                throw new IllegalArgumentException("Expected ':' or 'a', got " +
                        token.kind() + " (" + token.value() +
                        ") at line " + pos.line() + ", column " + pos.column() +
                        "\n" + parser.lexer().prettyContextFor(token));
            } else {
                return DEFAULT.parse(ctx, parser, token);
            }
        }
        return COLON.parse(ctx, parser, parser.consume(DiscordTokenKind.COLON));
    };
    
    // <@&id>
    // <@id>
    // <@!id>
    
    public static final PrefixParselet<Void, Entity> AT = (__1, parser, __2) -> {
        Token t = parser.peek();
        if(t.kind() == DiscordTokenKind.AMPERSAND) {
            parser.lexer().skip();
            return new Role(Long.parseUnsignedLong(parser.consume(DiscordTokenKind.ID).value()));
        }
        if(t.kind() == DiscordTokenKind.EXCLAMATION) {
            parser.lexer().skip();
        }
        return new User(Long.parseUnsignedLong(parser.consume(DiscordTokenKind.ID).value()));
    };
    
    // <#id>
    
    public static final PrefixParselet<Void, Entity> HASH = (__1, parser, __2) -> new Channel(
            Long.parseUnsignedLong(parser.consume(DiscordTokenKind.ID).value())
    );
    
    // <ANYTHING>
    
    public static final PrefixParselet<Void, Entity> LEFT_ANGLE_BRACKET = (ctx, parser, __2) -> {
        if(parser.peek().kind() == DiscordTokenKind.ID) {
            return DEFAULT.parse(ctx, parser, parser.peek());
        }
        Entity v = parser.parseExpression(ctx);
        parser.expect(DiscordTokenKind.RIGHT_ANGLE_BRACKET);
        return v;
    };
}

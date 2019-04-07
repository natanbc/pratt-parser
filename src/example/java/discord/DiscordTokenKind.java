package discord;

import com.github.natanbc.pratt.TokenKind;

public enum DiscordTokenKind implements TokenKind {
    LEFT_ANGLE_BRACKET, RIGHT_ANGLE_BRACKET, EXCLAMATION,
    AMPERSAND, HASH, COLON, AT, TEXT, ID, EOF
}

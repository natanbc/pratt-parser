package math;

import com.github.natanbc.pratt.TokenKind;

public enum MathTokenKind implements TokenKind {
    NUMBER, PLUS, MINUS, ASTERISK, SLASH, LEFT_PAREN, RIGHT_PAREN, EOF
}

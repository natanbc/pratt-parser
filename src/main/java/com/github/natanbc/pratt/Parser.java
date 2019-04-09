package com.github.natanbc.pratt;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Transforms a stream of tokens into an usable format, usually
 * an AST (Abstract Syntax Tree). This is done based on parselets
 * registered to this object.
 *
 * @param <C> Type of the object used for passing state for the parser.
 * @param <R> Result type of the parser.
 *
 * @see <a href="http://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy">Pratt Parsers: Expression Parsing Made Easy</a>
 * @see <a href="https://eli.thegreenplace.net/2010/01/02/top-down-operator-precedence-parsing/">Top-Down operator precedence parsing</a>
 */
public class Parser<C, R> {
    protected final Map<TokenKind, PrefixParselet<C, R>> prefixParselets = new HashMap<>();
    protected final Map<TokenKind, InfixParselet<C, R>> infixParselets = new HashMap<>();
    protected final Lexer lexer;
    
    public Parser(@Nonnull Lexer lexer) {
        this.lexer = lexer;
    }
    
    /**
     * Registers a prefix parselet.
     *
     * @param kind Token kind the parselet handles.
     * @param parselet Parselet to register.
     */
    public void register(@Nonnull TokenKind kind, @Nonnull PrefixParselet<C, R> parselet) {
        prefixParselets.put(kind, parselet);
    }
    
    /**
     * Registers an infix parselet.
     *
     * @param kind Token kind the parselet handles.
     * @param parselet Parselet to register.
     */
    public void register(@Nonnull TokenKind kind, @Nonnull InfixParselet<C, R> parselet) {
        infixParselets.put(kind, parselet);
    }
    
    /**
     * @return The lexer used by this parser.
     */
    @Nonnull
    @CheckReturnValue
    public Lexer lexer() {
        return lexer;
    }
    
    /**
     * Parses an expression with the default precedence of zero.
     *
     * @param context State used by the parselets. May be null.
     *
     * @return The result of the parsing.
     */
    @Nonnull
    @CheckReturnValue
    public R parseExpression(C context) {
        return parseExpression(context, 0);
    }
    
    /**
     * Parses an expression with the default precedence of zero.
     *
     * @param context State used by the parselets. May be null.
     * @param precedence Precedence for the current operator. Parsing
     *                   stops when a token which represents an operator
     *                   with lower precedence is found.
     *
     * @return The result of the parsing.
     */
    @Nonnull
    @CheckReturnValue
    public R parseExpression(C context, @Nonnegative int precedence) {
        Token t = lexer.next();
        if(t.kind().equals(lexer.eofKind())) {
            throw new IllegalArgumentException("Expression expected, got EOF\n" + lexer.prettyContextFor(t));
        }
        PrefixParselet<C, R> prefix = prefixParselets.get(t.kind());
        if(prefix == null) {
            Position pos = t.position();
            throw new IllegalArgumentException("Unexpected token of type " +
                    t.kind() + " (" + t.value() + ") at line " + pos.line() + ", column " +
                    pos.column() + "\n" + lexer.prettyContextFor(t));
        }
        R left = prefix.parse(context, this, t);
    
        while(precedence < currentPrecedence()) {
            t = lexer.next();
            InfixParselet<C, R> infix = infixParselets.get(t.kind());
            left = infix.parse(context, this, left, t);
        }
    
        return left;
    }
    
    @Nonnegative
    @CheckReturnValue
    private int currentPrecedence() {
        Token t = peek();
        InfixParselet<C, R> parser = infixParselets.get(t.kind());
        if(parser != null) return parser.precedence();
        return 0;
    }
    
    /**
     * Returns whether or not the current token matches the
     * provided kind, {@link Lexer#skip() skipping} it if it matches.
     *
     * @param kind Token kind to test.
     *
     * @return Whether or not the current token matches the provided kind.
     */
    @CheckReturnValue
    public boolean matches(@Nonnull TokenKind kind) {
        boolean match = peek().kind().equals(kind);
        if(match) {
            lexer.skip();
        }
        return match;
    }
    
    /**
     * Returns the next token if it matches the provided kind.
     * If it doesn't, it'll be {@link Lexer#push(Token) pushed}
     * back to the lexer.
     *
     * @param kind Wanted kind.
     *
     * @return The matched token, or {@link Optional#empty()} if
     *         there wasn't a match.
     */
    @Nonnull
    @CheckReturnValue
    public Optional<Token> match(@Nonnull TokenKind kind) {
        return match(t -> t.kind().equals(kind));
    }
    
    /**
     * Returns the next token if it matches the provided filter.
     * If it doesn't, it'll be {@link Lexer#push(Token) pushed}
     * back to the lexer.
     *
     * @param predicate Determines whether or not the token is
     *                  wanted.
     *
     * @return The matched token, or {@link Optional#empty()} if
     *         there wasn't a match.
     */
    @Nonnull
    @CheckReturnValue
    public Optional<Token> match(@Nonnull Predicate<Token> predicate) {
        Token t = lexer.next();
        if(predicate.test(t)) {
            return Optional.of(t);
        }
        lexer.push(t);
        return Optional.empty();
    }
    
    /**
     * Returns the current token, {@link Lexer#push(Token) pushing} it back to the lexer
     * so it can be read again.
     *
     * @return The current token.
     */
    @Nonnull
    @CheckReturnValue
    public Token peek() {
        Token t = lexer.next();
        lexer.push(t);
        return t;
    }
    
    /**
     * Returns the current token if it has the provided kind, otherwise throws.
     *
     * @param expected Expected kind.
     *
     * @return The current token, if it matches the wanted kind.
     */
    @Nonnull
    @CheckReturnValue
    public Token consume(@Nonnull TokenKind expected) {
        return checkKind(lexer.next(), expected);
    }
    
    /**
     * Skips the current token if it has the provided kind, otherwise throws.
     *
     * @param expected Expected kind.
     */
    public void expect(@Nonnull TokenKind expected) {
        checkKind(lexer.next(), expected);
    }
    
    private Token checkKind(@Nonnull Token actual, @Nonnull TokenKind expected) {
        if(!expected.equals(actual.kind())) {
            Position pos = actual.position();
            throw new IllegalArgumentException("Expected token of type " + expected + ", got " +
                    actual.kind() + " (" + actual.value() + ") at line " + pos.line() + ", column " +
                    pos.column() + "\n" + lexer.prettyContextFor(actual));
        }
        return actual;
    }
}

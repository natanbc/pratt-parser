# pratt-parser

Helper classes to write pratt parsers

# Usage

The core class that must be implemented is Lexer.

After implementing one, you can provide it to a Parser, along with the appropriate Prefix/Infix parselets to
parse expressions.

Example parsers can be found on the [examples](https://github.com/natanbc/pratt-parser/tree/master/src/example/java) directory.

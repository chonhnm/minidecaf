grammar MiniDecaf;

prog : func EOF;

func : type ident=Identity Lparen Rparen Lbrace stat Rbrace;

stat : Return expr Semicolon;

expr
    : additive
    ;

additive
    : multiplicative
    | additive op=('+' | '-') multiplicative
    ;

multiplicative
    : unary
    | multiplicative op=('*' | '/' | '%') unary
    ;

unary
    : primary
    | op=('-' | '!' | '~') unary
    ;

primary
    : Integer
    | '(' expr ')'
    ;

type : Int;

// lexer
Int : 'int';
Lparen : '(';
Rparen : ')';
Lbrace : '{';
Rbrace : '}';
Return : 'return';
Identity : Alphabet AlphaNum*;
Semicolon : ';';
Integer : [0-9]+;

fragment Alphabet : [a-zA-Z];
fragment AlphaNum : [a-zA-Z0-9];
fragment WhitespaceChar : [ \r\n];

WS : WhitespaceChar+ -> skip;


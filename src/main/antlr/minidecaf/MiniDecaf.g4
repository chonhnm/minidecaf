grammar MiniDecaf;

prog : func EOF;

func : type funcName Lparen Rparen Lbrace stat Rbrace;

funcName : Identity;

stat : Return expr Semicolon;

expr : Integer;

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
Integer : [+-]?[0-9]+;

fragment Alphabet : [a-zA-Z];
fragment AlphaNum : [a-zA-Z0-9];
fragment WhitespaceChar : [ \r\n];

WS : WhitespaceChar+ -> skip;


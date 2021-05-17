grammar MiniDecaf;

prog : func EOF;

func
    : type ident=Identifier '(' ')' compound_statement
    ;

compound_statement
    : '{' block_item* '}'
    ;

block_item
    : stat
    | declaration
    ;

stat
    : Return expr ';'  #retStat
    | expr? ';'              #exprStat
    | ifState                #ifStat
    | compound_statement     #comStat
    ;

ifState
    : ifWithoutElse
    | ifWithoutElse 'else' stat
    ;

ifWithoutElse
    : 'if' '(' expr ')' stat
    ;

declaration
    : type Identifier ('=' expr)? ';'
    ;

expr
    : assignment
    ;

assignment
    : conditional
    | Identifier '=' expr
    ;

conditional
    : logical_or
    | logical_or? '?' expr ':' conditional;

logical_or
    : logical_and
    | logical_or '||' logical_and
    ;

logical_and
    : equality
    | logical_and '&&' equality
    ;

equality
    : relational
    | equality op=('==' | '!=') relational
    ;

relational
    : additive
    | relational op=('<' | '>' | '<=' | '>=') additive
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
    : Integer        #priConst
    | '(' expr ')'   #priExpr
    | Identifier     #priIdent
    ;

type : Int;

// lexer
Int : 'int';
Lparen : '(';
Rparen : ')';
Lbrace : '{';
Rbrace : '}';
Return : 'return';
Identifier : Alphabet AlphaNum*;
Semicolon : ';';
Integer : [0-9]+;

fragment Alphabet : [a-zA-Z];
fragment AlphaNum : [a-zA-Z0-9];
fragment WhitespaceChar : [ \r\n];

WS : WhitespaceChar+ -> skip;


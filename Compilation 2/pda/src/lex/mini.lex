/* Compile me with:  jflex mini.lex  */
package lex;

%%

%class Scanner

%type Token

%{
Token tok(String tag) { 
	return new Token(tag, yycolumn, yytext());
}
%}
	
%column

INT = [0-9]+
NUM = {INT} ("." {INT})?
OP = [+-/*\^]
ID = [a-z][a-z0-9]*

DQUOTE = "\""
STR = {DQUOTE} ~([^\\]?{DQUOTE})

COMMENT = "#" .*
	
%%

{NUM}       { return tok("#"); }

{OP}        { return tok("◇"); }
{STR}       { return tok("A"); }
{ID}        { return tok("$"); }

{COMMENT} |
[ ]         { /* nothing; skip */ }

.           { throw new Error("Lexical error"); }

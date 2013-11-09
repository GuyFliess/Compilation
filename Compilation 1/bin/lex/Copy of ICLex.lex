/* Compile me with:  jflex mini.lex  */
package lex;

%%

%class Scanner
%column
%line

%type Token

%{
Token tok(String tag) { 
	return new Token(tag, yycolumn, yytext(), yyline, yycolumn);
}
%}

ID = [:jletter:] [:jletterdigit:]*

INT = [0-9]+
NUM = {INT} ("." {INT})?
OP = [+-/*\^]

DQUOTE = "\""
STR = {DQUOTE} ~([^\\]?{DQUOTE})

COMMENT = "#" .*
	
%%

/*keywords*/

<YYINITIAL> "class" { return tok("class");} 
<YYINITIAL> "return" {return tok("return");}
<YYINITIAL> "this" {return tok("this");}
<YYINITIAL> "extends" {return tok("extends");}
<YYINITIAL> "if" {return tok("if");}
<YYINITIAL> "new" {return tok("new");}
<YYINITIAL> "void" {return tok("void");}
<YYINITIAL> "else" {return tok("else");}
<YYINITIAL> "length" {return tok("length");}
<YYINITIAL> "int" {return tok("int");}
<YYINITIAL> "while" {return tok("while");}
<YYINITIAL> "true" {return tok("true");}
<YYINITIAL> "boolean" {return tok("boolean");}
<YYINITIAL> "break" {return tok("break");}
<YYINITIAL> "false" {return tok("false");}
<YYINITIAL> "string" {return tok("string");}
<YYINITIAL> "continue" {return tok("continue");}
<YYINITIAL> "null" {return tok("null");}
<YYINITIAL> "whilelse" {return tok("whilelse");}

<YYINITIAL> {
  /* identifiers */ 
  {ID} { return tok("ID"); }


{NUM}       { return tok("#"); }

{OP}        { return tok("?"); }
{STR}       { return tok("A"); }

{COMMENT} |
[ ]         { /* nothing; skip */ }

.           { throw new Error("Lexical error"); }
}






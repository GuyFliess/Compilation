package lex;

%%

%class Scanner
%unicode
%column
%line

%type Token

%{

StringBuffer string = new StringBuffer();

Token tok(String tag) { 
	return new Token(tag, yytext(), yyline + 1, yycolumn + 1);
}

Token tok(String tag, String value) { 
	return new Token(tag, value, yyline + 1, 1);
}
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator} 

Identifier = [:lowercase:] [:jletterdigit:]*
ClassID = [:uppercase:] [:jletterdigit:]*

DecIntegerLiteral = 0 | [1-9][0-9]*

%state STRING
	
%%

/*keywords*/

<YYINITIAL> "class" { return tok("class");} 
<YYINITIAL> "extends" {return tok("extends");}
<YYINITIAL> "static" {return tok("static");}
<YYINITIAL> "void" {return tok("void");}
<YYINITIAL> "int" {return tok("int");}
<YYINITIAL> "boolean" {return tok("boolean");}
<YYINITIAL> "string" {return tok("string");}
<YYINITIAL> "return" {return tok("return");}
<YYINITIAL> "if" {return tok("if");}
<YYINITIAL> "else" {return tok("else");}
<YYINITIAL> "while" {return tok("while");}
<YYINITIAL> "break" {return tok("break");}
<YYINITIAL> "continue" {return tok("continue");}
<YYINITIAL> "this" {return tok("this");}
<YYINITIAL> "new" {return tok("new");}
<YYINITIAL> "length" {return tok("length");}
<YYINITIAL> "true" {return tok("true");}
<YYINITIAL> "false" {return tok("false");}
<YYINITIAL> "null" {return tok("null");}

<YYINITIAL> {
  /* identifiers */ 
  {Identifier}                   { return tok("ID"); }
  {ClassID}						 { return tok("CLASS_ID"); }
 
  /* literals */
  {DecIntegerLiteral}            { return tok("INTEGER"); }
  \"                             { string.setLength(0); yybegin(STRING); }

  /* operators */
  "["                            { return tok("["); }
  "]"                            { return tok("]"); }
  "("                            { return tok("("); }
  ")"                            { return tok(")"); }
  "."                            { return tok("."); }
  "-"                            { return tok("-"); }
  "!"                            { return tok("!"); }
  "*"                            { return tok("*"); }
  "/"                            { return tok("/"); }
  "%"                            { return tok("%"); }
  "+"                            { return tok("+"); }
  "<"                            { return tok("<"); }
  "<="                           { return tok("<="); }
  ">"                            { return tok(">"); }
  ">="                           { return tok(">="); }
  "=="                           { return tok("=="); }
  "!="                           { return tok("!="); }
  "&&"                           { return tok("&&"); }
  "||"                           { return tok("||"); }
  "="                            { return tok("="); }
 
  /* structure */
  "{"                            { return tok("{"); }
  "}"                            { return tok("}"); }
  ";"                            { return tok(";"); }
  ","                            { return tok(","); }

  /* comments */
  {Comment}                      { /* ignore */ }
 
  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
}
 <STRING> {
  \"                             { yybegin(YYINITIAL);
                                   return tok("STRING", "\"" + string.toString() + "\""); }
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\t                            { string.append("\\t"); }
  \\n                            { string.append("\\n"); }
  \\r                            { string.append("\\r"); }
  \\                             { string.append("\\"); }
  \\\"                           { string.append("\\\""); }
  \\\\\"                         { string.append("\\\\"); }
  
  
}
 /* error fallback */
.|\n                             { return tok("ERROR","invalid character '"+yytext()+"'"); }








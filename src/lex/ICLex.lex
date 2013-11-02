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

TraditionalComment   = "/*" ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator} 

Identifier = [:lowercase:] [:jletterdigit:]*
ClassID = [:uppercase:] [:jletterdigit:]*

DecIntegerLiteral = 0 | [1-9][0-9]*

%state STRING
	
%%

<YYINITIAL> {

  /*keywords*/

  "class" 						 { return tok("class"); } 
  "extends" 					 { return tok("extends"); }
  "static" 						 { return tok("static"); }
  "void" 						 { return tok("void"); }
  "int" 						 { return tok("int"); }
  "boolean" 					 { return tok("boolean"); }
  "string" 						 { return tok("string"); }
  "return" 						 { return tok("return"); }
  "if" 							 { return tok("if"); }
  "else" 						 { return tok("else"); }
  "while" 						 { return tok("while"); }
  "break" 						 { return tok("break"); }
  "continue" 					 { return tok("continue"); }
  "this" 						 { return tok("this"); }
  "new" 						 { return tok("new"); }
  "length" 						 { return tok("length"); }
  "true" 						 { return tok("true"); }
  "false" 						 { return tok("false"); }
  "null" 						 { return tok("null"); }

  /* identifiers */ 
  {Identifier}                   { return tok("ID"); }
  _{Identifier}					 { return tok("ERROR","an identifier cannot start with '_'"); }
  {ClassID}						 { return tok("CLASS_ID"); }
 
  /* literals */
  {DecIntegerLiteral}            { return tok("INTEGER"); }
  _{DecIntegerLiteral}  		 { return tok("ERROR","an identifier cannot start with '_'"); }
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
  \\\\                           { string.append("\\\\"); }
  \\\"                           { string.append("\\\""); }
  {LineTerminator}  		     { yybegin(YYINITIAL);
  							       return tok("ERROR","malformed string literal"); }
  [\t]						     { yybegin(YYINITIAL);
  							       return tok("ERROR","malformed string literal"); }
}

 /* error fallback */

.|\n                             { return tok("ERROR","invalid character '" + yytext() + "'"); }








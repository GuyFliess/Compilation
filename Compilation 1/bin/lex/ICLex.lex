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
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

Identifier = [:lowercase:] [:jletterdigit:]*
ClassID = [:uppercase:] [:jletterdigit:]*

DecIntegerLiteral = 0 | [1-9][0-9]*

%state STRING
	
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

 <YYINITIAL> {
  /* identifiers */ 
  {Identifier}                   { return tok("ID"); }
  {ClassID}						 { return tok("CLASS_ID"); }
 
  /* literals */
  {DecIntegerLiteral}            { return tok("INTEGER"); }
  \"                             { string.setLength(0); yybegin(STRING); }

  /* operators */
  "="                            { return tok("EQ"); }
  "=="                           { return tok("EQEQ"); }
  "+"                            { return tok("PLUS"); }

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
.|\n                             { throw new Error("Illegal character <" + yytext() + ">"); }








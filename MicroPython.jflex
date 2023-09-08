%%
%{
  private void echo () { System . out . print (yytext ()); }

  public int position () { return yycolumn; }
%}

%class    MicroPythonLexer
%function nextToken
%type	  Token
%unicode
%line
%column
%eofval{
  { return new Token (Symbol . EOF); }
%eofval}

Comment    = \#.*
WhiteSpace = [ \t\n]
Id         = [:letter:] ("_"?([:letter:] | [:digit:]))*
Integer    = [:digit:] [:digit:]*

%%
"."		{ echo (); return new Token (Symbol . PERIOD); }
","		{ echo (); return new Token (Symbol . COMMA); }
":"		{ echo (); return new Token (Symbol . COLON); }
";"		{ echo (); return new Token (Symbol . SEMICOLON); }
"->"     	{ echo (); return new Token (Symbol . RETURNS); }
"("             { echo (); return new Token (Symbol . LPAREN); }
")"             { echo (); return new Token (Symbol . RPAREN); }
"<"		{ echo (); return new Token (Symbol . REL_OP, "<"); }
"<="		{ echo (); return new Token (Symbol . REL_OP, "<="); }
">"		{ echo (); return new Token (Symbol . REL_OP, ">"); }
">="		{ echo (); return new Token (Symbol . REL_OP, ">="); }
"=="		{ echo (); return new Token (Symbol . REL_OP, "=="); }
"!="		{ echo (); return new Token (Symbol . REL_OP, "!="); }
"+"		{ echo (); return new Token (Symbol . ADD_OP, "+"); }
"-"		{ echo (); return new Token (Symbol . ADD_OP, "-"); }
"*"		{ echo (); return new Token (Symbol . MULT_OP, "*"); }
"//"		{ echo (); return new Token (Symbol . MULT_OP, "//"); }
"="		{ echo (); return new Token (Symbol . ASSIGN); }
and             { echo (); return new Token (Symbol . AND); }
cons            { echo (); return new Token (Symbol . CONS); }
def             { echo (); return new Token (Symbol . DEF); }
else            { echo (); return new Token (Symbol . ELSE); }
from            { echo (); return new Token (Symbol . FROM); }
head          	{ echo (); return new Token (Symbol . HEAD); }
if		{ echo (); return new Token (Symbol . IF); }
import          { echo (); return new Token (Symbol . IMPORT); }
input		{ echo (); return new Token (Symbol . INPUT); }
int             { echo (); return new Token (Symbol . INT); }
MicroPythonList	{ echo (); return new Token (Symbol . MICRO_PYTHON_LIST); }
MicroPythonListClass
        	{ echo (); return new Token (Symbol . MICRO_PYTHON_LIST_CLASS); }
not             { echo (); return new Token (Symbol . NOT); }
null          	{ echo (); return new Token (Symbol . NULL); }
or              { echo (); return new Token (Symbol . OR); }
print         	{ echo (); return new Token (Symbol . PRINT); }
return          { echo (); return new Token (Symbol . RETURN); }
tail          	{ echo (); return new Token (Symbol . TAIL); }
while		{ echo (); return new Token (Symbol . WHILE); }
{Integer}	{ echo (); return new Token (Symbol . INTEGER, yytext ()); }
{Id}    	{ echo (); return new Token (Symbol . ID, yytext ()); }
{WhiteSpace}	{ echo (); }
{Comment}	{ echo (); }
.		{ echo (); ErrorMessage . print (yychar, "Illegal character"); }

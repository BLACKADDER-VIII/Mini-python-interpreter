// Token class definition
// Token is a class to represent lexical tokens in the in the MicroPython
// programming language.

public class Token {

  private Symbol symbol;	// current token
  private String lexeme;	// lexeme

  public Token () { }

  public Token (Symbol symbol) {
    this (symbol, null);
  }

  public Token (Symbol symbol, String lexeme) {
    this . symbol = symbol;
    this . lexeme  = lexeme;
  }

  public Symbol symbol () { return symbol; }

  public String lexeme () { return lexeme; }

  public String toString () {
    switch (symbol) {
      case PERIOD :            return "(punctuation, .) ";
      case COMMA :             return "(punctuation, ,) ";
      case COLON :             return "(punctuation, :) ";
      case SEMICOLON :         return "(punctuation, ;) ";
      case RETURNS :           return "(punctuation, ->) ";
      case ASSIGN :            return "(operator, =) ";
      case ADD_OP :            return "(operator, " + lexeme + ") ";
      case MULT_OP :           return "(operator, " + lexeme + ") ";
      case REL_OP :            return "(operator, " + lexeme + ") ";
      case LPAREN :            return "(operator, () ";
      case RPAREN :            return "(operator, )) ";
      case AND :               return "(keyword, and) ";
      case CONS :              return "(keyword, cons) ";
      case DEF :               return "(keyword, def) ";
      case ELSE :              return "(keyword, else) ";
      case FROM :              return "(keyword, from) ";
      case HEAD :              return "(keyword, head) ";
      case IF :                return "(keyword, if) ";
      case IMPORT :            return "(keyword, import) ";
      case INPUT :             return "(keyword, input) ";
      case INT :               return "(keyword, int) ";
      case MICRO_PYTHON_LIST : return "(keyword, MicroPythonList) ";
      case MICRO_PYTHON_LIST_CLASS : return "(keyword, MicroPythonListClass) ";
      case NOT :               return "(keyword, not) ";
      case NULL :              return "(keyword, null) ";
      case OR :                return "(keyword, or) ";
      case PRINT :             return "(keyword, print) ";
      case RETURN :            return "(keyword, return) ";
      case TAIL :              return "(keyword, tail) ";
      case WHILE :             return "(keyword, while) ";
      case ID :                return "(identifier, " + lexeme + ") ";
      case INTEGER :           return "(integer, " + lexeme + ") ";
      default : 
	ErrorMessage . print (0, "Unrecognized token");
        return null;
    }
  }

}

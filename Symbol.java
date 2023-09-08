// Symbol enumeration definition
// Symbol is an enumeration to represent lexical token classes in the 
// MicroPython programming language.

public enum Symbol {
  EOF, 
  // punctuation
  PERIOD, COMMA, COLON, SEMICOLON, RETURNS,
  // operators
  ASSIGN, ADD_OP, MULT_OP, REL_OP, LPAREN, RPAREN, 
  // keywords
  AND, CONS, DEF, ELSE, FROM, HEAD, IF, IMPORT, INPUT, INT, MICRO_PYTHON_LIST, 
  MICRO_PYTHON_LIST_CLASS, NOT, NULL, OR, PRINT, RETURN, TAIL, WHILE,
  // ids and integers
  ID, INTEGER
}


// ParserAST class

// ParserAST is a class to represent a recursive descent parser for the
// MicroPython programming language, and construct an abstract syntax tree
// for each function in the program.  ASTs are returned as the value of
// each parsing function.

import java.io.*;
import java.util.*;

public class ParserAST {

    protected MicroPythonLexer lexer; 	// lexical analyzer
    protected Token token;          	// current token

    public ParserAST (String inp) throws IOException {
        FileInputStream inputStream = new FileInputStream (inp);
        lexer = new MicroPythonLexer (new InputStreamReader (inputStream));
        getToken ();
    }

    private void getToken () throws IOException {
        token = lexer . nextToken ();
    }

    // program ::= from MicroPythonListClass import MicroPythonList { funcdef }

    public void program () throws java.io.IOException {
      Store store = new Store ();
        if (token . symbol () != Symbol . FROM) 		// from
            ErrorMessage . print (lexer . position (), "from EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . MICRO_PYTHON_LIST_CLASS) // MicroPythonListClass
            ErrorMessage . print (lexer . position (), "MicroPythonListClass EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . IMPORT) 		// import
            ErrorMessage . print (lexer . position (), "import EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . MICRO_PYTHON_LIST) // MicroPythonList
            ErrorMessage . print (lexer . position (), "list EXPECTED");
        getToken ();
        while (token . symbol () != Symbol . EOF)  		// { funcdef }
            funcDef (store);
    }

    // funcdef ::= def function-identifier ( [ formal-parameter { , formal-parameter } ] ) -> type :
    //   suite return add-expr

    public void funcDef (Store store) throws java.io.IOException {
        Statement functionBody, returnStatement;
        Vector<String> args = new Vector<String>();
        if (token . symbol () != Symbol . DEF)    		// def
            ErrorMessage . print (lexer . position (), "def EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . ID)    		// function-identifier
            ErrorMessage . print (lexer . position (), "function identifier EXPECTED");
        String functionId = token . lexeme ();
        System.out.println("Func name = "+functionId);
        getToken ();
        if (token . symbol () != Symbol . LPAREN)    	// (
            ErrorMessage . print (lexer . position (), "( EXPECTED");
        getToken ();
        if (token . symbol () == Symbol . ID) {		// [ formal-parameter
            formalParameter (args);
            while (token . symbol () == Symbol . COMMA) {	// { ,
                getToken ();
                if (token . symbol () != Symbol . ID)    	// formal-parameter
                    ErrorMessage . print (lexer . position (), "identifier EXPECTED");
                formalParameter (args);
            } 						// }
        } 							// ]
        if (token . symbol () != Symbol . RPAREN)    	// )
            ErrorMessage . print (lexer . position (), ") EXPECTED");
        getToken ();
        if (token . symbol () != Symbol . RETURNS)    	// )
            ErrorMessage . print (lexer . position (), "-> EXPECTED");
        getToken ();
        type ();
        if (token . symbol () != Symbol . COLON)   		// :
            ErrorMessage . print (lexer . position (), ": EXPECTED");
        getToken ();
        functionBody = suite (); 				// suite
        if (token . symbol () != Symbol . RETURN)  		// return
            ErrorMessage . print (lexer . position (), "return EXPECTED");
        getToken ();
        Expression returnExp = addExpr ();			// add-expr
        functionBody =
                new Statement (functionBody, new ReturnStatement (returnExp));
        if(Objects.equals(functionId, "main")){
            functionBody.semantics(store);
            System.out.println(returnExp.semantics(store));
        }
        else{
            Store fstore = new Store();
            fstore.func_body = functionBody;
            fstore.arg_list = args;
            fstore.id = functionId;
            store.update_f(functionId,fstore);
        }
    }

    // formal-parameter ::= variable-identifier : type

    public void formalParameter (Vector<String> args) throws java.io.IOException {
        if (token . symbol () != Symbol . ID)          	// variable-id
            ErrorMessage . print (lexer . position (), "variable identifier EXPECTED");
        args.add(token.lexeme());
        getToken ();
        if (token . symbol () != Symbol . COLON)            // :
            ErrorMessage . print (lexer . position (), ": EXPECTED");
        getToken ();
        type ();
    }

    // type ::= int | MicroPythonList

    public void type () throws java.io.IOException {
        if (token . symbol () == Symbol . INT)		// int
            getToken ();
        else if (token . symbol () == Symbol . MICRO_PYTHON_LIST) // MicroPythonList
            getToken ();
        else
            ErrorMessage . print (lexer . position (), "type EXPECTED");
    }

    // suite ::= statement { statement }

    public Statement suite () throws java.io.IOException {
        Statement stmt, stmt1;
        stmt = statement ();			// statement
        while (token . symbol () == Symbol . ID
                || token . symbol () == Symbol . IF
                || token . symbol () == Symbol . WHILE
                || token . symbol () == Symbol . PRINT) { // {
            stmt1 = statement ();			// statement
            stmt = new Statement (stmt, stmt1);
        } 						// }
        return stmt;
    }

    // statement ::= variable-identifier = add-expr
    //   | if or-test : suite ; [else : suite ;]
    //   | while or-test : suite ;
    //   | print ( add-expr )

    public Statement statement () throws java.io.IOException {
        Expression exp;
        Statement stmt = null, stmt1, stmt2;

        switch (token . symbol ()) {

            case ID : 					// variable-identifier
                Identifier id = new Identifier (token . lexeme ());
                getToken ();
                if (token . symbol () != Symbol . ASSIGN)	// =
                    ErrorMessage . print (lexer . position (), "= EXPECTED");
                getToken ();
                exp = addExpr ();				// add-expr
                stmt = new Assignment (id, exp);
                break;

            case IF : 					// if
                getToken ();
                exp = orTest ();				// or-test
                if (token . symbol () != Symbol . COLON)	// :
                    ErrorMessage . print (lexer . position (), ": EXPECTED");
                getToken ();
                stmt1 = suite ();				// suite
                if (token . symbol () != Symbol . SEMICOLON)	// ;
                    ErrorMessage . print (lexer . position (), "; EXPECTED");
                getToken ();
                if (token . symbol () == Symbol . ELSE) {	// [ else
                    getToken ();
                    if (token . symbol () != Symbol . COLON)	// :
                        ErrorMessage . print (lexer . position (), ": EXPECTED");
                    getToken ();
                    stmt2 = suite ();	           		// suite
                    if (token . symbol () != Symbol . SEMICOLON)	// ;
                        ErrorMessage . print (lexer . position (), "; EXPECTED");
                    getToken ();
                } 						// ]
                else
                    stmt2 = null;
                stmt = new IfStatement (exp, stmt1, stmt2);
                break;

            case WHILE : 					// while
                getToken ();
                exp = orTest ();	 			// or-test
                if (token . symbol () != Symbol . COLON)	// :
                    ErrorMessage . print (lexer . position (), ": EXPECTED");
                getToken ();
                stmt1 = suite ();				// suite
                if (token . symbol () != Symbol . SEMICOLON)	// ;
                    ErrorMessage . print (lexer . position (), "; EXPECTED");
                getToken ();
                stmt = new WhileStatement (exp, stmt1);
                break;

            case PRINT : 					// print
                getToken ();
                if (token . symbol () != Symbol . LPAREN)       // (
                    ErrorMessage . print (lexer . position (), "( EXPECTED");
                getToken ();
                exp = addExpr ();	 			// add-expr
                if (token . symbol () != Symbol . RPAREN)       // )
                    ErrorMessage . print (lexer . position (), ") EXPECTED");
                stmt = new PrintStatement (exp);
                getToken ();
                break;

            default :						// error
                ErrorMessage . print (lexer . position (), "STATEMENT EXPECTED");

        }
        return stmt;
    }

    // or-test ::= and-test { or and-test }

    public Expression orTest () throws java.io.IOException {
        Expression exp, exp1;
        exp = andTest ();	 				// and-test
        while (token . symbol () == Symbol . OR) {   	// { or
            getToken ();
            exp1 = andTest ();				// and-test
            exp = new Binary ("or", exp, exp1);
        } 							// }
        return exp;
    }

    // and-test ::= not-test { and not-test }

    public Expression andTest () throws java.io.IOException {
        Expression exp, exp1;
        exp = notTest ();	 				// not-test
        while (token . symbol () == Symbol . AND) {   	// { and
            getToken ();
            exp1 = notTest ();	 			// not-test
            exp = new Binary ("and", exp, exp1);
        } 							// }
        return exp;
    }

    // not-test ::= comparison | not not-test

    public Expression notTest () throws java.io.IOException {
        Expression exp;
        if (token . symbol () == Symbol . NOT) {   		// not
            getToken ();
            exp = notTest ();		 			// not-test
            exp = new Unary ("not", exp);
        }
        else
            exp = comparison ();				// comparison
        return exp;
    }

    // comparison ::= add-expr [ comp-operator add-expr ]

    public Expression comparison () throws java.io.IOException {
        Expression exp1, exp2 = null;
        String op = null;
        exp1 = addExpr ();	 				// add-expr
        if (token . symbol () == Symbol . REL_OP) {		// [ comp-operator
            op = token . lexeme ();
            getToken ();
            exp2 = addExpr ();				// add-expr ]
        }
        if (exp2 != null)
            exp1 = new Binary (op, exp1, exp2);
        return exp1;
    }

    // add-expr ::= mult-expr { add-operator mult-expr }

    public Expression addExpr () throws java.io.IOException {
        Expression exp, exp1;
        exp = multExpr ();	 				// mult-expr
        while (token . symbol () == Symbol . ADD_OP) {   	// { add-operator
            String op = token . lexeme ();
            getToken ();
            exp1 = multExpr ();				// mult-expr
            exp = new Binary (op, exp, exp1);
        } 							// }
        return exp;
    }

    // mult-expr ::= unary-expr { mult-operator unary-expr }

    public Expression multExpr () throws java.io.IOException {
        Expression exp, exp1;
        exp = unaryExpr ();	 				// unary-expr
        while (token . symbol () == Symbol . MULT_OP) {   	// { mult-operator
            String op = token . lexeme ();
            getToken ();
            exp1 = unaryExpr ();				// unary-expr
            exp = new Binary (op, exp, exp1);
        } 							// }
        return exp;
    }

    // unary-expr ::= [add-operator] primary

    public Expression unaryExpr () throws java.io.IOException {
        Expression exp;
        String unaryOp = null;
        if (token . symbol () == Symbol . ADD_OP) {  	// [ add-operator ]
            unaryOp = token . lexeme ();
            getToken ();
        }
        exp = primary ();	 				// primary
        if (unaryOp != null)
            exp = new Unary (unaryOp, exp);
        return exp;
    }

    // primary ::= integer-primary | list-primary
    // integer-primary ::= integer | int ( input ( ) )

    public Expression primary () throws java.io.IOException {
        Expression exp;
        if (token . symbol () == Symbol . INTEGER) {	// integer
            exp = new IntValue (token . lexeme ());
            getToken ();
        }
        else if (token . symbol () == Symbol . INT) {       // int
            getToken ();
            if (token . symbol () != Symbol . LPAREN)       	// (
                ErrorMessage . print (lexer . position (), "( EXPECTED");
            getToken ();
            if (token . symbol () != Symbol . INPUT)       // input
                ErrorMessage . print (lexer . position (), "input EXPECTED");
            getToken ();
            if (token . symbol () != Symbol . LPAREN)       // )
                ErrorMessage . print (lexer . position (), "( EXPECTED");
            getToken ();
            if (token . symbol () != Symbol . RPAREN)       // )
                ErrorMessage . print (lexer . position (), ") EXPECTED");
            getToken ();
            if (token . symbol () != Symbol . RPAREN)       // )
                ErrorMessage . print (lexer . position (), ") EXPECTED");
            exp = new Input ();
            getToken ();
        }
        else
            exp = listPrimary ();				// list-primary
        return exp;
    }

    // list-primary ::= atom | list-primary . cons ( add-expr )
    //   | list-primary . head ( ) | list-primary . tail ( )
    //   | list-primary . null ( )

    public Expression listPrimary () throws java.io.IOException {
        Expression exp1, exp2;
        exp1 = atom ();					// atom
        while (token . symbol () == Symbol . PERIOD) { 	// .
            getToken ();

            switch (token . symbol ()) {

                case CONS : 					// cons
                    getToken ();
                    if (token . symbol () != Symbol . LPAREN)  	// (
                        ErrorMessage . print (lexer . position (), "( EXPECTED");
                    getToken ();
                    exp2 = addExpr ();	 			// add-expr
                    if (token . symbol () != Symbol . RPAREN)  	// )
                        ErrorMessage . print (lexer . position (), ") EXPECTED");
                    exp1 = new Binary ("cons", exp1, exp2);
                    getToken ();
                    break;

                case HEAD : 	 				// head
                    getToken ();
                    if (token . symbol () != Symbol . LPAREN)  	// (
                        ErrorMessage . print (lexer . position (), "( EXPECTED");
                    getToken ();
                    if (token . symbol () != Symbol . RPAREN)  	// )
                        ErrorMessage . print (lexer . position (), ") EXPECTED");
                    exp1 = new Unary ("head", exp1);
                    getToken ();
                    break;

                case TAIL :	 				// tail
                    getToken ();
                    if (token . symbol () != Symbol . LPAREN)  	// (
                        ErrorMessage . print (lexer . position (), "( EXPECTED");
                    getToken ();
                    if (token . symbol () != Symbol . RPAREN)  	// )
                        ErrorMessage . print (lexer . position (), ") EXPECTED");
                    exp1 = new Unary ("tail", exp1);
                    getToken ();
                    break;

                case NULL :                                     // null
                    getToken ();
                    if (token . symbol () != Symbol . LPAREN)     // (
                        ErrorMessage . print (lexer . position (), "( EXPECTED");
                    getToken ();
                    if (token . symbol () != Symbol . RPAREN)     // )
                        ErrorMessage . print (lexer . position (), ") EXPECTED");
                    exp1 = new Unary ("null", exp1);
                    getToken ();
                    break;

                default:
                    ErrorMessage . print (lexer . position (), "LIST FUNCTION EXPECTED");

            }
        }
        return exp1;
    }

    // atom ::= variable-identifier | function-identifier ( [ add-expr-list ] )
    //   | ( add-expr ) | MicroPythonList ( )

    public Expression atom () throws java.io.IOException {
        ArrayList <Expression> expList = null;
        Expression exp = null;
        if (token . symbol () == Symbol . ID) { 		// identifier
            String id = token . lexeme ();
            getToken ();
            if (token . symbol () != Symbol . LPAREN) 	// [
                exp = new Identifier (id);
            else {						// (
                getToken ();
                if (token . symbol () != Symbol . RPAREN)
                    expList = addExprList ();			// add-expr-list
                if (token . symbol () != Symbol . RPAREN)     	// ) ]
                    ErrorMessage . print (lexer . position (), ") EXPECTED");
                exp = new FunctionCall (id, expList);
                getToken ();
            }
        }
        else if (token . symbol () == Symbol . LPAREN) {	// (
            getToken ();
            exp = addExpr ();					// add-expr
            if (token . symbol () != Symbol . RPAREN)	// )
                ErrorMessage . print (lexer . position (), ") EXPECTED");
            getToken ();
        }
        else if (token . symbol () == Symbol . MICRO_PYTHON_LIST) { // MicroPythonList
            getToken ();
            if (token . symbol () != Symbol . LPAREN)         // (
                ErrorMessage . print (lexer . position (), "( EXPECTED");
            getToken ();
            if (token . symbol () != Symbol . RPAREN)       // ) ]
                ErrorMessage . print (lexer . position (), ") EXPECTED");
            exp = new MicroPythonList ();
            getToken ();
        }
        else
            ErrorMessage . print (lexer . position (), "ATOM EXPECTED");
        return exp;
    }

    // add-expr-list ::= add-expr {, add-expr }

    public ArrayList<Expression> addExprList () throws java.io.IOException {
        Expression exp;
        ArrayList <Expression> expList = new ArrayList <Expression> ();
        exp = addExpr ();					// add-expr
        expList . add (exp);
        while (token . symbol () == Symbol . COMMA) {    	// { ,
            getToken ();
            exp = addExpr ();					// add-expr
            expList . add (exp);
        }							// }
        return expList;
    }

}

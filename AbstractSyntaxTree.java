// AbstractSyntaxTree.java

// This class represents all abstract syntax domains of the MicroPython
// programming language.

import java.util.*;

abstract class Program {
    abstract Object semantics(Store store);
}

class Statement extends Program {

   protected Statement stmt1, stmt2;

   public Statement () { }

   public Statement (Statement stmt1, Statement stmt2) {
     this . stmt1 = stmt1;
     this . stmt2 = stmt2;
   }

    public Object semantics(Store store){
       if(stmt1 != null && stmt2 == null)
        return stmt1.semantics(store);
       else if(stmt1 != null && stmt2 != null)
           stmt1.semantics(store);
       if(stmt2 != null)
        return stmt2.semantics(store);
       return null;
    }
}

class Assignment extends Statement {

   protected Identifier lhs;
   protected Expression rhs;

   public Assignment () { }

   public Assignment (Identifier lhs, Expression rhs) {
     this . lhs = lhs;
     this . rhs = rhs;
   }

   public String toString () {
     return "(= " + lhs + " " + rhs + ")";
   }
    public Object semantics (Store store) {
        store .  update (lhs.id() , rhs . semantics (store));
        return 1;
    }
}

class IfStatement extends Statement {

  protected Expression test;
  protected Statement thenStmt;
  protected Statement elseStmt;

  public IfStatement () { }

  public IfStatement (Expression test, Statement thenStmt) {
    this . test = test;
    this . thenStmt = thenStmt;
  }

  public IfStatement (Expression test, Statement thenStmt, Statement elseStmt) {
    this (test, thenStmt);
    this . elseStmt = elseStmt;
  }

  public String toString () {
     if (elseStmt == null)
       return "(if " + test + " " + thenStmt + ")";
     else
       return "(if " + test + " " + thenStmt + elseStmt + ")";
   }
    public Object semantics(Store store){
      if((int)test.semantics(store) != 0){
          return thenStmt.semantics(store);
      }
      else if(elseStmt != null){
          return elseStmt.semantics(store);
        }
      return 0;
    }
}

class PrintStatement extends Statement {

   protected Expression exp;

   public PrintStatement () { }

   public PrintStatement (Expression exp) {
     this . exp = exp;
   }

   public String toString () {
     return "(print " + exp + ")";
   }
    public Object semantics(Store store){
       System.out.println(exp.semantics(store));
       return null;
    }
}

class ReturnStatement extends Statement {

   protected Expression exp;

   public ReturnStatement () { }

   public ReturnStatement (Expression exp) {
     this . exp = exp;
   }

   public String toString () {
     return "(return " + exp + ")";
   }
    public Object semantics(Store store){
        return exp.semantics(store);
    }
}

class WhileStatement extends Statement {

  protected Expression test;
  protected Statement body;

  public WhileStatement () { }

  public WhileStatement (Expression test, Statement body) {
    this . test = test;
    this . body = body;
  }

  public String toString () {
     return "(while " + test + " " + body + ")";
   }
    public Object semantics(Store store){
      if((int)test.semantics(store) != 0){
          body.semantics(store);
          return this.semantics(store);
      }
      return null;
    }
}

abstract class Expression {
    abstract Object semantics(Store store);
}

class Identifier extends Expression {

  protected String id;

  public Identifier () { }

  public Identifier (String id) {
    this . id = id;
  }

  public String toString () {
    return "(id " + id + ")";
  }
  public String id(){return id;}
    public Object semantics(Store store){
        return store.get(id);
    }
}

class IntValue extends Expression {

  protected int intValue;

  public IntValue () { }

  public IntValue (int intValue) {
    this . intValue = intValue;
  }

  public IntValue (String intValue) {
    this . intValue = Integer . parseInt (intValue);
  }

  public String toString () {
    return "(integer " + intValue + ")";
  }
    public Object semantics(Store store){
      return intValue;
    }
}

class Input extends Expression {

  public Input () { }

  public String toString () {
    return "(input)";
  }
    public Object semantics(Store store){
      System.out.print("Input>>");
        Scanner myObj = new Scanner(System.in);
        return myObj.nextInt();
    }
}

class MicroPythonList extends Expression {
  public MicroPythonList () { }
  public String toString () {
    return "(MicroPythonList)";
  }
  public Object semantics(Store store){
      return new Vector<Object>();
  }
}

class Unary extends Expression {

  protected String op;
  protected Expression exp;

  public Unary () { }

  public Unary (String op, Expression exp) {
    this . op = op;
    this . exp = exp;
  }

  public String toString () {
    return "(" + op + " " + exp + ")";
  }
    public Object semantics(Store store){
        if(op == "-"){
            return -1*(int)exp.semantics(store);
        }
        else if(op == "head"){
            //Identifier id = (Identifier) exp;
            return ((Vector<Object>)(exp.semantics(store))).firstElement();
        }
        else if(op == "tail"){
            //Identifier id = (Identifier) exp;
            Vector<Object> v = new Vector<Object>();
            v.addAll((Vector<Object>)(exp.semantics(store)));
            v.remove(0);
            return v;
        }
        else if(op == "null"){
            Identifier id = (Identifier) exp;
            return (((Vector<Object>)(exp.semantics(store))).isEmpty())? 1:0;
        }
        else if(op == "not"){
            return ((int)exp.semantics(store) == 0)? 1:0;
        }
        else{
            return exp.semantics(store);
        }
  }
}

class Binary extends Expression {

  protected String op;
  protected Expression exp1, exp2;

  public Binary () { }

  public Binary (String op, Expression exp1, Expression exp2) {
    this . op = op;
    this . exp1 = exp1;
    this . exp2 = exp2;
  }

  public String toString () {
    return "(" + op + " " + exp1 + " " + exp2 + ")";
  }
    public Object semantics(Store store){
      if(Objects.equals(op, "+")){
          return (int)exp1.semantics(store) + (int)exp2.semantics(store);
      }
      else if(Objects.equals(op, "-")){
          return (int)exp1.semantics(store) - (int)exp2.semantics(store);
      }
      else if(Objects.equals(op, "*")){
          return (int)exp1.semantics(store) * (int)exp2.semantics(store);
      }
      else if(Objects.equals(op, "/")){
          return (int)exp1.semantics(store)/(int)exp2.semantics(store);
      }
      else if(Objects.equals(op, "==")){
          if(exp1.semantics(store) == exp2.semantics(store)){return 1;}
          else{return 0;}
      }
      else if(Objects.equals(op, ">=")){
          return ((int)exp1.semantics(store) >= (int)exp2.semantics(store))? 1:0;
      }
      else if(Objects.equals(op, "<=")){
          return ((int)exp1.semantics(store) <= (int)exp2.semantics(store))? 1:0;
      }
      else if(Objects.equals(op, "!=")){
          return ((int)exp1.semantics(store) != (int)exp2.semantics(store))? 1:0;
      }
      else if(Objects.equals(op, ">")){
          return ((int)exp1.semantics(store) > (int)exp2.semantics(store))? 1:0;
      }
      else if(Objects.equals(op, "<")){
          return ((int)exp1.semantics(store) < (int)exp2.semantics(store))? 1:0;
      }
      else if(Objects.equals(op, "cons")){
          ((Vector<Object>)(exp1.semantics(store))).add(exp2.semantics(store));
          return exp1.semantics(store);
      }
      else if(Objects.equals(op, "and")){
          if((int)exp1.semantics(store) != 0 && (int)exp2.semantics(store) != 0){
              return 1;
          }
      }
      else if(Objects.equals(op, "or")){
          if((int)exp1.semantics(store) != 0 || (int)exp2.semantics(store) != 0){
              return 1;
          }
      }
      return 0;
    }
}

class FunctionCall extends Expression {

  protected String id;
  protected ArrayList <Expression> actualParameters;
  public FunctionCall () { }

  public FunctionCall (String id, ArrayList <Expression> actualParameters) {
    this . id = id;
    this . actualParameters = actualParameters;
  }

  public String toString () {
    String actualParameterList = "";
    Iterator <Expression> actualParameterIterator =
      actualParameters . iterator ();
    while (actualParameterIterator . hasNext ()) {
      Expression actualParameter = actualParameterIterator . next ();
      if (actualParameterList . equals (""))
        actualParameterList = actualParameterList + actualParameter;
      else
        actualParameterList = actualParameterList + " " + actualParameter;
    }
    return "(apply " + id + " (" + actualParameterList + "))";
  }
    public Object semantics(Store store){
      Store fstore = store.get_f(id);
        Iterator <Expression> actualParameterIterator = actualParameters . iterator ();
      for(String s:fstore.arg_list){
          Expression e = (actualParameterIterator.next());
          fstore.update(s,e.semantics(store));
          fstore.update_f(id,fstore);
      }
      return (fstore.func_body).semantics(fstore);
    }
} 

// MicPyInt.java

// This program is an interpreter for PL/0. At the conclusion of interpreting
// a program, the program's memory store is output.

public class MicPyInt {

  public static void main (String args []) throws java.io.IOException {
    System . out . println ("Source Program");
    System . out . println ("--------------");
    System . out . println ();

    ParserAST pl0 = new ParserAST (args[0]);
    //Environment env = new Environment ();
    //Program program = pl0 . program ();
    pl0.program();
    System . out . println ();
    //env . print ("main program", program);
    System . out . println ();
    //Store store = new Store (Location . stackFrameSize (), Location . mainProgSize ());
    //program . semantics (store);
    System . out . println ();
    //System . out . println (store);
  }

}

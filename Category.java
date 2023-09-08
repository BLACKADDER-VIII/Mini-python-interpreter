// Category.java

// Category is a class to represent the categories of identifiers in a PL/0
// program.

public class Category {

  public static final int VARIABLE  = 1;
  public static final int FUNCTION = 2;

  public static String toString (int category) {
    switch (category) {
      case VARIABLE  : return "variable ";
      case FUNCTION : return "function";
      default        : return null;
    }
  }

}
enum Type { INT, MICRO_PYTHON_LIST, BOOLEAN, ERROR }
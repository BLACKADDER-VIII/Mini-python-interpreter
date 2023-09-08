// Store.java

// Store is a class to model the memory store of PL/0 programs.

import java.util.TreeMap;
import java.util.Vector;

public class Store {
  protected TreeMap<String,Object> map ;
  protected TreeMap<String,Store> funcmap;
  public Statement func_body;
  public Vector<String> arg_list;
  public String id;
  public Store(){
    map = new TreeMap<String,Object>();
    funcmap = new TreeMap<String,Store>();
    arg_list = new Vector<String>();
  }
  public void update(String s, Object o){map.put(s,o);}
  public Object get(String s){return map.get(s);}
  public void update_f(String f, Store s){funcmap.put(f,s);}
  public Store get_f(String f){return funcmap.get(f);}
}

/***
Sys.java
created 2005-april-18
biogeomancer project
***/
package org.biogeomancer.utils;

public class Sys{
    /***
    just some sys wrappers to reduce ammount
    of code. :)
    ***/
    public void print(String s){
	System.out.println(s);}
    public void except(String message, Exception e){
	e.printStackTrace();
	System.err.println(message);}
    
}

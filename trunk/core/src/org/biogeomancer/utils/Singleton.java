/***
Singleton.java
created 2005-june-18
biogeomancer project
***/
package org.biogeomancer.utils;

public class Singleton extends java.lang.Object implements Runnable{
    private static final Object lock = new Object();
    public Singleton(){
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();}
    public void run(){
	synchronized(lock){
	    try{
		lock.wait();} 
	    catch(InterruptedException e){
	    }}}
}

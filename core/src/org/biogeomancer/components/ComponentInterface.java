package org.biogeomancer.components;
import org.biogeomancer.managers.*;
import org.biogeomancer.records.*;


/**
 * <p></p>
 */
public interface ComponentInterface {
/**
 * <p>Does ...</p>
 * 
 * 
 * 
 * @param records 
 * @param managers 
 */
     public abstract RecSet execute(RecSet recset, ManagerInterface managers);
}










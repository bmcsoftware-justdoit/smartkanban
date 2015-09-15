package com.bmc.justdoit.smartkanban.agiletools;
import com.bmc.justdoit.smartkanban.agiletools.jira.JiraClientFacade;



/**
 * @author smijithkp
 *
 */
public class AgileToolFactory {
    static AgileToolIntf toolIntf = null;
    public static AgileToolIntf getAgileToolIntf(){
    	
        //TODO Load this dynamically based on config
        if(toolIntf == null){
            synchronized (AgileToolFactory.class) {
                if(toolIntf == null){
                	toolIntf =  new JiraClientFacade();
                }
            }
        }
        
        return toolIntf;
    }
}

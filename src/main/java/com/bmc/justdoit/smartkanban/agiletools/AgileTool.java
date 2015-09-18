/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.agiletools;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author spichapp
 */
public abstract class AgileTool implements AgileToolIntf {
    
    private final static Map<String, Integer>  PHYSICAL_KANBAN_STATUS_MAP;
    static{
        PHYSICAL_KANBAN_STATUS_MAP = new HashMap<String, Integer>();
        PHYSICAL_KANBAN_STATUS_MAP.put("Backlog", 0);
        PHYSICAL_KANBAN_STATUS_MAP.put("Develepment In Progress", 1);
        PHYSICAL_KANBAN_STATUS_MAP.put("Development Complete", 2);
        PHYSICAL_KANBAN_STATUS_MAP.put("Testing In Progress", 3);
        PHYSICAL_KANBAN_STATUS_MAP.put("Done", 4);
        PHYSICAL_KANBAN_STATUS_MAP.put("Accepted", 5);
    }
    
    public Set<String> getSupportedPhysicalKanbanStatuses() {
        return PHYSICAL_KANBAN_STATUS_MAP.keySet();
    }
    
    public Map<String, Integer> getPhysicalKanbanStatusMap(){
        return PHYSICAL_KANBAN_STATUS_MAP;
    }
}

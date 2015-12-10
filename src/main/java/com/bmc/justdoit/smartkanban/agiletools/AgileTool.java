/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.agiletools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author spichapp
 */
public abstract class AgileTool implements AgileToolIntf {

    private final static List<PhysicalKanbanStatus> PHYSICAL_KANBAN_STATUS = 
            new ArrayList<PhysicalKanbanStatus>();

    static {
        PhysicalKanbanStatus header = new PhysicalKanbanStatus();
        header.setKey(0);
        header.setQrcodeData("Header 0");
        header.setLabel("Sprint Backlog");
        header.setDescription("Stories/Tasks backlog");
        PHYSICAL_KANBAN_STATUS.add(header);
        
        header = new PhysicalKanbanStatus();
        header.setKey(1);
        header.setQrcodeData("Header 1");
        header.setLabel("Development In Progress");
        header.setDescription("Tasks/sub-tasks in development phase");
        PHYSICAL_KANBAN_STATUS.add(header);
        
        header = new PhysicalKanbanStatus();
        header.setKey(2);
        header.setQrcodeData("Header 2");
        header.setLabel("Development Complete");
        header.setDescription("Tasks/sub-tasks' development is complete");
        PHYSICAL_KANBAN_STATUS.add(header);
        
        header = new PhysicalKanbanStatus();
        header.setKey(3);
        header.setQrcodeData("Header 3");
        header.setLabel("Testing In Progress");
        header.setDescription("Tasks/sub-tasks in testing phase");
        PHYSICAL_KANBAN_STATUS.add(header);
        
        header = new PhysicalKanbanStatus();
        header.setKey(4);
        header.setQrcodeData("Header 4");
        header.setLabel("Done");
        header.setDescription("Story/Task are done and waiting for approval from PO");
        PHYSICAL_KANBAN_STATUS.add(header);
        
        header = new PhysicalKanbanStatus();
        header.setKey(5);
        header.setQrcodeData("Header 5");
        header.setLabel("Accepted");
        header.setDescription("Stories/Tasks ready for delivery");
        PHYSICAL_KANBAN_STATUS.add(header);
    }
    
    public Collection<PhysicalKanbanStatus> getSupportedPhysicalKanbanStatuses(){
        return PHYSICAL_KANBAN_STATUS;
    }
}

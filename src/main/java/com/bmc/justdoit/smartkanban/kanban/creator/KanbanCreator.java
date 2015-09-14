/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban.creator;

import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import java.util.List;

/**
 *
 * @author gokumar
 */
public class KanbanCreator implements Runnable{

//    private String requestId;
    private String emailId;
    private List<WorkItem> workItems;
    
    public KanbanCreator(String emailId, List<WorkItem> workItems){
        this.emailId = emailId;
    }
    public void run() {
        System.out.println("New request: " + emailId);
    }
}

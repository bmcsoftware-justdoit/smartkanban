/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.agiletools;

import java.util.List;

/**
 *
 * @author gokumar
 */
public class WorkItem {

    String id;
    String title;
    String status;
    String estimation;
    String remaining;
    String description;
    String acceptanceCriteria;
    String physicalKanbanStatus;
    String assignee;
    String transition;

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }
    
    WorkItemType type;
    
    List<WorkItem> subTasks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEstimation() {
        return estimation;
    }

    public void setEstimation(String estimation) {
        this.estimation = estimation;
    }

    public String getRemaining() {
        return remaining;
    }

    public void setRemaining(String remaining) {
        this.remaining = remaining;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria;
    }

    public List<WorkItem> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<WorkItem> subTasks) {
        this.subTasks = subTasks;
    }

    public WorkItemType getType() {
        return type;
    }

    public void setType(WorkItemType type) {
        this.type = type;
    }
    
    public String getPhysicalKanbanStatus() {
        return physicalKanbanStatus;
    }

    public void setPhysicalKanbanStatus(String physicalKanbanStatus) {
        this.physicalKanbanStatus = physicalKanbanStatus;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
    public String getAssignee() {
        return assignee;
    }
}

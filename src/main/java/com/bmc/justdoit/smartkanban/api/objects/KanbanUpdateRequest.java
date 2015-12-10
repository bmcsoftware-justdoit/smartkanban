/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api.objects;

import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gokumar
 */
public class KanbanUpdateRequest {
    private Map<String, String> authAttrs;
    private String requestId;
    private boolean async;
    private List<WorkItem> workItems;

    public List<WorkItem> getWorkItems() {
        return workItems;
    }

    public void setWorkItems(List<WorkItem> workItems) {
        this.workItems = workItems;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public Map<String, String> getAuthAttrs() {
        return authAttrs;
    }

    public void setAuthAttrs(Map<String, String> authAttrs) {
        this.authAttrs = authAttrs;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

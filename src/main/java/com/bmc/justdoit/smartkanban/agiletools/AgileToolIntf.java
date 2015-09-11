/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.agiletools;

import com.bmc.justdoit.smartkanban.api.objects.LoginRequest;
import com.bmc.justdoit.smartkanban.api.objects.LoginResponse;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gokumar
 */
public interface AgileToolIntf {
    public LoginResponse login(LoginRequest request);
    
    public WorkItem getWorkItem(Map<String, String> authAttrs, String workItemId);
    
    public List<WorkItem> getWorkItems(Map<String, String> authAttrs, SprintQuery query);
    
    public boolean updateWorkItem(Map<String, String> authAttrs, WorkItem item);    
}

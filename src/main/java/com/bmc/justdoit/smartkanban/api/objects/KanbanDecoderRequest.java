/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api.objects;

import java.util.Map;

/**
 *
 * @author gokumar
 */
public class KanbanDecoderRequest {
    private Map<String, String> authAttrs;
    private String requestId;
    private String fileName;

    public Map<String, String> getAuthAttrs() {
        return authAttrs;
    }

    public void setAuthAttrs(Map<String, String> authAttrs) {
        this.authAttrs = authAttrs;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

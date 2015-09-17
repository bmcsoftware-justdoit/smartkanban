/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api.objects;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gokumar
 */
@XmlRootElement
public class KanbanImageUploadResponse extends ErrorResponse {
   String fileName;
   String requestId; 

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
   
   
}

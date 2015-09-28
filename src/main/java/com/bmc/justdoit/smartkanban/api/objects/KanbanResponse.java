/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api.objects;

import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gokumar
 */
@XmlRootElement
public class KanbanResponse extends ErrorResponse {
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private String result;
    private List<WorkItem> workItems;

    public List<WorkItem> getWorkItems() {
        return workItems;
    }

    public void setWorkItems(List<WorkItem> workItems) {
        this.workItems = workItems;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    
    public Object getAttrValue(String key){
        return attributes.get(key);
    }
    
    public void setAttr(String key, Object value){
        attributes.put(key, value);
    }

    public KanbanResponse() {
        super();
    }
}

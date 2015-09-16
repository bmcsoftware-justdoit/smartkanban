/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api.objects;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gokumar
 */
@XmlRootElement
public class KanbanDecoderResponse extends ErrorResponse {
    private Map<String, Object> attributes = new HashMap<String, Object>();
    
    public Object getAttrValue(String key){
        return attributes.get(key);
    }
    
    public void setAttr(String key, Object value){
        attributes.put(key, value);
    }

    public KanbanDecoderResponse() {
        super();
    }
}

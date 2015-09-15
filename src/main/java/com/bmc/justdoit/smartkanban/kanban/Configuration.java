/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gokumar
 */
public class Configuration {
    private static Configuration _configuration;
    
    private Map<String, Object> attrs = new HashMap<String, Object>();

    public Object getAttrValue(String key) {
        return attrs.get(key);
    }

    public void setAttr(String key, Object value) {
        attrs.put(key, value);
    }
    
    private Configuration(){
        
    }
    
    public static Configuration getInstance(){
        if(_configuration == null){
            _configuration = new Configuration();
        }
        return _configuration;
    }
}

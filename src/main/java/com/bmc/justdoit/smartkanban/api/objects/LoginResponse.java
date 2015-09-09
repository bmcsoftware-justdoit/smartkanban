/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api.objects;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author karanbirgujral
 */
@XmlRootElement
public class LoginResponse extends ErrorResponse {
    private String token;

    public LoginResponse() {
        super();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

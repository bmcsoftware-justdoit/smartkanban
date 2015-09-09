/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api;

import com.bmc.justdoit.smartkanban.api.objects.ErrorResponse;
import com.bmc.justdoit.smartkanban.api.objects.LoginRequest;
import com.bmc.justdoit.smartkanban.api.objects.LoginResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author gokumar
 */
@Path("/authenticate")
public class AuthenticateResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AuthenticateResource
     */
    public AuthenticateResource() {
        
    }

    /**
     * @POST @Consumes("application/json")
     * @Produces("application/json") public LoginResponse postJson(LoginRequest
     * request) {
     *
     * String token;
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public LoginResponse postJson(LoginRequest request) {
        LoginResponse response = null;
        try {
            response = new LoginResponse();
            response.setObjectId("dummy");
            response.setToken("dummy");
        } catch (Exception ex) {
            response = new LoginResponse();
            response.setErrorCode(ErrorResponse.NESTED_ERROR);
            response.setErrorMessage("Could not identify and login user " + request.getLoginId());
            response.setErrorTrace(ex.getStackTrace().toString());
        }
        return response;
    }
}

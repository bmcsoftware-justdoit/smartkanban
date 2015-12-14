package com.bmc.justdoit.smartkanban.api;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.bmc.justdoit.smartkanban.agiletools.AgileToolFactory;
import com.bmc.justdoit.smartkanban.agiletools.AgileToolIntf;
import com.bmc.justdoit.smartkanban.api.objects.LoginRequest;
import com.bmc.justdoit.smartkanban.api.objects.LoginResponse;

/**
 * REST Web Service
 * 
 * @author smijithkp
 */

@Path("/login")
public class LoginResource {
	@Context
	private UriInfo context;
	
	public LoginResource() {

	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public LoginResponse login(LoginRequest request) throws Exception {
		System.out.println("Receieved login request for user " +  request.getLoginId());
		AgileToolIntf toolIntf = AgileToolFactory.getAgileToolIntf();
		LoginResponse respData = toolIntf.login(request);
		System.out.println("Preparing response" );
		//Response resp = Response.status(200).entity(respData).header("Access-Control-Allow-Origin" , "*").build();
		//System.out.println(resp);
                if(respData.getErrorCode() != 0){
                    throw new Exception("Login failed");
                }
		return respData;
	}

}

package com.bmc.justdoit.smartkanban.api;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.bmc.justdoit.smartkanban.agiletools.AgileToolFactory;
import com.bmc.justdoit.smartkanban.agiletools.AgileToolIntf;
import com.bmc.justdoit.smartkanban.agiletools.SprintQuery;
import com.bmc.justdoit.smartkanban.agiletools.TeamInfo;
import com.bmc.justdoit.smartkanban.agiletools.WorkItem;

/**
 * REST Web Service
 * 
 * @author smijithkp
 */

@Path("/workitem/list")
public class WorkItemResource {
	@Context
	private UriInfo context;

	public WorkItemResource() {

	}

	@POST
	@Produces("application/json")
	public List<WorkItem> workitemList(SprintQuery query) {
		AgileToolIntf toolIntf = AgileToolFactory.getAgileToolIntf();
		return toolIntf.getWorkItems(query.getAttributes(), query);
	}

}

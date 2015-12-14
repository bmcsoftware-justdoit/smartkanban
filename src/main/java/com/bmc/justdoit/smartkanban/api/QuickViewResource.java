package com.bmc.justdoit.smartkanban.api;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.bmc.justdoit.smartkanban.agiletools.AgileToolFactory;
import com.bmc.justdoit.smartkanban.agiletools.AgileToolIntf;
import com.bmc.justdoit.smartkanban.agiletools.PhysicalKanbanStatus;
import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * REST Web Service
 * 
 * @author smijithkp
 */

@Path("/quickview/{workitemid}")
public class QuickViewResource {
	@Context
	private UriInfo context;

	public QuickViewResource() {

	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public WorkItem getWorkItem(Map<String, String> authAttrs, @PathParam("workitemid") String workItemId) {
		AgileToolIntf toolIntf = AgileToolFactory.getAgileToolIntf();
                WorkItem wi = toolIntf.getWorkItem(authAttrs, workItemId);
                Collection<PhysicalKanbanStatus> statuses = toolIntf.getSupportedPhysicalKanbanStatuses();
                List<String> statusList = new ArrayList<String>();
                if(statuses != null){
                    for(PhysicalKanbanStatus status: statuses){
                       statusList.add(status.getLabel()); 
                    }
                }
                wi.setSupportedPhysicalKanbanStatuses(statusList);
		return wi;
	}

}

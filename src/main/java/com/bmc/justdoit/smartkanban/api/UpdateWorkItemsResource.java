/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api;

import com.bmc.justdoit.smartkanban.agiletools.AgileToolFactory;
import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import com.bmc.justdoit.smartkanban.api.objects.ErrorResponse;
import com.bmc.justdoit.smartkanban.api.objects.KanbanResponse;
import com.bmc.justdoit.smartkanban.api.objects.KanbanUpdateRequest;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * REST Web Service
 *
 * @author gokumar
 */
@Path("/workitems/update")
public class UpdateWorkItemsResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public KanbanResponse postJson(KanbanUpdateRequest request) {
        KanbanResponse response = new KanbanResponse();

        try {
                response.setObjectId(request.getRequestId());
                response.setWorkItems(request.getWorkItems());
                Collection<WorkItem> items = AgileToolFactory.getAgileToolIntf().updateWorkItems(request.getAuthAttrs(), request.getWorkItems());
                response.setResult("Updated Work Items successfully!");
        } catch (Exception ex) {
            System.out.println("Updating Work Items failed.");
            System.out.println("Reason: [" + ex.getMessage() + "] ");
            response.setObjectId(request.getRequestId());
            response.setErrorCode(ErrorResponse.NESTED_ERROR);
            response.setErrorMessage(ex.getMessage());
            response.setErrorTrace(ExceptionUtils.getStackTrace(ex));
        }
        return response;
    }
}

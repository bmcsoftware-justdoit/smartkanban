/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api;

import com.bmc.justdoit.smartkanban.api.objects.ErrorResponse;
import com.bmc.justdoit.smartkanban.api.objects.KanbanResponse;
import com.bmc.justdoit.smartkanban.api.objects.KanbanGeneratorRequest;
import com.bmc.justdoit.smartkanban.kanban.queue.KanbanQueue;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * REST Web Service
 *
 * @author gokumar
 */
@Path("/kanban/generate")
public class KanbanGeneratorResource {

    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public KanbanResponse postJson(@Context ServletContext context, KanbanGeneratorRequest request) {
        KanbanResponse response = new KanbanResponse();

        try {
            String requestId = UUID.randomUUID().toString();
            request.setRequestId(requestId);
            String path = context.getRealPath("generator-requests");
            request.setRootPath(path);
            String rootUrl = uriInfo.getBaseUri().toString();
            rootUrl = rootUrl.substring(0, rootUrl.lastIndexOf("/rest"));
            request.setUriPath(rootUrl);
            // System.out.println(">>>>>>>>>> URL " + rootUrl);
            KanbanQueue.CREATOR_QUEUE.add(request);
            response.setObjectId(request.getRequestId());
            response.setResult("Added Kanban board creator request to queue!");
        } catch (Exception ex) {
            System.out.println("Processing Kanban failed.");
            System.out.println("Reason: " + ex.getMessage());
            response.setObjectId(request.getRequestId());
            response.setErrorCode(ErrorResponse.NESTED_ERROR);
            response.setErrorMessage(ex.getMessage());
            response.setErrorTrace(ExceptionUtils.getStackTrace(ex));
        }
        return response;
    }
}

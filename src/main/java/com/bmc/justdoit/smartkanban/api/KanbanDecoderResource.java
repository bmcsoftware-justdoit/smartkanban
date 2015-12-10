/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api;

import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import com.bmc.justdoit.smartkanban.api.objects.ErrorResponse;
import com.bmc.justdoit.smartkanban.api.objects.KanbanResponse;
import com.bmc.justdoit.smartkanban.api.objects.KanbanDecoderRequest;
import com.bmc.justdoit.smartkanban.kanban.decoder.KanbanDecoder;
import com.bmc.justdoit.smartkanban.kanban.error.KanbanException;
import java.util.List;
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
@Path("/kanban/decode")
public class KanbanDecoderResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public KanbanResponse postJson(KanbanDecoderRequest request) {
        KanbanResponse response = new KanbanResponse();

        try {
//            if (request.isAsync()) {
//                KanbanQueue.DECODER_QUEUE.add(request);
//                response.setObjectId(request.getRequestId());
//                response.setResult("Added Kanban board to process queue!");
//            } else {
                KanbanDecoder kanbanDecoder = new KanbanDecoder(request);
                List<WorkItem> workItems = kanbanDecoder.decodeKanbanBoard();
                response.setObjectId(request.getRequestId());
                response.setWorkItems(workItems);
                response.setResult("Decoded Kanban board.");
//            }
        } catch (KanbanException ex) {
            System.out.println("Decoding Kanban failed.");
            System.out.println("Reason: [" + ex.getErrorCode().toString() + "] " + ex.getMessage());
            response.setObjectId(request.getRequestId());
            response.setErrorCode(ErrorResponse.NESTED_ERROR);
            response.setErrorMessage(ex.getErrorCode().toString() + ": " + ex.getMessage());
            response.setErrorTrace(ExceptionUtils.getStackTrace(ex));
        }
        return response;
    }
}

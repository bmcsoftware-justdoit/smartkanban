/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api;

import com.bmc.justdoit.smartkanban.api.objects.ErrorResponse;
import com.bmc.justdoit.smartkanban.api.objects.KanbanDecoderResponse;
import com.bmc.justdoit.smartkanban.api.objects.KanbanDecoderRequest;
import com.bmc.justdoit.smartkanban.kanban.decoder.KanbanDecoder;
import com.bmc.justdoit.smartkanban.kanban.error.KanbanException;
import com.bmc.justdoit.smartkanban.kanban.queue.KanbanQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * REST Web Service
 *
 * @author gokumar
 */
@Path("/kanban/decoder")
public class KanbanDecoderResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public KanbanDecoderResponse postJson(KanbanDecoderRequest request) {
        KanbanDecoderResponse response = new KanbanDecoderResponse();

        try {
            if (request.isAsync()) {
                KanbanQueue.DECODER_QUEUE.add(request);
                response.setObjectId(request.getRequestId());
                response.setResult("Added Kanban board to process queue!");
            } else {
                KanbanDecoder kanbanDecoder = new KanbanDecoder(request);
                kanbanDecoder.decodeKanbanBoard();
                response.setObjectId(request.getRequestId());
                response.setResult("Processed SmartKanban board and updated agile tool successfully.");
            }
        } catch (KanbanException ex) {
            System.out.println("Processing SmartKanban failed.");
            System.out.println("Reason: [" + ex.getErrorCode().toString() + "] " + ex.getMessage());
            response.setObjectId(request.getRequestId());
            response.setErrorCode(ErrorResponse.NESTED_ERROR);
            response.setErrorMessage(ex.getErrorCode().toString() + ": " + ex.getMessage());
            response.setErrorTrace(ExceptionUtils.getStackTrace(ex));
        }
        return response;
    }
}

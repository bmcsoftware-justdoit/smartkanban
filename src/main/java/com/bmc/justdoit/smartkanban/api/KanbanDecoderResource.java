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

    /**
     * Creates a new instance of KanbanDecoderResource
     */
    public KanbanDecoderResource() {

    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public KanbanDecoderResponse postJson(
            @FormDataParam("uploadFile") InputStream fileInputStream,
            @FormDataParam("uploadFile") FormDataContentDisposition fileFormDataContentDisposition,
            @FormDataParam("userName") String userName,
            @FormDataParam("password") String password,
            @DefaultValue("false") @FormDataParam("async") String async) {
        KanbanDecoderResponse response = new KanbanDecoderResponse();

        try {
            String userRootFolder = System.getProperty("user.home");
            String requestId = UUID.randomUUID().toString();
            new File(userRootFolder + "/smartkanban/" + requestId).mkdirs();
            String filePath = userRootFolder + "/smartkanban/" + requestId + "/" + fileFormDataContentDisposition.getFileName();

            // save the file to the server
            saveFile(fileInputStream, filePath);

            System.out.println("File saved to server location : " + filePath);
            
            KanbanDecoderRequest request = new KanbanDecoderRequest();
            Map<String, String> authAttrs = new HashMap<String, String>();
            authAttrs.put("userName", userName);
            authAttrs.put("password", password);
            request.setAuthAttrs(authAttrs);
            request.setRequestId(requestId);
            request.setFileName(fileFormDataContentDisposition.getFileName());

            if(new Boolean(async).booleanValue()){
                KanbanQueue.DECODER_QUEUE.add(request);
                response.setResult("Kanban board uploaded successfully. Added to decode queue for further processing!");
            }else{
                KanbanDecoder kanbanDecoder = new KanbanDecoder(request);
                kanbanDecoder.decodeKanbanBoard();
                response.setResult("SmartKanban processed the uploaded board and updated Jira successfully.");
            }
        } catch (KanbanException ex) {
            System.out.println("SmartKanban processing failed.");
            System.out.println("Reason: [" + ex.getErrorCode().toString() + "] " + ex.getMessage());
            response.setErrorCode(ErrorResponse.NESTED_ERROR);
            response.setErrorMessage(ex.getErrorCode().toString() + ": " + ex.getMessage());
            response.setErrorTrace(ExceptionUtils.getStackTrace(ex));
        } catch (IOException ex) {
            System.out.println("SmartKanban processing failed.");
            System.out.println("Reason: " + ex.getMessage());
            response.setErrorCode(ErrorResponse.NESTED_ERROR);
            response.setErrorMessage("Upload failed! Could not save file on the server.");
            response.setErrorTrace(ExceptionUtils.getStackTrace(ex));
        }
        return response;
    }

    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream,
            String serverLocation) throws FileNotFoundException, IOException {
        OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
        int read = 0;
        byte[] bytes = new byte[1024];
        outpuStream = new FileOutputStream(new File(serverLocation));
        while ((read = uploadedInputStream.read(bytes)) != -1) {
            outpuStream.write(bytes, 0, read);
        }
        outpuStream.flush();
        outpuStream.close();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api;

import com.bmc.justdoit.smartkanban.api.objects.ErrorResponse;
import com.bmc.justdoit.smartkanban.api.objects.KanbanResponse;
import com.bmc.justdoit.smartkanban.api.objects.KanbanImageUploadResponse;
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
@Path("/kanban/imageupload")
public class KanbanImageUploadResource {

    /**
     * Creates a new instance of KanbanDecoderResource
     */
    public KanbanImageUploadResource() {

    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public KanbanImageUploadResponse postJson(
            @FormDataParam("uploadFile") InputStream fileInputStream,
            @FormDataParam("uploadFile") FormDataContentDisposition fileFormDataContentDisposition) {
        KanbanImageUploadResponse response = new KanbanImageUploadResponse();

        System.out.print("File upload request receieved");
        String userRootFolder = System.getProperty("user.home");
        String requestId = UUID.randomUUID().toString();
        String fileName = fileFormDataContentDisposition.getFileName();
        new File(userRootFolder + "/smartkanban/" + requestId).mkdirs();
        String filePath = userRootFolder + "/smartkanban/" + requestId + "/" + fileName;
        try{
            // save the file to the server
            saveFile(fileInputStream, filePath);
        } catch (IOException ex) {
            System.out.println("SmartKanban processing failed.");
            System.out.println("Reason: " + ex.getMessage());
            response.setErrorCode(ErrorResponse.NESTED_ERROR);
            response.setErrorMessage("Upload failed! Could not save file on the server.");
            response.setErrorTrace(ExceptionUtils.getStackTrace(ex));
            
        }
        
        try {
            fileInputStream.close();
        } catch (IOException ex) {
            //This is ok
        }

        System.out.println("File saved to server location : " + filePath);
        response.setFileName(fileName);
        response.setRequestId(requestId);
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
        
        System.out.print("File upload completed....");
        outpuStream.flush();
        outpuStream.close();
    }
}

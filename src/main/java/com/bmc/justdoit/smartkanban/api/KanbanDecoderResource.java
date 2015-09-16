/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api;

import com.bmc.justdoit.smartkanban.api.objects.ErrorResponse;
import com.bmc.justdoit.smartkanban.api.objects.KanbanDecoderResponse;
import com.bmc.justdoit.smartkanban.kanban.Configuration;
import com.bmc.justdoit.smartkanban.kanban.queue.KanbanDecoderQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
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
    @Produces("application/json")
    public KanbanDecoderResponse postJson(@Context ServletContext ctx, FormDataMultiPart form) {
        KanbanDecoderResponse response = new KanbanDecoderResponse();
        
        try {
            FormDataBodyPart filePart = form.getField("file");
            ContentDisposition headerOfFilePart = filePart.getContentDisposition();
            InputStream fileInputStream = filePart.getValueAs(InputStream.class);

            String imageRootFolder = ctx.getRealPath("/WEB-INF/images/");
            String filePath = imageRootFolder + headerOfFilePart.getFileName();
            // save the file to the server
            saveFile(fileInputStream, filePath);

            System.out.println("File saved to server location : " + filePath);
            Configuration.getInstance().setAttr("IMAGES_ROOT_FOLDER", imageRootFolder);
            KanbanDecoderQueue.decoderQueue.add(imageRootFolder + File.pathSeparator + headerOfFilePart.getFileName());
            response.setAttr("result", "Added item to decoder queue");
        } catch (Exception ex) {
            ex.printStackTrace();
            response.setErrorCode(ErrorResponse.NESTED_ERROR);
            response.setErrorMessage("Upload failed!");
            response.setErrorTrace(ex.getStackTrace().toString());
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

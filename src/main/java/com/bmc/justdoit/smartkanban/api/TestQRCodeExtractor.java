/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api;

import com.bmc.justdoit.smartkanban.api.objects.ErrorResponse;
import com.bmc.justdoit.smartkanban.api.objects.LoginRequest;
import com.bmc.justdoit.smartkanban.api.objects.LoginResponse;
import com.bmc.justdoit.smartkanban.api.objects.TestRequest;
import com.bmc.justdoit.smartkanban.api.objects.TestResponse;
import com.bmc.justdoit.smartkanban.kanban.Configuration;
import com.bmc.justdoit.smartkanban.kanban.queue.KanbanCreatorQueue;
import com.bmc.justdoit.smartkanban.kanban.queue.KanbanDecoderQueue;
import com.bmc.justdoit.smartkanban.qrcode.decoder.QRCodeDataExtractor;
import com.bmc.justdoit.smartkanban.qrcode.QRCodeData;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author gokumar
 */
@Path("/test")
public class TestQRCodeExtractor {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AuthenticateResource
     */
    public TestQRCodeExtractor() {
        
    }

    /**
     * @POST @Consumes("application/json")
     * @Produces("application/json") public TestResponse postJson(TestRequest
     * request) {
     *
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public TestResponse postJson(@Context ServletContext ctx, TestRequest request) {
        TestResponse response = null;
        try {
            response = new TestResponse();
            String obj = request.getImageFileName();
            String imageRootFolder = ctx.getRealPath("/WEB-INF/images/");
            Configuration.getInstance().setAttr("IMAGES_ROOT_FOLDER", imageRootFolder);
            KanbanDecoderQueue.decoderQueue.add(imageRootFolder + obj);
//            KanbanCreatorQueue.creatorQueue.add(obj);      
//            List<QRCodeData> qrCodeDataLst = MultipleQRCodeExtractor.decodeDataAndLocation(ctx.getRealPath("/WEB-INF/images/"+request.getImageFileName()));
//            response.setQrCodes(qrCodeDataLst);
            response.setResult("Added item creator/decoder queue");
            response.setQrCodes(new ArrayList<QRCodeData>());
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new TestResponse();
            response.setErrorCode(ErrorResponse.NESTED_ERROR);
            response.setErrorMessage("Could not decode QRCodes from " + request.getImageFileName());
            response.setErrorTrace(ex.getStackTrace().toString());
        }
        return response;
    }
}

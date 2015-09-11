/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api;

import com.bmc.justdoit.smartkanban.api.objects.ErrorResponse;
import com.bmc.justdoit.smartkanban.api.objects.LoginRequest;
import com.bmc.justdoit.smartkanban.api.objects.LoginResponse;
import com.bmc.justdoit.smartkanban.qrcode.MultipleQRCodeExtractor;
import com.bmc.justdoit.smartkanban.qrcode.QRCodeData;
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
     * @Produces("application/json") public LoginResponse postJson(LoginRequest
     * request) {
     *
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public LoginResponse postJson(@Context ServletContext ctx, LoginRequest request) {
        System.out.println("Got request>>>> " + ctx.getContextPath());
        System.out.println("Real Path: " + ctx.getRealPath("/WEB-INF/images/"+request.getOrgId()));
        System.out.println("User file: " + request.getOrgId());
        LoginResponse response = null;
        try {
            response = new LoginResponse();
            response.setObjectId("dummy");
            response.setToken("dummy");
            
            List<QRCodeData> qrCodeDataLst = MultipleQRCodeExtractor.decodeDataAndLocation(ctx.getRealPath("/WEB-INF/images/"+request.getOrgId()));
            System.out.println("Detected QRCodes: " + qrCodeDataLst.size());
            for (QRCodeData qRCodeData : qrCodeDataLst) {
                System.out.println("Location: (" + qRCodeData.getX() + ", " + qRCodeData.getY() + ")");
                System.out.println("Data: " + qRCodeData.getData());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response = new LoginResponse();
            response.setErrorCode(ErrorResponse.NESTED_ERROR);
            response.setErrorMessage("Could not identify and login user " + request.getLoginId());
            response.setErrorTrace(ex.getStackTrace().toString());
        }
        return response;
    }
}

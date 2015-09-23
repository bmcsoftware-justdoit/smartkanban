package com.bmc.justdoit.smartkanban.api;

import com.bmc.justdoit.smartkanban.agiletools.AgileToolFactory;
import com.bmc.justdoit.smartkanban.agiletools.AgileToolIntf;
import com.bmc.justdoit.smartkanban.agiletools.PhysicalKanbanStatus;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import com.bmc.justdoit.smartkanban.kanban.error.ErrorCode;
import com.bmc.justdoit.smartkanban.kanban.error.KanbanException;
import com.bmc.justdoit.smartkanban.qrcode.creator.QRCodeCreator;
import com.google.zxing.WriterException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;

/**
 * REST Web Service
 *
 * @author gokumar
 */
@Path("kanban/stickies/headers")
public class KanbanHeadersStickiesResource {

    @GET
    @Produces("application/json")
    public Collection<PhysicalKanbanStatus> getWorkItem(@Context ServletContext context) throws KanbanException {

        try {
            AgileToolIntf tool = AgileToolFactory.getAgileToolIntf();
            Collection<PhysicalKanbanStatus> statuses = tool.getSupportedPhysicalKanbanStatuses();

            String path = context.getRealPath("images");

            if (!new File(path + "/0.jpg").exists()) {
                int qrCodeHeight = 200;
                int qrCodeWidth = 200;
                for (PhysicalKanbanStatus status : statuses) {
                    String qrCodeData = status.getQrcodeData();
                    Integer key = status.getKey();
                    String qrCodeFileName = key + ".jpg";
                    String qrCodeFilePath = path + "/" + qrCodeFileName;
                    QRCodeCreator.createQRCode(qrCodeData, qrCodeFilePath, qrCodeHeight, qrCodeWidth);
                }
            }
            return statuses;
        } catch (WriterException ex) {
            Logger.getLogger(KanbanHeadersStickiesResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new KanbanException("[ErrorCode: " + ErrorCode.COULD_NOT_CREATE_QR_CODE + "] " + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(KanbanHeadersStickiesResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new KanbanException("[ErrorCode: " + ErrorCode.COULD_NOT_CREATE_QR_CODE + "] " + ex.getMessage());
        }
    }
}

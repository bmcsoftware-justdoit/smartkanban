/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban.creator;

import com.bmc.justdoit.smartkanban.agiletools.AgileToolFactory;
import com.bmc.justdoit.smartkanban.agiletools.AgileToolIntf;
import com.bmc.justdoit.smartkanban.agiletools.SprintQuery;
import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import com.bmc.justdoit.smartkanban.api.objects.KanbanGeneratorRequest;
import com.bmc.justdoit.smartkanban.notification.Mail;
import com.bmc.justdoit.smartkanban.qrcode.creator.QRCodeCreator;
import com.google.zxing.WriterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author gokumar
 */
public class KanbanCreator implements Runnable {

    private final KanbanGeneratorRequest request;

    public KanbanCreator(KanbanGeneratorRequest request) {
        this.request = request;
    }

    public void run() {
        try {
            SprintQuery query = new SprintQuery();
            query.setAttributes(request.getAuthAttrs());
            query.setProject(request.getProject());
            query.setSprint(request.getSprint());
            query.setTeam(request.getTeam());

            AgileToolIntf tool = AgileToolFactory.getAgileToolIntf();
            List<WorkItem> items = tool.getWorkItems(query.getAttributes(), query);
            if (items == null || items.isEmpty()) {
                System.out.println("No work items found for " + query.toString());
                Mail.sendMail(request.getEmailId(), "Kanban Generator Process: Failed!", "[" + request.getRequestId() + "] There are no work items found in Jira for "+ query.toString());
            }
            String path = request.getRootPath();

            String requestFolder = path + "/" + request.getRequestId();
            new File(requestFolder).mkdirs();

            generateStickies(query, items, tool.getSupportedPhysicalKanbanStatuses(), requestFolder);
            
            String subject = "Kanban Generator Process: Successful!";
            StringBuilder body = new StringBuilder();
            String headersUrl = request.getUriPath() + "/headers.html";
            String tasksUrl = request.getUriPath() + "/tasks.html?requestId=" + request.getRequestId();
            
            body.append("To print headers, click ").append(headersUrl);
            body.append("\n");
            body.append("To print tasks' stickies, click ").append(tasksUrl);
            
            boolean sendMailStatus = Mail.sendMail(request.getEmailId(), subject, body.toString());
            System.out.println("Stickies generated.");
            if(sendMailStatus){
                System.out.println("Mail sent successfully.");
            }else{
                System.out.println("Sending mail failed.");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(KanbanCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WriterException ex) {
            Logger.getLogger(KanbanCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KanbanCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void generateStickies(SprintQuery query, List<WorkItem> items, Set<String> columnHeaders, String requestFolder) throws FileNotFoundException, WriterException, IOException {
        int qrCodeWidth = 100; //px
        int qrCodeHeight = 100; //px
        
        for (WorkItem item : items) {
            printWorkItem(item, requestFolder, qrCodeHeight, qrCodeWidth);
            if (item.getSubTaks() == null || item.getSubTaks().isEmpty()) {
                continue;
            }

            for (WorkItem subTask : item.getSubTaks()) {
                printWorkItem(subTask, requestFolder, qrCodeHeight, qrCodeWidth);
            }
        }
    }

    private void printWorkItem(WorkItem item, String directory, int qrCodeHeight, int qrCodeWidth) throws WriterException, IOException {
        String qrCodeFileName = item.getId() + ".jpg";
        String qrCodeFilePath = directory + "/" + qrCodeFileName;
        QRCodeCreator.createQRCode(item.getId(), qrCodeFilePath, qrCodeHeight, qrCodeWidth);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(directory + "/" + item.getId() + ".json"), item);
    }
}

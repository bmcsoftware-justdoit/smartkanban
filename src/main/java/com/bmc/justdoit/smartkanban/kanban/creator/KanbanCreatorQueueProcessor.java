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
import com.bmc.justdoit.smartkanban.kanban.queue.KanbanQueue;
import com.bmc.justdoit.smartkanban.qrcode.creator.QRCodeCreator;
import com.google.zxing.WriterException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author gokumar
 */
public class KanbanCreatorQueueProcessor extends HttpServlet implements Runnable {

    @Override
    public void init() throws ServletException {
        System.out.println("Initializing KanbanCreator Queue");
        new Thread(new KanbanCreatorQueueProcessor()).start();
    }

    /*public void run() {
        while (true) {
            try {
                synchronized (KanbanQueue.CREATOR_QUEUE) {
                    while (!KanbanQueue.CREATOR_QUEUE.isEmpty()) {
                        String request = KanbanQueue.CREATOR_QUEUE.poll().toString();
                        System.out.println("Got a request to create >>>>>>>> " + request);
                        KanbanCreator kanbanCreator = new KanbanCreator(request, null);
                        Thread th = new Thread(kanbanCreator);
                        th.start();
                    }
                }
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(KanbanCreatorQueueProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }*/
    
    public void run(){
        while(true){
            SprintQuery query = KanbanQueue.STICKY_REQUEST_QUEUE.poll();
            if(query == null) continue;
            
            AgileToolIntf tool =     AgileToolFactory.getAgileToolIntf();
            List<WorkItem> items = tool.getWorkItems(query.getAttributes(), query);
            if(items == null || items.size() == 0){
                System.out.println("No work items found for " + query.toString());
                //TODO e-mail this 
                continue;
            }
            String requestId =  generateStickies(query, items, tool.getSupportedPhysicalKanbanStatuses());
            System.out.println("stickies.html generated under " + requestId);
        }
    }
    
    
    private String generateStickies(SprintQuery query, List<WorkItem> items, Set<String> columnHeaders){
        
        System.out.print("File upload request receieved");
        String userRootFolder = System.getProperty("user.home");
        String requestId = UUID.randomUUID().toString();
        String fileName = "stickies.html";
        String directory = userRootFolder + "/smartkanban/" + requestId;
        new File(directory).mkdirs();
        String filePath = directory + "/" + fileName;
        try{
            PrintWriter writer = new PrintWriter(filePath);
            
            writer.println("<html>\n" +
"<body>\n" +
"	<style type=\"text/css\">\n" +
"		.sticky{\n" +
"			display: inline-block;\n" +
"			border-style: solid;\n" +
"			border-width: 1px;\n" +
"			border-color: grey;\n" +
"			padding: 5px;\n" +
"		}\n" +
"		.stickyHeader{\n" +
"\n" +
"			display: block;\n" +
"			height: 110px;\n" +
"			border-style: solid;\n" +
"			border-width: 0px;\n" +
"			border-color: grey;\n" +
"			padding: 5px;\n" +
"\n" +
"		}\n" +
"\n" +
"		.stickySummary{\n" +
"			display: inline-block;\n" +
"			float: left;\n" +
"			width: 200px;\n" +
"			padding-top:5px \n" +
"		}\n" +
"\n" +
"		.qrcode{	\n" +
"			display: inline-block;\n" +
"			width: 100px;\n" +
"			height: 100px;\n" +
"			border-style: solid;\n" +
"			border-width: 0px;\n" +
"			border-color: grey;\n" +
"			padding: 5px;\n" +
"		}\n" +
"		.headerLabel{\n" +
"			padding: 1px;\n" +
"			display: block;\n" +
"		}\n" +
"		.task{\n" +
"			padding-top: 10px;\n" +
"			display: block;\n" +
"			width: 320px;\n" +
"		}\n" +
"		.pin{\n" +
"			width: 150;\n" +
"			display: block;\n" +
"			text-align: right;\n" +
"		}\n" +
"\n" +
"		.headerPin{\n" +
"			width: 150;\n" +
"			display: block;\n" +
"			text-align: left;\n" +
"			float: right;\n" +
"		}\n" +
"\n" +
"		.statusHeaderName{\n" +
"			display: inline-block;\n" +
"			float: right;\n" +
"			width: 200px;\n" +
"			padding-top:5px \n" +
"		}\n" +
"\n" +
"		.statusHeaderLabel{\n" +
"			padding: 30px;\n" +
"			display: block;\n" +
"			font-size: 22px;\n" +
"		}\n" +
"\n" +
"	</style>\n" +
"</body>\n" +
"<head>\n" +
"	<title>Smart Kanban Stickies</title>\n" +
"</head>\n" +
"<body>\n" +
"	<h2> Status Header Stickies </h2>");
            
            //generator status colum headers 
            int qrCodeWidth  = 100; //px
            int qrCodeHeight  = 100; //px
            int headerIdx = 0;
            for(String coulmnHeader : columnHeaders){
                String qrCodeFileName = headerIdx + ".jpg";
                String qrCodeFilePath = directory + "/" + qrCodeFileName;
                QRCodeCreator.createQRCode(coulmnHeader, qrCodeFilePath,qrCodeHeight, qrCodeWidth);
                writer.println("<div class=\"sticky\">\n" +
"		<div class=\"stickyHeader\"> \n" +
"			<img class=\"qrcode\" src=\"" + qrCodeFileName +"\">\n" +
"			<div class=\"statusHeaderName\">\n" +
"				<div class=\"headerPin\"><img src=\"pin.jpg\"></div>\n" +
"				<label class=\"statusHeaderLabel\"><b>" + coulmnHeader +"</b></label>			\n" +
"			</div>\n" +
"			\n" +
"		</div>		\n" +
"		\n" +
"	</div>");
            }
            
            writer.println("<h2> Work stickies </h2>");
            
            for(WorkItem item : items){
                writer.println("<h3> Stickies for " + item.getId() + "-" + item.getTitle() + "</h3>");
                
                printWorkItem(writer, item, directory,qrCodeHeight, qrCodeWidth );
                if(item.getSubTaks() == null || item.getSubTaks().size() == 0) continue;
                
                for(WorkItem subTask : item.getSubTaks() ){
                    printWorkItem(writer, subTask, directory,qrCodeHeight, qrCodeWidth );
                }
            }
            
            writer.println("/body>\n" +
"</html>");
            
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            System.out.println("Stikcy creation failed");
            System.out.println("Reason: " + ex.getMessage());
            
        } catch (WriterException ex) {
            Logger.getLogger(KanbanCreatorQueueProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        System.out.println("File saved to server location : " + filePath);
       
        return requestId;
    }

    private void printWorkItem(PrintWriter writer, WorkItem item, String directory, int qrCodeHeight, int qrCodeWidth) throws WriterException, IOException {
        String qrCodeFileName = item.getId() + ".jpg";
        String qrCodeFilePath = directory + "/" + qrCodeFileName;
        QRCodeCreator.createQRCode(item.getId(), qrCodeFilePath,qrCodeHeight, qrCodeWidth);
        writer.println("<div class=\"sticky\">\n" +
"		<div class=\"stickyHeader\"> \n" +
"\n" +
"			<div class=\"stickySummary\">\n" +
"				<div class=\"pin\"><img src=\"pin.jpg\"></div>\n" +
"				<label class=\"headerLabel\">" + item.getType() + "</label>\n" +
"				<label class=\"headerLabel\"><b>"+ item.getId()+"</b></label>\n" +
"				<label class=\"headerLabel\">"+item.getAssignee()+"</label>\n" +
"				<label class=\"headerLabel\">Estimation: 1d</label>			\n" +
"			</div>\n" +
"			<img class=\"qrcode\" src=\""+qrCodeFileName+"\">\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"task\">\n" +
item.getTitle() +
"		</div>	\n" +
"	</div>");
        
    }
}

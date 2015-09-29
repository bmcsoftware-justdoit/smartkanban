/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban.decoder;

import com.bmc.justdoit.smartkanban.agiletools.AgileToolFactory;
import com.bmc.justdoit.smartkanban.agiletools.PhysicalKanbanStatus;
import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import com.bmc.justdoit.smartkanban.api.objects.KanbanDecoderRequest;
import com.bmc.justdoit.smartkanban.kanban.error.ErrorCode;
import com.bmc.justdoit.smartkanban.kanban.error.KanbanException;
import com.bmc.justdoit.smartkanban.notification.Mail;
import com.bmc.justdoit.smartkanban.qrcode.decoder.QRCodeDataExtractor;
import com.bmc.justdoit.smartkanban.qrcode.QRCodeData;
import com.bmc.justdoit.smartkanban.qrcode.QRCodeDataCompareX;
import com.google.zxing.NotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author gokumar
 */
public class KanbanDecoder implements Runnable {

    private final String fileName;
    private final String requestId;
    private final Map<String, String> authAttrs;
    private final boolean async;

    public KanbanDecoder(KanbanDecoderRequest request) {
        this.requestId = request.getRequestId();
        this.fileName = request.getFileName();
        this.authAttrs = request.getAuthAttrs();
        this.async = request.isAsync();
    }

    private List<String> headerNames;

    public void run() {
        try {
            decodeKanbanBoard();
        } catch (Exception ex) {
            Logger.getLogger(KanbanDecoder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<WorkItem> decodeKanbanBoard() throws KanbanException {
        List<QRCodeData> qrCodes;
        try {
            System.out.println("FileName>>> " + fileName);
            String userRootFolder = System.getProperty("user.home");
            String filePath = userRootFolder + "/agilebuddy/" + requestId + "/" + fileName;
            qrCodes = QRCodeDataExtractor.decodeQRCodeData(filePath);
            for (QRCodeData qrCode : qrCodes) {
                System.out.println(qrCode.getData() + ":" + qrCode.getPoint().toString());
            }

            if (qrCodes == null) {
                KanbanException e = new KanbanException("Could not decode/identify any QRCodes. Please retry!");
                e.setErrorCode(ErrorCode.NO_QR_CODES_IDENTIFIED);
                throw e;
            } else {
                //Identify headers and extract to a separate list
                List<QRCodeData> headers = new ArrayList<QRCodeData>();
                String qrCodeData;
                String present;
                Set<String> labels = new HashSet<String>();
                for (PhysicalKanbanStatus status : AgileToolFactory.getAgileToolIntf().getSupportedPhysicalKanbanStatuses()) {
                    labels.add(status.getQrcodeData());
                }

                for (QRCodeData qrCode : qrCodes) {
                    qrCodeData = qrCode.getData();
                    present = null;
                    for (String header : labels) {
                        if (qrCodeData.contains(header)) {
                            present = header;
                            break;
                        }
                    }
                    if (present != null) {
                        qrCode.setData(present);
                        headers.add(qrCode);
                    }
                }

                if (headers.isEmpty()) {
                    KanbanException e = new KanbanException("Headers are missing in the Kanban board. Please retry!");
                    e.setErrorCode(ErrorCode.NO_HEADERS_AVAILABLE);
                    throw e;
                }

                Collections.sort(headers, new QRCodeDataCompareX());

                // Isolate tasks by removing all headers from qrCode list
                qrCodes.removeAll(headers);

                headerNames = new ArrayList<String>();
                for (QRCodeData headerQRCode : headers) {
                    String headerName = getHeaderName(headerQRCode.getData());
                    headerNames.add(headerName);
                }

                Map<String, List<QRCodeData>> columnWiseQRCodeData = this.prepareColumnWiseQRCodeData(headers, qrCodes);
                List<WorkItem> workItems = new ArrayList<WorkItem>();
                WorkItem workItem;

                for (Map.Entry<String, List<QRCodeData>> column : columnWiseQRCodeData.entrySet()) {
                    System.out.println("Tasks under " + column.getKey());
                    for (QRCodeData qRCodeData : column.getValue()) {
                        workItem = AgileToolFactory.getAgileToolIntf().getWorkItem(authAttrs, qRCodeData.getData());
                        workItem.setPhysicalKanbanStatus(column.getKey());

                        System.out.println(qRCodeData.getData() + ":" + qRCodeData.getPoint().toString());
                        workItems.add(workItem);
                    }
                    System.out.println("------------------------------");
                }
                
                filePath = userRootFolder + "/agilebuddy/" + requestId + "/update.json";
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new File(filePath), workItems);
                
                return workItems;

                // AgileToolFactory.getAgileToolIntf().updateWorkItems(authAttrs, workItems);
                /* if (status) {
                    Mail.sendMail(authAttrs.get("loginId") + "@bmc.com", "Kanban Decoder Process: Successful!", "[" + requestId + "] Successfully processed Kanban board and updated Jira!");
                }else{
                    Mail.sendMail(authAttrs.get("loginId") + "@bmc.com", "Kanban Decoder Process: Failed!", "[" + requestId + "] Failed to process Kanban board and updated Jira! Please try again later.");
                }*/
            }

        } catch (IOException ex) {
            KanbanException e = new KanbanException("Encounted error during decode! Please contact AgileBuddy Admin!");
            e.setErrorCode(ErrorCode.COULD_NOT_DECODE_KANBAN);
            Mail.sendMail(authAttrs.get("loginId") + "@bmc.com", "Kanban Decoder Process: Failed!", "[" + requestId + "] Failed to process Kanban board and updated Jira! Please try again later." + e.getMessage());
            throw e;
        } catch (NotFoundException ex) {
            KanbanException e = new KanbanException("No QR Codes identified in the Kanban board! Please retry with different image!");
            e.setErrorCode(ErrorCode.NO_QR_CODES_IDENTIFIED);
            Mail.sendMail(authAttrs.get("loginId") + "@bmc.com", "Kanban Decoder Process: Failed!", "[" + requestId + "] Failed to process Kanban board and updated Jira! Please try again later." + e.getMessage());
            throw e;
        }
    }

    private Map<String, List<QRCodeData>> prepareColumnWiseQRCodeData(List<QRCodeData> headers, List<QRCodeData> tasks) {
        Map<String, List<QRCodeData>> columnWiseQRCodeData = new HashMap<String, List<QRCodeData>>();
        for (int headerIndx = 0; headerIndx < headers.size(); headerIndx++) {
            QRCodeData currentHeader = headers.get(headerIndx);
            float headerWidth;
            if (headerIndx + 1 == headers.size()) {
                headerWidth = Float.MAX_VALUE - (Float) headers.get(headerIndx).getPoint().x;
            } else {
                headerWidth = (Float) headers.get(headerIndx + 1).getPoint().x
                        - (Float) headers.get(headerIndx).getPoint().x;
            }
            float leftMargin = (Float) currentHeader.getPoint().x;
            float rightMargin = leftMargin + headerWidth;
            List<QRCodeData> currentHeaderTasks = columnWiseQRCodeData.get(headerNames.get(headerIndx));
            if (null == currentHeaderTasks) {
                currentHeaderTasks = new ArrayList<QRCodeData>();
            }

            for (QRCodeData task : tasks) {
                float x = (Float) task.getPoint().x;
                if (x >= leftMargin && x < rightMargin) {
                    currentHeaderTasks.add(task);
                }
            }
            // columnWiseQRCodeData.put(headerNames.get(headerIndx) + ":" + leftMargin + "_" + rightMargin, currentHeaderTasks);
            columnWiseQRCodeData.put(headerNames.get(headerIndx), currentHeaderTasks);
        }
        return columnWiseQRCodeData;
    }

    private String getHeaderName(String data) {
        String label = null;
        for (PhysicalKanbanStatus status : AgileToolFactory.getAgileToolIntf().getSupportedPhysicalKanbanStatuses()) {
            if(status.getQrcodeData().equals(data)){
                label = status.getLabel();
                break;
            }
        }
        return label;
    }
}

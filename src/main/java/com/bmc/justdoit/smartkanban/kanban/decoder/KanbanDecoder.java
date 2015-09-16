/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban.decoder;

import com.bmc.justdoit.smartkanban.agiletools.AgileToolFactory;
import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import com.bmc.justdoit.smartkanban.api.objects.KanbanDecoderRequest;
import com.bmc.justdoit.smartkanban.kanban.KanbanHeaders;
import com.bmc.justdoit.smartkanban.qrcode.decoder.QRCodeDataExtractor;
import com.bmc.justdoit.smartkanban.qrcode.QRCodeData;
import com.bmc.justdoit.smartkanban.qrcode.QRCodeDataCompareX;
import com.bmc.justdoit.smartkanban.qrcode.QRCodeDataCompareY;
import com.google.zxing.NotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gokumar
 */
public class KanbanDecoder implements Runnable {

    private String fileName;
    private String requestId;
    private Map<String, String> authAttrs;

    public KanbanDecoder(KanbanDecoderRequest request) {
        this.requestId = request.getRequestId();
        this.fileName = request.getFileName();
        this.authAttrs = request.getAuthAttrs();
    }

    private List<String> headerNames;

    public void run() {
        try {
            decodeKanbanBoard();
        } catch (Exception ex) {
            Logger.getLogger(KanbanDecoder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void decodeKanbanBoard() throws Exception {
        List<QRCodeData> qrCodes;
        try {
            System.out.println("FileName>>> " + fileName);
            String userRootFolder = System.getProperty("user.home");
            String filePath = userRootFolder + "/smartkanban/" + requestId + "/" + fileName;
            qrCodes = QRCodeDataExtractor.decodeQRCodeData(filePath);

            Collections.sort(qrCodes, new QRCodeDataCompareY());
            List<QRCodeData> headers = qrCodes.subList(0, KanbanHeaders.getHeaders().size());
            List<QRCodeData> tasks = qrCodes.subList(KanbanHeaders.getHeaders().size(), qrCodes.size());

            headerNames = new ArrayList<String>();
            Collections.sort(headers, new QRCodeDataCompareX());
            for (QRCodeData headerQRCode : headers) {
                String headerName = headerQRCode.getData();
                if (KanbanHeaders.getHeaders().contains(headerName)) {
                    headerNames.add(headerName);
                } else {
                    throw new Exception(headerName + " is not identified as supported header. Please contact SmartKanban Admin!");
                }
            }

            Map<String, List<QRCodeData>> columnWiseQRCodeData = this.prepareColumnWiseQRCodeData(headers, tasks);
            WorkItem workItem;

            for (Map.Entry<String, List<QRCodeData>> column : columnWiseQRCodeData.entrySet()) {
                System.out.println("Tasks under " + column.getKey());
                for (QRCodeData qRCodeData : column.getValue()) {
                    workItem = new WorkItem();
                    workItem.setId(qRCodeData.getData());
                    workItem.setStatus(column.getKey());

                    AgileToolFactory.getAgileToolIntf().updateWorkItem(authAttrs, workItem);
                    System.out.println(qRCodeData.getData());
                }
                System.out.println("------------------------------");
            }

        } catch (IOException ex) {
            Logger.getLogger(KanbanDecoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotFoundException ex) {
            Logger.getLogger(KanbanDecoder.class.getName()).log(Level.SEVERE, null, ex);
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
            columnWiseQRCodeData.put(headerNames.get(headerIndx), currentHeaderTasks);
        }
        return columnWiseQRCodeData;
    }
}

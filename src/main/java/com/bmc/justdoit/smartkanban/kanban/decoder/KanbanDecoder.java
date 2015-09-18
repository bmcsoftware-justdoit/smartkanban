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
import com.bmc.justdoit.smartkanban.kanban.error.ErrorCode;
import com.bmc.justdoit.smartkanban.kanban.error.KanbanException;
import com.bmc.justdoit.smartkanban.qrcode.decoder.QRCodeDataExtractor;
import com.bmc.justdoit.smartkanban.qrcode.QRCodeData;
import com.bmc.justdoit.smartkanban.qrcode.QRCodeDataCompareX;
import com.bmc.justdoit.smartkanban.qrcode.QRCodeDataCompareY;
import com.google.zxing.NotFoundException;
import com.google.zxing.qrcode.encoder.QRCode;
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

    public void decodeKanbanBoard() throws KanbanException {
        List<QRCodeData> qrCodes;
        try {
            System.out.println("FileName>>> " + fileName);
            String userRootFolder = System.getProperty("user.home");
            String filePath = userRootFolder + "/smartkanban/" + requestId + "/" + fileName;
            qrCodes = QRCodeDataExtractor.decodeQRCodeData(filePath);
            for (QRCodeData qrCode : qrCodes) {
                System.out.println(qrCode.getData() + ":" + qrCode.getPoint().toString());
            }

            if (qrCodes == null) {
                KanbanException e = new KanbanException("Could not decode/identify any QRCodes. Please retry!");
                e.setErrorCode(ErrorCode.NO_QR_CODES_IDENTIFIED);
                throw e;
            } /*else if (qrCodes.size() < KanbanHeaders.getHeaders().size()) {
                KanbanException e = new KanbanException("Required headers are missing in the Kanban board. Please retry!");
                e.setErrorCode(ErrorCode.INVALID_OR_NO_HEADERS_AVAILABLE);
                throw e;
            } else if (qrCodes.size() <= KanbanHeaders.getHeaders().size()) {
                KanbanException e = new KanbanException("No work items found to update Jira!");
                e.setErrorCode(ErrorCode.NO_WORKITEMS_FOUND);
                throw e;
            }*/ else {
                //Identify headers and extract to a separate list
                List<QRCodeData> headers = new ArrayList<QRCodeData>();
                for (QRCodeData qrCode : qrCodes) {
                    String qrCodeData = qrCode.getData();
                    boolean present = false;
                    for (String header : KanbanHeaders.getHeaders()) {
                        if (qrCodeData.contains(header)) {
                            present = true;
                            break;
                        }
                    }
                    if (present) {
                        headers.add(qrCode);
                    }
                }

                if (headers.isEmpty()) {
                    KanbanException e = new KanbanException("Headers are missing in the Kanban board. Please retry!");
                    e.setErrorCode(ErrorCode.NO_HEADERS_AVAILABLE);
                    throw e;
                }

                Collections.sort(headers, new QRCodeDataCompareX());
                
                // Isolate tasks by removing all headers from qrCodse list
                qrCodes.removeAll(headers);

                headerNames = new ArrayList<String>();
                for (QRCodeData headerQRCode : headers) {
                    String headerName = headerQRCode.getData();
                    headerNames.add(headerName);
                }

                Map<String, List<QRCodeData>> columnWiseQRCodeData = this.prepareColumnWiseQRCodeData(headers, qrCodes);
                WorkItem workItem;

                for (Map.Entry<String, List<QRCodeData>> column : columnWiseQRCodeData.entrySet()) {
                    System.out.println("Tasks under " + column.getKey());
                    for (QRCodeData qRCodeData : column.getValue()) {
                        workItem = new WorkItem();
                        workItem.setId(qRCodeData.getData());
                        workItem.setStatus(column.getKey());

                        AgileToolFactory.getAgileToolIntf().updateWorkItem(authAttrs, workItem);
                        System.out.println(qRCodeData.getData() + ":" + qRCodeData.getPoint().toString());
                    }
                    System.out.println("------------------------------");
                }
            }

        } catch (IOException ex) {
            KanbanException e = new KanbanException("Encounted error during decode! Please contact SmartKanban Admin!");
            e.setErrorCode(ErrorCode.COULD_NOT_DECODE_KANBAN);
            throw e;
        } catch (NotFoundException ex) {
            KanbanException e = new KanbanException("No QR Codes identified in the Kanban board! Please retry with different image!");
            e.setErrorCode(ErrorCode.NO_QR_CODES_IDENTIFIED);
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
            columnWiseQRCodeData.put(headerNames.get(headerIndx) + ":" + leftMargin + "_" + rightMargin, currentHeaderTasks);
        }
        return columnWiseQRCodeData;
    }
}

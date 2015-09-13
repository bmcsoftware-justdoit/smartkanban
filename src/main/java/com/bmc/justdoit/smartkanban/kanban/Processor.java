/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban;

import com.bmc.justdoit.smartkanban.kanban.location.Point;
import com.bmc.justdoit.smartkanban.qrcode.MultipleQRCodeExtractor;
import com.bmc.justdoit.smartkanban.qrcode.QRCodeData;
import com.google.zxing.NotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gokumar
 */
public class Processor {

    public void processKanbanBoard(String filePath) {
        List<QRCodeData> qrCodes;
        try {
            qrCodes = MultipleQRCodeExtractor.decodeDataAndLocation(filePath);
            List<QRCodeData> headers = this.getHeaderQRCodes(qrCodes);
            List<QRCodeData> tasks = this.getNonHeaders(qrCodes);
            
            Map<String, List<QRCodeData>> columnWiseQRCodeData = this.prepareColumnWiseQRCodeData(headers, tasks);
            
        } catch (IOException ex) {
            Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotFoundException ex) {
            Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<QRCodeData> getHeaderQRCodes(List<QRCodeData> qrCodes) {
        List<Point> headerLocations = new ArrayList<Point>();
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private List<QRCodeData> getNonHeaders(List<QRCodeData> qrCodes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Map<String, List<QRCodeData>> prepareColumnWiseQRCodeData(List<QRCodeData> headers, List<QRCodeData> tasks) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

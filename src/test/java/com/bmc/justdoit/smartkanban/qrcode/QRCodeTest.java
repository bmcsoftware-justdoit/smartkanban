/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.qrcode;

import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;

/**
 *
 * @author gokumar
 */
public class QRCodeTest {

    /**
     * Test of createQRCode method, of class QRCode.
     */
    @org.junit.Test
    public void testCreateQRCode() {
        System.out.println("createQRCode");
        String qrCodeData = "UnitTestCreate";
        String filePath = "./testQRCodeCreate.png";
        int qrCodeheight = 124;
        int qrCodewidth = 124;
        QRCode instance = new QRCode();
        try {
            instance.createQRCode(qrCodeData, filePath, qrCodeheight, qrCodewidth);
            assertEquals("UnitTestCreate", instance.readQRCode(filePath));
        } catch (WriterException ex) {
            Logger.getLogger(QRCodeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QRCodeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotFoundException ex) {
            Logger.getLogger(QRCodeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Test of createQRCode method, of class QRCode.
     */
    @org.junit.Test
    public void testCreateQRCodeThrowException() {
        System.out.println("createQRCodeThrowException");
        String qrCodeData = "UnitTestCreate";
        String filePath = "./testQRCodeCreateThrowException";
        int qrCodeheight = 124;
        int qrCodewidth = 124;
        QRCode instance = new QRCode();
        try {
            instance.createQRCode(qrCodeData, filePath, qrCodeheight, qrCodewidth);
        } catch (WriterException ex) {
            Logger.getLogger(QRCodeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            assertTrue(true);
        }
    }

    /**
     * Test of readQRCode method, of class QRCode.
     */
    @org.junit.Test
    public void testReadQRCode() {
        System.out.println("readQRCode");
        String filePath = "./testQRCodeRead.png";
        String expResult = "UnitTestRead";
        int qrCodeheight = 128;
        int qrCodewidth = 128;
        QRCode instance = new QRCode();
        try {
            instance.createQRCode(expResult, filePath, qrCodeheight, qrCodewidth);
            assertEquals(expResult, instance.readQRCode(filePath));
        } catch (WriterException ex) {
            Logger.getLogger(QRCodeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QRCodeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotFoundException ex) {
            Logger.getLogger(QRCodeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of readQRCode method, of class QRCode. Force to throw exception
     */
    @org.junit.Test
    public void testReadQRCodeThrowException() {
        System.out.println("readQRCodeThrowException");
        String filePath = "./testQRCodeRead.png";
        int qrCodeheight = 128;
        int qrCodewidth = 128;
        QRCode instance = new QRCode();
        try {
            instance.createQRCode("UnitTestRead", filePath, qrCodeheight, qrCodewidth);
            String expResult = "UnitTestRead";
            String result = instance.readQRCode("dummy_" + filePath);
        } catch (WriterException ex) {
            Logger.getLogger(QRCodeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            assertTrue(true);
        } catch (NotFoundException ex) {
            Logger.getLogger(QRCodeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

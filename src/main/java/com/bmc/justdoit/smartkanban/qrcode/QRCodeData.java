/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.qrcode;

/**
 *
 * @author gokumar
 */
public class QRCodeData {
    
    public QRCodeData(double x, double y, String data){
        this.x = x;
        this.y = y;
        this.data = data;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    double x;
    double y;
    String data;
}

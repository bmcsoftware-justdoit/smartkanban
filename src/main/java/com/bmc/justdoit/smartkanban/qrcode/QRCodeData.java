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
    
    public QRCodeData(float x, float y, String data){
        this.x = x;
        this.y = y;
        this.data = data;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    float x;
    float y;
    String data;
}

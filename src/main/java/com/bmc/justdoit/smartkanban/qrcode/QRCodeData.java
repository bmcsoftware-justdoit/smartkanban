/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.qrcode;

import com.bmc.justdoit.smartkanban.qrcode.location.Point;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gokumar
 */
@XmlRootElement
public class QRCodeData {
    
    public QRCodeData(Point point, String data){
        this.point = point;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
    
    Point point;
    String data;
}

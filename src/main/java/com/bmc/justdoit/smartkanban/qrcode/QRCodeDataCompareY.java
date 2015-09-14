/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.qrcode;

import java.util.Comparator;

/**
 *
 * @author gokumar
 */
public class QRCodeDataCompareY 
        implements Comparator<QRCodeData> {

    public int compare(final QRCodeData a, final QRCodeData b) {

        if ((Float) a.getPoint().y < (Float) b.getPoint().y) {
            return -1;
        } else if ((Float) a.getPoint().y > (Float) b.getPoint().y) {
            return 1;
        } else {
            return 0;
        }
    }
}

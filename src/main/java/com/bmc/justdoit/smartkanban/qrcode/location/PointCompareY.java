/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.qrcode.location;

import java.util.Comparator;

/**
 *
 * @author gokumar
 */
public class PointCompareY 
        implements Comparator<Point> {

    public int compare(final Point a, final Point b) {

        if ((Float) a.y < (Float) b.y) {
            return -1;
        } else if ((Float) a.y > (Float) b.y) {
            return 1;
        } else {
            return 0;
        }
    }
}

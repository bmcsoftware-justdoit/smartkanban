/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.qrcode.location;

/**
 *
 * @author gokumar
 */
public class Point<X, Y> {
    public X x;
    public Y y;

    public Point(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}

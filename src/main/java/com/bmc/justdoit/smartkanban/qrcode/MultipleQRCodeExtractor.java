/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.qrcode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author gokumar
 */
public class MultipleQRCodeExtractor {

    public static List<QRCodeData> decodeDataAndLocation(String filePath) throws IOException, NotFoundException {
        QRCodeMultiReader multiReader = new QRCodeMultiReader();
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(new FileInputStream(filePath)))));
        Map hintMap = new HashMap();
        hintMap.put(DecodeHintType.TRY_HARDER, true);
        hintMap.put(DecodeHintType.CHARACTER_SET, "ISO-8859-1");
        Result[] results = multiReader.decodeMultiple(binaryBitmap, hintMap);
        List<QRCodeData> decodedQRCodeData = new ArrayList<QRCodeData>();
        for (Result result : results) {
            ResultPoint[] rp = result.getResultPoints();
            Float x = null;
            Float y = null;
            List<Point> points = new ArrayList<Point>();

            for (ResultPoint resultPoint : rp) {
                x = resultPoint.getX();
                y = resultPoint.getY();
//                System.out.println("RP>>>>X " + x);
//                System.out.println("RP>>>>Y " + y);
                Point p = new Point(x, y);
                points.add(p);
            }
            Point finalLocation = MultipleQRCodeExtractor.getTopLeftXY(points);
//            System.out.println("Result:::: " + result.getText());
//            System.out.println("TLC: (" + finalLocation.x + ", " + finalLocation.y + ")");
//            System.out.println("----------------------------------");
            decodedQRCodeData.add(new QRCodeData((Float) finalLocation.x, (Float) finalLocation.y, result.getText()));
        }
        System.out.println("Total: " + decodedQRCodeData.size());
        return decodedQRCodeData;
    }

    private static Point getTopLeftXY(List<Point> points) {
        Collections.sort(points, new PointCompareX());
        List<Point> newPoints = new ArrayList<Point>();
        newPoints.add(points.get(0));
        newPoints.add(points.get(1));
        Collections.sort(newPoints, new PointCompareY());

        return newPoints.get(0);
    }
}

class Point<X, Y> {

    public final X x;
    public final Y y;

    public Point(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}

class PointCompareX
        implements Comparator<Point> {

    public int compare(final Point a, final Point b) {

        if ((Float) a.x < (Float) b.x) {
            return -1;
        } else if ((Float) a.x > (Float) b.x) {
            return 1;
        } else {
            return 0;
        }
    }
}

class PointCompareY
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

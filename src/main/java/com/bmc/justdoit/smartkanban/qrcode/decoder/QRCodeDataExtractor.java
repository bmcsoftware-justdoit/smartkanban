/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.qrcode.decoder;

import com.bmc.justdoit.smartkanban.qrcode.QRCodeData;
import com.bmc.justdoit.smartkanban.qrcode.location.Point;
import com.bmc.justdoit.smartkanban.qrcode.location.PointCompareX;
import com.bmc.justdoit.smartkanban.qrcode.location.PointCompareY;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author gokumar
 */
public class QRCodeDataExtractor {
    
    public static List<QRCodeData> decodeQRCodeData(String filePath) throws IOException, NotFoundException {
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
            Point finalLocation = QRCodeDataExtractor.getTopLeftXY(points);
//            System.out.println("Result:::: " + result.getText());
//            System.out.println("TLC: (" + finalLocation.x + ", " + finalLocation.y + ")");
//            System.out.println("----------------------------------");
            decodedQRCodeData.add(new QRCodeData(finalLocation, result.getText()));
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.qrcode;

import com.google.zxing.NotFoundException;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author gokumar
 */
public class QRCodeProcessor {

    public static List<QRCodeData> decodeDataAndFetchQRCodeLocations(
            String filePath, String templatePath) throws NotFoundException,
            IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread(filePath);
        Mat template = Imgcodecs.imread(templatePath);

        int result_cols = image.cols() - template.cols() + 1;
        int result_rows = image.rows() - template.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
        List<Tuple> locs = new ArrayList<Tuple>();

        int indx = 0;
        List<QRCodeData> qrCodeDataLst = new ArrayList<QRCodeData>();

        while (true) {
            Imgproc.matchTemplate(image, template, result,
                    Imgproc.TM_SQDIFF_NORMED);
            Core.normalize(result, result, 0.9, 1, Core.NORM_MINMAX, -1);

            Core.MinMaxLocResult res = Core.minMaxLoc(result);

            if (res.maxVal < 0.9) {
                System.out.println("No matches");
                break;
            }

            Point tlc = res.minLoc;
            double x = res.minLoc.x;
            double y = res.minLoc.y;
            Tuple xyTuple = new Tuple(x, y);
            boolean overlap = false;
            for (Tuple loc : locs) {
                if (QRCodeProcessor.doesItOverlap((Double) loc.x,
                        (Double) loc.y, x, y, 134, 134)) {
                    overlap = true;
                    break;
                }
            }
            if (overlap) {
                break;
            }
            locs.add(xyTuple);

            Point brc = new Point((x + template.cols() + 134),
                    (y + template.rows() + 134));
            Rect r = new Rect(tlc, brc);
            Mat newR = image.submat(r);

            String tmpFilePath = "resources/tmp/" + indx + ".png";
            Imgcodecs.imwrite(tmpFilePath, newR);

            QRCode qrCode = new QRCode();
            String data = qrCode.readQRCode(tmpFilePath);
            QRCodeData qrCodeData = new QRCodeData(x, y, data);
            qrCodeDataLst.add(qrCodeData);
        }

        return qrCodeDataLst;
    }

    private static boolean doesItOverlap(double x, double y, double x1,
            double y1, double width, double height) {
        return (x1 >= x && x1 <= (x + width)) && (y1 >= y && y1 <= (y + height));
    }

    private static BufferedImage Mat2BufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    private static void displayImage(Image img2) {
        ImageIcon icon = new ImageIcon(img2);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(img2.getWidth(null) + 50, img2.getHeight(null) + 50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class Tuple<X, Y> {

    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}

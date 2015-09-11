/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.api.objects;

import com.bmc.justdoit.smartkanban.qrcode.QRCodeData;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gokumar
 */
@XmlRootElement
public class TestResponse extends ErrorResponse {
    private String result;
    private List<QRCodeData> qrCodes;

    public List<QRCodeData> getQrCodes() {
        return qrCodes;
    }

    public void setQrCodes(List<QRCodeData> qrCodes) {
        this.qrCodes = qrCodes;
    }

    public TestResponse() {
        super();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String token) {
        this.result = token;
    }
}

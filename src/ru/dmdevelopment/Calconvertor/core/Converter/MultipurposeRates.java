package ru.dmdevelopment.Calconvertor.core.Converter;

import android.app.Activity;
import android.content.res.AssetManager;
import ru.dmdevelopment.Calconvertor.core.helper.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by blitz on 11.05.14.
 */
public class MultipurposeRates implements Rates {
    protected HashMap<String, Multipurpose> ratesMap = new HashMap<String, Multipurpose>();
    private String fileName = "";

    public MultipurposeRates(String fileName) {
        this.fileName = fileName;
        String s = Helper.getStringFromAsset(fileName);

        parseXML(s);
    }

    private void parseXML(String s) {
        for (int i = 0; i < s.split("\\n").length; i++) {
            Multipurpose multipurpose = new Multipurpose(s.split("\\n")[i].split(" ")[0],
                    s.split("\\n")[i].split(" ")[1],
                    Double.parseDouble(s.split("\\n")[i].split(" ")[2]));

            ratesMap.put(multipurpose.getCharCode(), multipurpose);
        }
    }

    public int getRatesCount() {
        return ratesMap.size();
    }

    public double convert(String srcName, double val, String dstName) {
        Multipurpose src = ratesMap.get(srcName);
        Multipurpose dst = ratesMap.get(dstName);

        return (val / src.getValue() * dst.getValue() );
    }

    public String getRatesCharacter(int i) {
        List<String> keys = new ArrayList<String>(ratesMap.keySet());
        return keys.get(i);
    }
}

class Multipurpose {
    private String charCode;
    private String rusName;
    private double value;

    Multipurpose(String charCode, String rusName, double value) {
        this.charCode = charCode;
        this.rusName = rusName;
        this.value = value;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public String getRusName() {
        return rusName;
    }

    public void setRusName(String rusName) {
        this.rusName = rusName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}

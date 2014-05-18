package ru.dmdevelopment.Calconvertor.core.Converter;

import android.app.Activity;

/**
 * Created by blitz on 11.05.14.
 */
public class TemperatureRates extends MultipurposeRates {
    public TemperatureRates(String fileName) {
        super(fileName);
    }

    public double convert(String srcName, double val, String dstName) {
        Multipurpose src = ratesMap.get(srcName);
        Multipurpose dst = ratesMap.get(dstName);

        return (val - src.getValue() + dst.getValue());
    }
}

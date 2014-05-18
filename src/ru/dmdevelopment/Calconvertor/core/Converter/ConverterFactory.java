package ru.dmdevelopment.Calconvertor.core.Converter;

import ru.dmdevelopment.Calconvertor.core.helper.App;

import java.util.HashMap;

/**
 * Created by blitz on 14.05.14.
 */
public class ConverterFactory {
    private final static int converterCount = 14;
    private final static HashMap<Integer, Rates> ratesMap = new HashMap<Integer, Rates>();
    private static final String ratesFileNames[] = new String[converterCount];
    private static final String ratesNames[] = new String[converterCount];

    static {
        ratesFileNames[3] = new String("mass.txt");
        ratesFileNames[5] = new String("length.txt");
        ratesFileNames[7] = new String("volume.txt");
        ratesFileNames[9] = new String("temperature.txt");
        ratesFileNames[11] = new String("sq.txt");
        ratesFileNames[13] = new String("speed.txt");

        ratesNames[1] = new String("Валюта");
        ratesNames[3] = new String("Масса");
        ratesNames[5] = new String("Длина");
        ratesNames[7] = new String("Объем");
        ratesNames[9] = new String("Температура");
        ratesNames[11] = new String("Площадь");
        ratesNames[13] = new String("Скорость");
    }

    public static Rates getConverter(int position) {
        if (!ratesMap.containsKey(position)) {
            createConverter(position);
        }
        if (position == 1) {
            ((CrossValueRates)ratesMap.get(position)).update();
        }
        return ratesMap.get(position);
    }

    private static void createConverter(int position) {
        if (position == 1) ratesMap.put(position, new CrossValueRates(App.getContext()));
        else if (position == 9) ratesMap.put(position, new TemperatureRates(ratesFileNames[position]));
        else ratesMap.put(position, new MultipurposeRates(ratesFileNames[position]));
    }

    public static int getConverterCount() {
        return converterCount;
    }

    public static String getConverterName(int position) {
        return ratesNames[position];
    }

    public static String[] getConverterNames() {
        return ratesNames;
    }
}

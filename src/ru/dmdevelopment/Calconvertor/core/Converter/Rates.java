package ru.dmdevelopment.Calconvertor.core.Converter;

/**
 * Created by blitz on 11.05.14.
 */
public interface Rates {
    public int getRatesCount();
    public double convert(String srcName, double val, String dstName);
    public String getRatesCharacter(int i);
}

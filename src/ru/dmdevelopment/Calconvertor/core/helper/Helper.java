package ru.dmdevelopment.Calconvertor.core.helper;

import android.content.res.AssetManager;

import java.io.*;
import java.util.*;

/**
 * Created by blitz on 14.05.14.
 */
public class Helper {
    public static String getStringFromAsset(String fileName) {
        AssetManager am = App.getContext().getAssets();
        InputStream is;
        String result = null;
        try {
            is = am.open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            result = new String(buffer);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    public static String getStringFromDisk(String fileName) {
        String result = "";
        try {
            InputStream inputStream = App.getContext().openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString + "\n");
                }

                inputStream.close();
                result = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            e.toString();
        } catch (IOException e) {
            e.toString();
        }
        return result;
    }
}

package ru.dmdevelopment.Calconvertor.core.Converter;

import android.content.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by blitz on 19.04.14.
 */
public class CrossValueRates implements Rates {

    private String ratesServerReply = "";
    private HashMap<String, Value> valueMap = new HashMap<String, Value>();
    private String xmlEncoding = "windows-1251";
    Context context;
    String fileName = "";

    public CrossValueRates(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        try {
            if (!existRatesOnDisk()) {
                if (!downloadRates()) {
                    fileName = findOldRatesOnDisk();
                    if (fileName.equals("")) return;
                }
                else {
                    saveRatesOnDisk();
                    fileName = getCurrentRatesFullFileName();
                }
            }
            else {
                fileName = getCurrentRatesFullFileName();
            }
            parseXML(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private String findOldRatesOnDisk() {
        final File folder = new File(context.getFilesDir().getPath().toString());
        final File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir,
                                  final String name) {
                return (name.indexOf("rates_") != -1) ? true : false;
            }
        });
        if (files.length == 0) return "";
        return context.getFilesDir().getPath().toString() + "/" + files[0].getName();
    }

    public int getRatesCount() {
        return valueMap.size();
    }

    public String getRatesCharacter(int i) {
        List<String> keys = new ArrayList<String>(valueMap.keySet());
        return keys.get(i);
    }

    public void update() {
        if (fileName.equals(getCurrentRatesFullFileName())) {
            return;
        }
        if (downloadRates()) {
            try {
                saveRatesOnDisk();
                parseXML(getCurrentRatesFullFileName());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

        }
    }

    public double convert(String srcName, double val, String dstName) {
        Value src = valueMap.get(srcName);
        Value dst = valueMap.get(dstName);

        return (val * src.getValue() / src.getNominal()) / (dst.getValue() / dst.getNominal());
    }

    private boolean existRatesOnDisk() throws IOException {
        String fileName = getCurrentRatesFullFileName();
        File file = new File(fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private void saveRatesOnDisk() throws IOException {
        removeOldRatesFromDisk();

        String fileName = getCurrentRatesFileName();
        FileOutputStream outputStream =  context.openFileOutput(fileName, Context.MODE_PRIVATE);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                outputStream, xmlEncoding));
        writer.write(ratesServerReply);
        writer.close();
    }

    private void removeOldRatesFromDisk() {
        final File folder = new File(context.getFilesDir().getPath().toString());
        final File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir,
                                  final String name) {
                return (name.indexOf("rates_") != -1) ? true : false;
            }
        });
        if (files != null) {
            for (final File file : files) {
                file.delete();
            }
        }
    }

    private String getCurrentRatesFileName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        Date date = new Date();

        return "rates_" + dateFormat.format(date);
    }

    private String getCurrentRatesFullFileName() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        Date date = new Date();

        return context.getFilesDir().getPath().toString() + "/rates_" + dateFormat.format(date);
    }

    private boolean downloadRates() {
        String url = "http://cbr.ru/scripts/XML_daily.asp"; // The Central Bank of the Russian Federation Rates (1 day updates)
        try {
            ratesServerReply = getRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (ratesServerReply.equals("")) return false;
        else return true;
    }

    private String getRequest(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Server not answer");
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), xmlEncoding));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private void parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        File fXmlFile = new File(fileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();

        valueMap = new HashMap<String, Value>();
        NodeList nList = doc.getElementsByTagName("Valute");
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                Value value = new Value();
                value.setId(eElement.getAttribute("ID"));
                value.setCharCode(eElement.getElementsByTagName("CharCode").item(0).getTextContent());
                value.setNominal(Double.parseDouble(eElement.getElementsByTagName("Nominal").item(0).getTextContent()));
                value.setRusName(eElement.getElementsByTagName("Name").item(0).getTextContent());
                value.setValue(Double.parseDouble(eElement.getElementsByTagName("Value").item(0).getTextContent().replace(",", ".")));

                valueMap.put(value.getCharCode(), value);
            }
        }

        Value value = new Value();
        value.setId("R");
        value.setNominal(1);
        value.setCharCode("RUB");
        value.setRusName("Российский рубль");
        value.setValue(1);
        valueMap.put(value.getCharCode(), value);
    }
}

class Value {
    private String id;
    private String charCode;
    private double nominal;
    private String rusName;
    private double value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
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


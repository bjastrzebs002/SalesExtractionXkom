package lib;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class ProductSpecRetriever {
    String productLink;
    Document doc;
    Properties prop;
    InputStream inputStream;
    String propFile = "config.properties";
    String baseAddress = "https://www.x-kom.pl";
    public ProductSpecRetriever(String link) throws IOException {
        prop = new Properties();
        inputStream = getClass().getClassLoader().getResourceAsStream(propFile);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFile + "' not found in the classpath");
        }

        productLink = link;
        doc = Jsoup.connect(baseAddress + productLink).get();
    }

    public ArrayList<String> getProductSpecification() throws Exception {
        ArrayList<String> prodSpec = new ArrayList<>();
        try{
            Element prod = doc.select("div.col-xs-12").first();
            prodSpec.add(prod.attr(prop.getProperty("idAttr")));
            prodSpec.add(prod.attr(prop.getProperty("nameAttr")));
            prodSpec.add(prod.attr(prop.getProperty("brandAttr")));
            prodSpec.add(prod.attr(prop.getProperty("priceAttr")));
        }catch (Exception e){
            throw new Exception("Could not find product specification");
        }
        return prodSpec;
    }
}

package lib;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProductSpecRetriever {
    String productLink;
    Document doc;
    String prodId, prodName, prodBrand;
    float prodPrice;
    Properties prop;
    InputStream inputStream;
    String propFile = "config.properties";

    public ProductSpecRetriever(String link) throws IOException {
        prop = new Properties();
        inputStream = getClass().getClassLoader().getResourceAsStream(propFile);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFile + "' not found in the classpath");
        }

        productLink = link;
        doc = Jsoup.connect(productLink).get();
    }

    public void getProductSpecification() throws Exception {
        try{
            Element prod = doc.select("div.col-xs-12").first();
            prodId = prod.attr(prop.getProperty("idAttr"));
            prodName = prod.attr(prop.getProperty("nameAttr"));
            prodBrand = prod.attr(prop.getProperty("brandAttr"));
            prodPrice = Float.parseFloat(prod.attr(prop.getProperty("priceAttr")));
        }catch (Exception e){
            throw new Exception("Could not find product specification");
        }

    }
}

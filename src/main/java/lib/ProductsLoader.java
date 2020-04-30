package lib;

import Bigquery.BQHandler;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ProductsLoader {
    final static Logger logger = Logger.getLogger(ProductsLoader.class);
    Map<String, String> brands;
    BQHandler bq;
    public ProductsLoader() throws IOException, InterruptedException {
        bq = new BQHandler(BigQueryOptions.getDefaultInstance().getService());

        brands = bq.getBrandsCodes();
    }

    public void loadProducts() throws Exception {
        for (Map.Entry<String,String> entry : brands.entrySet()){
            XkomProducentRetriever retriever = new XkomProducentRetriever(
                    entry.getKey(),
                    entry.getValue());
            retriever.allPagesChecker();

            ArrayList<String> hrefs = retriever.getLinks();
            ArrayList<ArrayList<String>> allSpecs = new ArrayList<>();
            ProductSpecRetriever prodSpec;
            for(String href : hrefs){

                try{
                    prodSpec = new ProductSpecRetriever(href);
                } catch (Exception e){
                    logger.warn("Couldnt get json, error: " + e);
                    continue;
                }

                allSpecs.add(prodSpec.getProductSpecification());
            }
            logger.info("Got all specifications from products' pages for " + entry.getValue());

            bq.updateProducts(allSpecs);

            logger.info("Updated all products and prices for " + entry.getValue());
        }
    }
}

package lib;

import Bigquery.BQHandler;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ProductsLoader {
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
            System.out.println("ALL pages done");
            ArrayList<String> hrefs = retriever.getLinks();
            ArrayList<ArrayList<String>> allSpecs = new ArrayList<>();
            ProductSpecRetriever prodSpec;
            for(String href : hrefs){

                try{
                    prodSpec = new ProductSpecRetriever(href);
                } catch (Exception e){
                    System.out.println("Couldn't get json: " + e);
                    continue;
                }

                allSpecs.add(prodSpec.getProductSpecification());
            }
            System.out.println("Got all specs");
            bq.updateProducts(allSpecs);
            System.out.println("Updated all products and prices");
        }
    }
}

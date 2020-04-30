package Bigquery;

import com.google.api.client.util.DateTime;
import com.google.cloud.bigquery.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.time.LocalDate;
import java.util.*;

public class BQHandler {
    BigQuery bigquery;
    String propFile = "config.properties";
    Properties prop;
    InputStream inputStream;
    TableId tableIdProducts, tableIdPrices, tableIdBrands;

    public BQHandler(BigQuery bq) throws IOException {
        bigquery = bq;
        prop = new Properties();
        inputStream = getClass().getClassLoader().getResourceAsStream(propFile);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFile + "' not found in the classpath");
        }
        tableIdProducts = TableId.of("xkomdb", "products");
        tableIdPrices = TableId.of("xkomdb", "productPrices");
        tableIdBrands = TableId.of("xkomdb", "brands");
    }

    private TableResult getResult(String query) throws InterruptedException {
        QueryJobConfiguration queryConfig =
                QueryJobConfiguration.newBuilder(query)
                        .setUseLegacySql(false)
                        .build();
        JobId jobId = JobId.of(UUID.randomUUID().toString());
        Job queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());
        queryJob = queryJob.waitFor();
        if (queryJob == null) {
            throw new RuntimeException("Job no longer exists");
        } else if (queryJob.getStatus().getError() != null) {
            throw new RuntimeException(queryJob.getStatus().getError().toString());
        }

        return queryJob.getQueryResults();
    }

    public Map<String, String> getBrandsCodes() throws InterruptedException {
        String query = String.format("Select brand, code from %s", prop.getProperty("brandsTable"));

        TableResult result = getResult(query);
        Map<String, String> brands = new HashMap<>();

        for (FieldValueList row : result.iterateAll()){
            brands.put(row.get("code").getStringValue(), row.get("brand").getStringValue());
        }

        return brands;
    }

    public ArrayList<String> getAllBrands() throws InterruptedException {
        String query = String.format("Select distinct(productBrand) as b_dist from %s", prop.getProperty("productsTable"));

        TableResult result = getResult(query);
        ArrayList<String> brands = new ArrayList<>();

        for (FieldValueList row : result.iterateAll()){
            brands.add(row.get("b_dist").getStringValue());
        }

        return brands;
    }

    public Map<Integer, String> getBrandProducts(String brand) throws InterruptedException {
        String query = String.format("Select productId, productName from %s where productBrand='%s'",
                prop.getProperty("productsTable"), brand);
        TableResult result = getResult(query);
        Map<Integer, String> products = new TreeMap<>();

        for (FieldValueList row : result.iterateAll()){
            products.put(row.get("productId").getNumericValue().intValue(), row.get("productName").getStringValue());
        }

        return products;
    }

    public Map<Date, Float> getPricesOfProduct(Integer id) throws InterruptedException {
        String query = String.format("Select date, price from %s where productId=%d",
                prop.getProperty("productsTable"), id);

        TableResult result = getResult(query);
        Map<Date, Float> products = new TreeMap<>();

        for (FieldValueList row : result.iterateAll()){
            Date rowDate = new Date(row.get("date").getTimestampValue());
            products.put(rowDate, (float) row.get("price").getDoubleValue());
        }

        return products;
    }

    private ArrayList<Integer> getAllProducts() throws InterruptedException {
        String query = String.format("Select productId from %s ",
                prop.getProperty("productsTable"));

        TableResult result = getResult(query);
        ArrayList<Integer> products = new ArrayList<>();

        for (FieldValueList row : result.iterateAll()){
            products.add(row.get("productId").getNumericValue().intValue());
        }

        return products;
    }

    public void updateProducts(ArrayList<ArrayList<String>> products) throws InterruptedException {
        ArrayList<Integer> actualProducts = getAllProducts();
        for(ArrayList<String> row : products){
            int id = Integer.parseInt(row.get(0));
            if(actualProducts.contains(id)){
                continue;
            }
            Map<String, Object> rowContent = new HashMap<>();
            rowContent.put("productId", id);
            rowContent.put("productName", row.get(1));
            rowContent.put("productBrand", row.get(2));
            InsertAllResponse responseProducts =
                    bigquery.insertAll(
                            InsertAllRequest.newBuilder(tableIdProducts)
                                    .addRow(rowContent)
                                    // More rows can be added in the same RPC by invoking .addRow() on the builder.
                                    // You can also supply optional unique row keys to support de-duplication scenarios.
                                    .build());
            if (responseProducts.hasErrors()) {
                System.out.println("Couldn't insert product.");
            }

            Map<String, Object> rowContentPrices = new HashMap<>();
            rowContentPrices.put("productId", id);
            rowContentPrices.put("date", LocalDate.now().toString());
            rowContentPrices.put("price", Float.parseFloat(row.get(3)));
            InsertAllResponse responsePrices =
                    bigquery.insertAll(
                            InsertAllRequest.newBuilder(tableIdPrices)
                                    .addRow(rowContentPrices)
                                    // More rows can be added in the same RPC by invoking .addRow() on the builder.
                                    // You can also supply optional unique row keys to support de-duplication scenarios.
                                    .build());
            if (responsePrices.hasErrors()) {
                System.out.println("Couldn't insert product's price and date.");
            }
        }

    }
}

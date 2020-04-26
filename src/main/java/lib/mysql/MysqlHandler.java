package lib.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class MysqlHandler {
    Connection con;
    Statement stmt;

    public MysqlHandler(Connection con) throws SQLException {
        this.con = con;
        stmt = this.con.createStatement();
    }

    //TODO - required queries to db
    public ArrayList<String> getAllBrands() throws SQLException {
        ArrayList<String> brands = new ArrayList<>();
        ResultSet rs = stmt.executeQuery(
                "select distinct(productBrand) from Products"
        );
        while(rs.next()){
            brands.add(rs.getString("productBrand"));
        }
        return brands;
    }

    public ArrayList<String> getAllProducts(String brand) throws SQLException {
        ArrayList<String> products = new ArrayList<>();
        ResultSet rs = stmt.executeQuery(String.format(
                "select productName " +
                        "from Products " +
                        "where productBrand='%s'", brand)
        );
        while(rs.next()){
            products.add(rs.getString("productName"));
        }
        return products;
    }

    public Map<Date, Float> getProductPrices(int productId) throws SQLException {
        Map<Date, Float> prices = new TreeMap<>();
        ResultSet rs = stmt.executeQuery(String.format(
                "select date, price " +
                        "from ProductPrices " +
                        "where productId='%s'", productId)
        );
        while(rs.next()){
            prices.put(rs.getDate("date"),
                    rs.getFloat("price"));
        }
        return prices;
    }


}

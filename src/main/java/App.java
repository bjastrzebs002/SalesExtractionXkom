import javafx.application.Application;
import javafx.stage.Stage;
import lib.ProductSpecRetriever;
import lib.XkomProducentRetriever;
import lib.mysql.MysqlCon;
import lib.mysql.MysqlHandler;

import java.io.IOException;
import java.sql.Connection;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // apple 357 code
//        XkomProducentRetriever retriever = new XkomProducentRetriever(
//                "357",
//                "apple");
////        System.out.println("ALL pages done");
//        ProductSpecRetriever prod = new ProductSpecRetriever("https://www.x-kom.pl/p/506954-notebook-laptop-133-apple-macbook-pro-i7-17ghz-16gb-256-iris645-space-gray.html");
//        MysqlCon msq = new MysqlCon();
//        MysqlHandler msqHandler = new MysqlHandler(msq.getConnection());

    }
}

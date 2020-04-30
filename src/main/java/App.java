import javafx.application.Application;
import javafx.stage.Stage;
import lib.ProductsLoader;


public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ProductsLoader pl = new ProductsLoader();
        pl.loadProducts();
    }
}

import lib.ProductsLoader;



public class App {

    public App() throws Exception {
        ProductsLoader pl = new ProductsLoader();
        pl.loadProducts();
    }

    public static void main(String[] args) throws Exception {
        new App();
    }
}

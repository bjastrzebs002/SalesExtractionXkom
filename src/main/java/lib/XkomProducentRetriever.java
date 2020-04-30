package lib;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class XkomProducentRetriever {
    String producentProductsLink = "https://www.x-kom.pl/szukaj?sort_by=accuracy_desc&f[manufacturers][%s]=1&q=%s";
    ArrayList<String> aHrefs = new ArrayList<>();

    public XkomProducentRetriever(String code,
                                  String name){
        producentProductsLink = String.format(producentProductsLink, code, name);
    }

    public void allPagesChecker(){
        int page = 1;
        while(true) {
            try{
                fillAllHrefs(page);
            } catch (Exception e){
                break;
            }
            page += 1;
        }
    }

    private void fillAllHrefs(int pageNumber) throws Exception {
        Document doc;
        doc = Jsoup.connect(producentProductsLink + "&page=" + pageNumber).get();
        Elements aElements = doc.select("a");
        ArrayList<String> pageHrefs = new ArrayList<>();
        try{
            for(Element e : aElements){
                String href = e.attr("href");
                if (href.startsWith("/p/") && href.endsWith(".html")
                        && !aHrefs.contains(href) && !pageHrefs.contains(href)){
                    pageHrefs.add(href);
                }
            }
        } catch(Exception e){
            System.out.println("End od page");
        }
        if(pageHrefs.size() == 0) throw new Exception("It was last page");
        aHrefs.addAll(pageHrefs);

    }

    public ArrayList<String> getLinks(){ return aHrefs; }
}

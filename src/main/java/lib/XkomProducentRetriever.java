package lib;

import Bigquery.BQHandler;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class XkomProducentRetriever {
    final static Logger logger = Logger.getLogger(XkomProducentRetriever.class);
    String producentProductsLink = "https://www.x-kom.pl/szukaj?sort_by=accuracy_desc&f[manufacturers][%s]=1&q=%s";
    ArrayList<String> aHrefs = new ArrayList<>();
    String producent;
    public XkomProducentRetriever(String code,
                                  String name){
        producentProductsLink = String.format(producentProductsLink, code, name);
        producent = name;
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
        logger.info("All pages done for producent: " + producent);
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

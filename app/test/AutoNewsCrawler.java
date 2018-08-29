package test;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

import java.util.List;

/**
 * Crawling news from hfut news
 *
 * @author hu
 */
public class AutoNewsCrawler extends BreadthCrawler {

    public AutoNewsCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

    List<String> titles;

    @Override
    public void visit(Page page, CrawlDatums next) {
        System.out.println(page.url());
        String name = page.selectText("h1",0);
        System.out.println(name);
        System.out.println(page.select("h1"));
        titles.add(name);
    }

    public static void main(String[] args) {
        AutoNewsCrawler code = new AutoNewsCrawler("crawle", false);
        code.setThreads(2);

        for(int i=1;i<100;i++){
            System.out.println(i);
            code.addSeed("https://www.gbif.org/species/" + i);
        }

        try {
                code.start(1);

            } catch (Exception e) {
                e.printStackTrace();
            }

    }
}
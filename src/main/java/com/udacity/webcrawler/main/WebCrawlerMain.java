package com.udacity.webcrawler.main;

import com.google.inject.Guice;
import com.udacity.webcrawler.WebCrawler;
import com.udacity.webcrawler.WebCrawlerModule;
import com.udacity.webcrawler.json.ConfigurationLoader;
import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.json.CrawlResultWriter;
import com.udacity.webcrawler.json.CrawlerConfiguration;
import com.udacity.webcrawler.profiler.Profiler;
import com.udacity.webcrawler.profiler.ProfilerModule;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Objects;
import javax.inject.Inject;

public final class WebCrawlerMain {

    private final CrawlerConfiguration config;

    private WebCrawlerMain(CrawlerConfiguration config) {
        this.config = Objects.requireNonNull(config);
    }

    @Inject
    private WebCrawler crawler;

    @Inject
    private Profiler profiler;

    private void run() throws Exception {
        Guice.createInjector(new WebCrawlerModule(config), new ProfilerModule()).injectMembers(this);

        CrawlResult result = crawler.crawl(config.getStartPages());
        CrawlResultWriter resultWriter = new CrawlResultWriter(result);
//        **Old Code not using  try-with-resources idiom.**
//        if (config.getResultPath() != null && !config.getResultPath().isBlank()) {
//            resultWriter.write(Path.of(config.getResultPath()));
//        } else {
//            resultWriter.write(new PrintWriter(System.out, true));
//        }
//        if (config.getProfileOutputPath() != null && !config.getProfileOutputPath().isBlank()) {
//            profiler.writeData(Path.of(config.getProfileOutputPath()));
//        } else {
//            profiler.writeData(new PrintWriter(System.out, true));
//        }
        try (Writer w = new OutputStreamWriter(System.out)){
            if (config.getResultPath() != null && !config.getResultPath().isBlank()) {
                resultWriter.write(Path.of(config.getResultPath()));
            } else{
                resultWriter.write(w);
            }
            if (config.getProfileOutputPath() != null && !config.getProfileOutputPath().isBlank()) {
                profiler.writeData(Path.of(config.getProfileOutputPath()));
            } else{
                profiler.writeData(w);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
            //I have implemented the try-with-resources idiom and exception handling using the recommendation and tips
            // in the first review. Including following the example tip code provided in the code review section.
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: WebCrawlerMain [starting-url]");
            return;
        }

        CrawlerConfiguration config = new ConfigurationLoader(Path.of(args[0])).load();
        new WebCrawlerMain(config).run();
        System.exit(0);
    }
}

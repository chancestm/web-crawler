package com.udacity.webcrawler;

import com.udacity.webcrawler.json.CrawlResult;
import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import javax.inject.Inject;

/**
 * A concrete implementation of {@link WebCrawler} that runs multiple threads on a {@link ForkJoinPool} to fetch and
 * process multiple web pages in parallel.
 */
final class ParallelWebCrawler implements WebCrawler {
    private final Clock clock;
    private final PageParserFactory parserFactory;
    private final Duration timeout;
    private final int popularWordCount;
    private final int maxDepth;
    private final List<Pattern> ignoredUrls;
    private final ExecutorService pool;

    @Inject
    ParallelWebCrawler(
            Clock clock,
            PageParserFactory parserFactory,
            @Timeout Duration timeout,
            @PopularWordCount int popularWordCount,
            @MaxDepth int maxDepth,
            @IgnoredUrls List<Pattern> ignoredUrls,
            @TargetParallelism int threadCount) {
        this.clock = clock;
        this.parserFactory = parserFactory;
        this.timeout = timeout;
        this.popularWordCount = popularWordCount;
        this.maxDepth = maxDepth;
        this.ignoredUrls = ignoredUrls;
        pool = Executors.newScheduledThreadPool(Math.min(threadCount, getMaxParallelism()));
    }

    @Override
    public CrawlResult crawl(List<String> startingUrls) {
        Instant deadline = clock.instant().plus(timeout);
        ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
        Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
        ConcurrentLinkedDeque<CompletableFuture> tasks = new ConcurrentLinkedDeque<>();
        for (String url : startingUrls) {
            tasks.add(CompletableFuture.runAsync(() -> crawlInternal(url, deadline, maxDepth, counts, visitedUrls, tasks), pool));
        }
        while (!tasks.isEmpty()) {
            CompletableFuture task = tasks.pollFirst();
            try {
                task.get(deadline.toEpochMilli() - Instant.now().toEpochMilli(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                task.cancel(true);
            }
        }

        if (counts.isEmpty()) {
            return new CrawlResult.Builder()
                    .setWordCounts(counts)
                    .setUrlsVisited(visitedUrls.size())
                    .build();
        }

        return new CrawlResult.Builder()
                .setWordCounts(WordCounts.sort(counts, popularWordCount))
                .setUrlsVisited(visitedUrls.size())
                .build();
    }

    private void crawlInternal(
            String url,
            Instant deadline,
            int maxDepth,
            ConcurrentHashMap<String, Integer> counts,
            Set<String> visitedUrls,
            ConcurrentLinkedDeque<CompletableFuture> tasks) {
        if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
            return;
        }
        for (Pattern pattern : ignoredUrls) {
            if (pattern.matcher(url).matches()) {
                return;
            }
        }
        if (!visitedUrls.add(url)) {
            return;
        }
        PageParser.Result result = parserFactory.get(url).parse();
        result.getWordCounts().entrySet().forEach(e -> counts.merge(e.getKey(), e.getValue(), (current, newValue) -> current + newValue));
        for (String link : result.getLinks()) {
            tasks.add(CompletableFuture.runAsync(() -> crawlInternal(link, deadline, maxDepth - 1, counts, visitedUrls, tasks), pool));
        }
    }

    @Override
    public int getMaxParallelism() {
        return Runtime.getRuntime().availableProcessors();
    }
}

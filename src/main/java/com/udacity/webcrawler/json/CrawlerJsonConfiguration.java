package com.udacity.webcrawler.json;

import java.util.List;

public class CrawlerJsonConfiguration {
    private List<String> startPages;
    private List<String> ignoredUrls;
    private List<String> ignoredWords;
    private Integer parallelism;
    private String implementationOverride;
    private Integer maxDepth;
    private Integer timeoutSeconds;
    private Integer popularWordCount;
    private String profileOutputPath;
    private String resultPath;

    public List<String> getStartPages() {
        return startPages;
    }

    public void setStartPages(List<String> startPages) {
        this.startPages = startPages;
    }

    public List<String> getIgnoredUrls() {
        return ignoredUrls;
    }

    public void setIgnoredUrls(List<String> ignoredUrls) {
        this.ignoredUrls = ignoredUrls;
    }

    public List<String> getIgnoredWords() {
        return ignoredWords;
    }

    public void setIgnoredWords(List<String> ignoredWords) {
        this.ignoredWords = ignoredWords;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public String getImplementationOverride() {
        return implementationOverride;
    }

    public void setImplementationOverride(String implementationOverride) {
        this.implementationOverride = implementationOverride;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public Integer getPopularWordCount() {
        return popularWordCount;
    }

    public void setPopularWordCount(Integer popularWordCount) {
        this.popularWordCount = popularWordCount;
    }

    public String getProfileOutputPath() {
        return profileOutputPath;
    }

    public void setProfileOutputPath(String profileOutputPath) {
        this.profileOutputPath = profileOutputPath;
    }

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

}

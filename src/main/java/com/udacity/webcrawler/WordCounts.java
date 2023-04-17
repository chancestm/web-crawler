package com.udacity.webcrawler;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class that sorts the map of word counts.
 *
 * <p>
 */
final class WordCounts {

    /**
     * Given an unsorted map of word counts, returns a new map whose word counts are sorted according to the provided
     * {@link WordCountComparator}, and includes only the top {@param popluarWordCount} words and counts.
     *
     * <p>
     *
     * @param wordCounts the unsorted map of word counts.
     * @param popularWordCount the number of popular words to include in the result map.
     * @return a map containing the top {@param popularWordCount} words and counts in the right order.
     */
    static Map<String, Integer> sort(Map<String, Integer> wordCounts, int popularWordCount) {

        return wordCounts.entrySet().stream().sorted(
                Comparator.<Map.Entry<String, Integer>, Integer>comparing(Map.Entry::getValue).thenComparing(e -> e.getKey().length()).reversed().thenComparing(Map.Entry::getKey))
                .limit(Math.min(popularWordCount, wordCounts.size()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));
    }

    private WordCounts() {
        // This class cannot be instantiated
    }
}

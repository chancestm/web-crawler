package com.udacity.webcrawler.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A static utility class that loads a JSON configuration file.
 */
public final class ConfigurationLoader {

    private final Path path;

    /**
     * Create a {@link ConfigurationLoader} that loads configuration from the given {@link Path}.
     */
    public ConfigurationLoader(Path path) {
        this.path = Objects.requireNonNull(path);
    }

    /**
     * Loads configuration from this {@link ConfigurationLoader}'s path
     *
     * @return the loaded {@link CrawlerConfiguration}.
     */
    public CrawlerConfiguration load() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CrawlerJsonConfiguration config;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            config = mapper.readValue(reader, CrawlerJsonConfiguration.class);
        }
        return build(config);
    }

    /**
     * Loads crawler configuration from the given reader.
     *
     * @param reader a Reader pointing to a JSON string that contains crawler configuration.
     * @return a crawler configuration
     */
    public static CrawlerConfiguration read(Reader reader) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CrawlerJsonConfiguration config = mapper.readValue(new BufferedReader(reader).lines().collect(Collectors.joining("\n")), CrawlerJsonConfiguration.class);
        return build(config);
    }

    private static CrawlerConfiguration build(CrawlerJsonConfiguration config) {
        CrawlerConfiguration.Builder builder = new CrawlerConfiguration.Builder();
        Optional.ofNullable(config.getStartPages()).ifPresent(v -> builder.addStartPages(v.toArray(new String[0])));
        Optional.ofNullable(config.getIgnoredUrls()).ifPresent(v -> builder.addIgnoredUrls(v.toArray(new String[0])));
        Optional.ofNullable(config.getIgnoredWords()).ifPresent(v -> builder.addIgnoredWords(v.toArray(new String[0])));
        Optional.ofNullable(config.getParallelism()).ifPresent(v -> builder.setParallelism(v));
        Optional.ofNullable(config.getImplementationOverride()).ifPresent(v -> builder.setImplementationOverride(v));
        Optional.ofNullable(config.getMaxDepth()).ifPresent(v -> builder.setMaxDepth(v));
        Optional.ofNullable(config.getPopularWordCount()).ifPresent(v -> builder.setPopularWordCount(v));
        Optional.ofNullable(config.getProfileOutputPath()).ifPresent(v -> builder.setProfileOutputPath(v));
        Optional.ofNullable(config.getResultPath()).ifPresent(v -> builder.setResultPath(v));
        Optional.ofNullable(config.getTimeoutSeconds()).ifPresent(v -> builder.setTimeoutSeconds(v));
        return builder.build();
    }
}

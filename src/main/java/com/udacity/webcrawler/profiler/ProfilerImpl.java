package com.udacity.webcrawler.profiler;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;
import javax.inject.Inject;

/**
 * Concrete implementation of the {@link Profiler}.
 */
final class ProfilerImpl implements Profiler {

    private final Clock clock;
    private final ProfilingState state = new ProfilingState();
    private final ZonedDateTime startTime;

    @Inject
    ProfilerImpl(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
        startTime = ZonedDateTime.now(clock);
    }

    @Override
    public <T> T wrap(Class<T> klass, T delegate) {
        Objects.requireNonNull(klass);
        if (Arrays.asList(klass.getMethods()).stream().flatMap(m -> Arrays.asList(m.getAnnotations()).stream()).allMatch(a -> !a.annotationType().equals(Profiled.class))) {
            throw new IllegalArgumentException("Class " + klass.getName() + " doesn't have method with annotation @Profiled");
        }

        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {klass}, new ProfilingMethodInterceptor(clock, delegate, state));
    }

    @Override
    public void writeData(Path path) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
            writer.write(System.lineSeparator());
            state.write(writer);
            writer.write(System.lineSeparator());
            writer.flush();
        }
    }

    @Override
    public void writeData(Writer writer) throws IOException {
        writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
        writer.write(System.lineSeparator());
        state.write(writer);
        writer.write(System.lineSeparator());
        writer.flush();
    }
}

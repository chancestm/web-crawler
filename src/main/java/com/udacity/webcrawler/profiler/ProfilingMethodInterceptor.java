package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled} annotation. If they
 * are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

    private final Clock clock;
    private final Object target;
    private final ProfilingState state;

    ProfilingMethodInterceptor(Clock clock, Object target, ProfilingState state) {
        this.clock = Objects.requireNonNull(clock);
        this.target = Objects.requireNonNull(target);
        this.state = Objects.requireNonNull(state);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean recodrd = Arrays.asList(method.getAnnotations()).stream().anyMatch(a -> a.annotationType().equals(Profiled.class));
        long start = clock.millis();
        Object result = null;
        try {
            result = method.invoke(target, args);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();//changed from .getCause() as per reviewer.
        } catch (Throwable e) {
            throw e;
        } finally {
            if (recodrd) {
                state.record(target.getClass(), method, Duration.ofMillis(clock.millis() - start));
            }
        }
        return result;
    }
}

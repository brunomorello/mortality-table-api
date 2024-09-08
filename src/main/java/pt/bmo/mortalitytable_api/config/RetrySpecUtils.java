package pt.bmo.mortalitytable_api.config;

import pt.bmo.mortalitytable_api.externalservice.exception.ExternalSystemException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.time.Duration;

public class RetrySpecUtils {
    private RetrySpecUtils() { }

    public static Retry retrySpec() {
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof ExternalSystemException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
    }
}

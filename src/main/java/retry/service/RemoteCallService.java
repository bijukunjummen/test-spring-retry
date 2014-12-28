package retry.service;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

public interface RemoteCallService {
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    String call() throws Exception;
}

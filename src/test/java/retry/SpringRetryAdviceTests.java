package retry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import retry.service.RemoteCallService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SpringRetryAdviceTests {

    @Autowired
    private RemoteCallService remoteCallService;

    @Test
    public void testRetry() throws Exception {
        String message = this.remoteCallService.call();
        verify(remoteCallService, times(3)).call();
        assertThat(message, is("Completed"));
    }

    @Configuration
    @EnableAspectJAutoProxy
    @ImportResource("classpath:/retryadvice.xml")
    public static class SpringConfig {

        @Bean
        public RemoteCallService remoteCallService() throws Exception {
            RemoteCallService remoteService = mock(RemoteCallService.class);
            when(remoteService.call())
                    .thenThrow(new RuntimeException("Remote Exception 1"))
                    .thenThrow(new RuntimeException("Remote Exception 2"))
                    .thenReturn("Completed");
            return remoteService;
        }

        @Bean
        public RetryTemplate retryTemplate() {
            RetryTemplate retryTemplate = new RetryTemplate();
            FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
            fixedBackOffPolicy.setBackOffPeriod(2000l);
            retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

            SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
            retryPolicy.setMaxAttempts(3);

            retryTemplate.setRetryPolicy(retryPolicy);
            return retryTemplate;
        }

        @Bean
        public RetryOperationsInterceptor retryAdvice() {
            RetryOperationsInterceptor retryAdvice = new RetryOperationsInterceptor();
            retryAdvice.setRetryOperations(retryTemplate());
            return retryAdvice;
        }
    }
}

package retry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import retry.service.RemoteCallService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SpringRetryDeclarativeTests {

    @Autowired
    private RemoteCallService remoteCallService;

    @Test
    public void testRetry() throws Exception {
        String message = this.remoteCallService.call();
        verify(remoteCallService, times(3)).call();
        assertThat(message, is("Completed"));
    }

    @Configuration
    @EnableRetry
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
    }
}

package retry.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;

@Aspect
public class RetryAspect {

    private static Logger logger = LoggerFactory.getLogger(RetryAspect.class);

    @Autowired
    private RetryTemplate retryTemplate;

    @Pointcut("execution(* retry.service..*(..))")
    public void serviceMethods() {
        //
    }

    @Around("serviceMethods()")
    public Object aroundServiceMethods(ProceedingJoinPoint joinPoint) {
        try {
            return retryTemplate.execute(retryContext -> joinPoint.proceed());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
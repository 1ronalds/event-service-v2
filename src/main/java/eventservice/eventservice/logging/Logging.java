package eventservice.eventservice.logging;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Log4j2
public class Logging {

    @Before("execution(* eventservice.eventservice.web.controller.*.*(..))")
    public void logBeforeController(JoinPoint joinPoint){
        log.info("Calling {}.{}() with arguments: {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @Before("execution(* eventservice.eventservice.business.service.impl.*.*(..))")
    public void logBeforeService(JoinPoint joinPoint){
        log.info("Calling {}.{}() with arguments: {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

}

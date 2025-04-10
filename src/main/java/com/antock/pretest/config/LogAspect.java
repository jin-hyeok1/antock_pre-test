package com.antock.pretest.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("execution(* com.antock.pretest.business.controller..*(..))")
    private void method() {
    }

    @Around("method()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        Throwable throwable = null;
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throwable = e;
            throw e;
        } finally {
            long duration = (System.nanoTime() - start) / 1_000_000;
            String args = Arrays.stream(joinPoint.getArgs()).map(o -> {
                String type = o == null ? "null" : o.getClass().getSimpleName();
                return String.format("%s %s", type, o);
            }).collect(Collectors.joining(", "));
            if (throwable == null) {
                log.info("[{}][{}] {} method={} params=[{}] duration={}ms",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        Thread.currentThread().getName(),
                        "SUCCESS",
                        joinPoint.getSignature().getName(),
                        args,
                        duration
                );
            } else {
                log.error("[{}][{}] {} method={} params=[{}] duration={}ms exception={} message={}",
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        Thread.currentThread().getName(),
                        "FAIL",
                        joinPoint.getSignature().getName(),
                        args,
                        duration,
                        throwable.getClass().getName(),
                        throwable.getMessage()
                );
            }

        }
    }
}


package com.helpdesk.aspect;

import com.helpdesk.repository.jpa.UserRepository;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // It allows you to write messages to the application logs
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    private final UserRepository userRepository;

    public LoggingAspect(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Around("execution(* com.helpdesk.service..*.create*(..)) || " +
            "execution(* com.helpdesk.service..*.update*(..)) || " +
            "execution(* com.helpdesk.service..*.delete*(..)) || " +
            "execution(* com.helpdesk.service..*.claim*(..)) || " +
            "execution(* com.helpdesk.service..*.assign*(..)) || " +
            "execution(* com.helpdesk.service..*.add*(..)) || " +
            "execution(* com.helpdesk.service..*.register*(..))")
    public Object logServiceOperation(ProceedingJoinPoint joinPoint)
            throws Throwable {

        long startTime = System.nanoTime();

        String operation = joinPoint.getSignature().getName();
        String actorId = getActorId();

        try {
            Object result = joinPoint.proceed();

            long duration = (System.nanoTime() - startTime) / 1_000_000;

            logger.info(
                    "Operation: {}, Actor ID: {}, Duration: {} ms, Status: SUCCESS",
                    operation, actorId, duration);

            return result;

        } catch (Throwable exception) {

            long duration = (System.nanoTime() - startTime) / 1_000_000;

            logger.error(
                    "Operation: {}, Actor ID: {}, Duration: {} ms, Status: FAILURE, Exception: {}",
                    operation,
                    actorId,
                    duration,
                    exception.getClass().getSimpleName());

            throw exception;
        }
    }

    private String getActorId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return "anonymous";
        }

        try {
            Long userId = Long.parseLong(authentication.getName());

            return userRepository.findById(userId)
                    .map(user -> String.valueOf(user.getId()))
                    .orElse("unknown");

        } catch (Exception exception) {
            return "unavailable";
        }
    }
}

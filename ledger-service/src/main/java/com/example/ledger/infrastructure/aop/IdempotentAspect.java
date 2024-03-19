package com.example.ledger.infrastructure.aop;


import com.example.ledger.domain.shared.annotation.Idempotent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.lang.reflect.Method;

@Aspect
@RequiredArgsConstructor
//@Component
public class IdempotentAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(com.example.ledger.domain.shared.annotation.Idempotent)")
    public Object idempotent(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Idempotent idempotent = method.getAnnotation(Idempotent.class);

        // todo :
        //  return data if  key status = finished.
        //  throw duplicate request exception if key status = start
        // use SpringEL find key
        String key = method.getName() + idempotent.key();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            // for retry
            redisTemplate.delete(key);
            throw ex;
        }

        return result;
    }
}
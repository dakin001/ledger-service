package com.example.ledger.infrastructure.aop;


import com.example.ledger.domain.shared.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RedissonClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@RequiredArgsConstructor
@Component
@Log4j2
public class DistributedLockAspect {

    private final RedissonClient redissonClient;
    private static final String LOCK_KEY_FORMAT = "DistributedLock-{0}";
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();


    @Around("@annotation(com.example.ledger.domain.shared.annotation.DistributedLock)")
    public Object aroundMethod(ProceedingJoinPoint point) throws Throwable {
        Method method = getMethod(point);
        DistributedLock lockConfig = method.getAnnotation(DistributedLock.class);

        String identifier = generateIdentifier(method, point.getArgs(), lockConfig);
        String lockKey = MessageFormat.format(LOCK_KEY_FORMAT, identifier);

        var lock = redissonClient.getLock(lockKey);

        boolean isLocked = lock.tryLock(lockConfig.waitSecond(), lockConfig.expireSecond(), TimeUnit.SECONDS);
        if (!isLocked) {
            System.out.println("Could not acquire the lock");
        }
        try {
            return point.proceed();
        } finally {
            lock.unlock();
        }
    }

    private Method getMethod(ProceedingJoinPoint point) {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return methodSignature.getMethod();
    }

    private String generateIdentifier(Method method, Object[] args, DistributedLock idempotent) {
        var identifier = StringUtils.isEmpty(idempotent.key()) ?
                generateIdentifier(method, args) :
                generateIdentifier(method, args, idempotent.key());

        return method.getDeclaringClass().getSimpleName() +
                "." + method.getName() +
                ":" + identifier;
    }

    /**
     * use SpringEL find key
     */
    private String generateIdentifier(Method method, Object[] args, String keyEl) {
        EvaluationContext context = new StandardEvaluationContext();
        String[] parameterNames = discoverer.getParameterNames(method);
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        Expression expression = parser.parseExpression(keyEl);
        return Optional.ofNullable(expression.getValue(context)).orElse("null").toString();
    }

    private String generateIdentifier(Method method, Object[] args) {
        return Stream.of(args).filter(Objects::nonNull).map(Object::toString)
                .collect(Collectors.joining());
    }
}


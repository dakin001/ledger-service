package com.example.ledger.infrastructure.aop;


import com.example.ledger.domain.shared.annotation.Idempotent;
import com.example.ledger.domain.shared.util.JsonSerializationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.retry.RetryException;
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
public class IdempotentAspect {

    private final StringRedisTemplate redisTemplate;
    private static final String STATUS_KEY_FORMAT = "Idempotent-{0}:status";
    private static final String DATA_KEY_FORMAT = "Idempotent-{0}:data";

    private static final String STATUS_BEGIN = "begin";
    private static final String STATUS_END = "end";
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(com.example.ledger.domain.shared.annotation.Idempotent)")
    public Object idempotent(ProceedingJoinPoint point) throws Throwable {
        Method method = getMethod(point);
        Idempotent idempotent = method.getAnnotation(Idempotent.class);

        // use SpringEL find key
        String identifier = generateIdentifier(method, point.getArgs(), idempotent);
        String statusKey = MessageFormat.format(STATUS_KEY_FORMAT, identifier);
        String dataKey = MessageFormat.format(DATA_KEY_FORMAT, identifier);

        var valueOperations = redisTemplate.opsForValue();

        Boolean isNew = valueOperations.setIfAbsent(statusKey, STATUS_BEGIN, idempotent.expireSecond(), TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(isNew)) {
            return getResult(valueOperations, dataKey, statusKey, method);
        }
        return proceed(point, valueOperations, dataKey, idempotent, statusKey);
    }

    private static Object getResult(ValueOperations<String, String> valueOperations, String dataKey, String statusKey, Method method) {
        //  get data first avoid timeout
        String dataObject = valueOperations.get(dataKey);
        String status = valueOperations.get(statusKey);
        if (STATUS_BEGIN.equals(status)) {
            throw new RetryException("in processing, please request later");
        }
        if (dataObject == null) {
            throw new RuntimeException("data not found");
        }

        return JsonSerializationUtils.deserialize(dataObject, method.getReturnType());
    }

    private Object proceed(ProceedingJoinPoint point, ValueOperations<String, String> valueOperations, String dataKey, Idempotent idempotent, String statusKey) throws Throwable {
        try {
            Object result = point.proceed();
            // + 10s, avoid data timeout before status
            valueOperations.set(dataKey, JsonSerializationUtils.serialize(result), idempotent.expireSecond() + 10, TimeUnit.SECONDS);
            valueOperations.set(statusKey, STATUS_END, idempotent.expireSecond(), TimeUnit.SECONDS);
            return result;
        } catch (Throwable ex) {
            // for retry
            redisTemplate.delete(statusKey);
            throw ex;
        }
    }

    private Method getMethod(ProceedingJoinPoint point) {
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        return methodSignature.getMethod();
    }


    private String generateIdentifier(Method method, Object[] args, Idempotent idempotent) {
        var identifier = StringUtils.isEmpty(idempotent.key()) ?
                generateIdentifier(method, args) :
                generateIdentifier(method, args, idempotent.key());

        return method.getDeclaringClass().getSimpleName() +
                "." + method.getName() +
                ":" + identifier;
    }

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


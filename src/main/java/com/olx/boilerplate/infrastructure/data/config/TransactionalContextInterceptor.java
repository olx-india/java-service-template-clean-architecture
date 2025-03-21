package com.olx.boilerplate.infrastructure.data.config;

import com.olx.boilerplate.annotation.ReadOnlyTransaction;
import com.olx.boilerplate.annotation.ReadWriteTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static com.olx.boilerplate.infrastructure.constant.CommonConstants.READ;

@Aspect
@Component
public class TransactionalContextInterceptor {

    @Around("@annotation(readOnlyTransaction)")
    public Object traceRead(ProceedingJoinPoint joinPoint, ReadOnlyTransaction readOnlyTransaction) throws Throwable {
        return process(joinPoint, READ);
    }

    @Around("@annotation(readWriteTransaction)")
    public Object traceReadWrite(ProceedingJoinPoint joinPoint, ReadWriteTransaction readWriteTransaction)
            throws Throwable {
        return process(joinPoint, "");
    }

    private Object process(ProceedingJoinPoint joinPoint, String prefix) throws Throwable {
        try {
            DbContextHolder.set(prefix);
            return joinPoint.proceed();
        } finally {
            DbContextHolder.remove();
        }
    }

}

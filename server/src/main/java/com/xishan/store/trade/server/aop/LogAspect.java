package com.xishan.store.trade.server.aop;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
//申明是个spring管理的bean
@Component
@Slf4j
@Order(1)
public class LogAspect {
    //申明一个切点 里面是 execution表达式

    @Pointcut("execution(public * com.xishan.store.trade.server.facade.*.*(..))")
    private void controllerAspect() {
    }

    @Around("controllerAspect()")
    public Object serviceLogAround(ProceedingJoinPoint point) throws Throwable {
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
          StopWatch stopWatch = new StopWatch();
          stopWatch.start();
          Object result = null;
        try {
            result = point.proceed();
            stopWatch.stop();
            return result;
        } catch (RuntimeException e){
            e.printStackTrace();
            throw e;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            log.info("class:{},method:{},args:{},runTime:{},result:{}",className,methodName, handlerParameter(point),stopWatch.getTotalTimeMillis(), JSON.toJSONString(result));
        }
    }

    private String handlerParameter(ProceedingJoinPoint point){
        StringBuilder stringBuilder = new StringBuilder();
        MethodSignature methodSignature = (MethodSignature)point.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Class[] parameterTypes = methodSignature.getParameterTypes();
        Object[] args = point.getArgs();
        int i=0;
        for (Object pojo : args){
            stringBuilder.append("parameterName:").append(parameterNames[i]);
            stringBuilder.append("parameterType:").append(parameterTypes[i]);
            stringBuilder.append("parameterValue:").append(pojo);
        }
        return stringBuilder.toString();
    }


    //在方法执行完结后打印返回内容

}
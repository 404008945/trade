package com.xishan.store.trade.web.aop;

import com.alibaba.fastjson.JSON;
import com.xishan.store.usercenter.userapi.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
@Order(2)
public class TradeLogAspect {

    //申明一个切点 里面是 execution表达式

    @Pointcut("execution(public * com.xishan.store.trade.web.controller.*.*(..))")
    private void tradeControllerAspect() {
    }

      @Around("tradeControllerAspect()")
    public Object webLogAround(ProceedingJoinPoint point) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
          UserDTO user = null;

        if( request.getSession().getAttribute("user") != null){
            user = (UserDTO) request.getSession().getAttribute("user");
        }
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
          StopWatch stopWatch = new StopWatch();
          stopWatch.start();
          Object result = null;
        try {
            result = point.proceed();
            return result;
        } catch (Throwable e){
            e.printStackTrace();
           throw e;//可能抛出异常，并且异常不能被吃了
        } finally {
            stopWatch.stop();
            log.info("class:{},method:{},args:{},user:{},runTime:{},result:{}",className,methodName, handlerParameter(point),user,stopWatch.getTotalTimeMillis() ,JSON.toJSONString(result));
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
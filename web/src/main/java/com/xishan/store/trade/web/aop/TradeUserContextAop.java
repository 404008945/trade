package com.xishan.store.trade.web.aop;

import com.alibaba.fastjson.JSON;
import com.xishan.store.usercenter.userapi.context.UserContext;
import com.xishan.store.usercenter.userapi.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
@Order(3)
public class TradeUserContextAop {

    @Pointcut("execution(public * com.xishan.store.trade.web.controller.*.*(..))")
    private void userContextAspect() {
    }

    @Around("userContextAspect()")
    public Object webContextAround(ProceedingJoinPoint point) throws Throwable {
        //执行前，塞进UserContext中，执行后清除UserContext
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        UserDTO user = null;
        if( request.getSession().getAttribute("user") != null){
            user = (UserDTO) request.getSession().getAttribute("user");
        }
        if(user != null){
            UserContext.putCurrentUser(user);
            RpcContext.getContext().setAttachment("user",JSON.toJSONString(user));
        }
        Object result = null;
        try {
            result = point.proceed();//可能抛出异常，并且异常不能被吃了
        }catch (Throwable e){
            throw  e;
        }finally {
            UserContext.clearCurrentUser();
            RpcContext.getContext().removeAttachment("user");
        }
        return result;
        //执行目标
    }
}

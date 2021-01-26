package com.xishan.store.trade.server.aop;

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

@Aspect
//申明是个spring管理的bean
@Component
@Slf4j
@Order(2)
public class UserContextAop {

    @Pointcut("execution(public * com.xishan.store.trade.server.facade.*.*(..))")
    private void userContextAspect() {
    }

    @Around("userContextAspect()")
    public Object serviceLogAround(ProceedingJoinPoint point) throws Throwable {
        //执行前，塞进UserContext中，执行后清除UserContext
        Object userInfo = RpcContext.getContext().getAttachment("user");//web层透传过来的用户信息
        Object result = null;
        if(userInfo != null){
            UserContext.putCurrentUser(JSON.parseObject((String)userInfo,UserDTO.class));
        }
        try {
            result = point.proceed();
        } catch (Throwable throwable) {
             throw throwable;
        }
        finally {
            UserContext.clearCurrentUser();
        }

        return result;
        //执行目标
    }
}

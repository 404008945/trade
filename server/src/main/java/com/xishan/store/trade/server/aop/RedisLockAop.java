package com.xishan.store.trade.server.aop;


import com.xishan.store.trade.server.annotation.NeedRedisLock;
import com.xishan.store.trade.server.redis.RedisLock;
import com.xishan.store.trade.server.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Aspect
//申明是个spring管理的bean
@Component
@Slf4j
@Order(3)
public class RedisLockAop {//在事务之后释放锁
    //15秒未抢到，则失败，需要重新抢
    @Value("${lockExpireTime:15000}")
    private Integer lockExpireTime;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisLock redisLock;


    @Pointcut("@annotation(com.xishan.store.trade.server.annotation.NeedRedisLock)")
    private void redisLockAspect() {
    }

    /**
     * redis锁切面，获取注解，有注解，根据注解中的加锁，无注解不管
     * @param point
     * @return
     * @throws Throwable
     */
    @Order(3)
    @Around("redisLockAspect()")
    public Object redisLockAround(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        NeedRedisLock lock = method.getAnnotation(NeedRedisLock.class);
        String []values = lock.value().split("\\.");
        String []keys = lock.key().split("\\.");
        if (keys == null || keys.length != 2) {//只允许两级
            log.error("redisLock注解格式错误");
        }
        if (values == null || values.length != 2) {//只允许两级
            log.error("redisLock注解格式错误");
        }

        String[] parameterNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        if(args == null){
            log.error("方法入参错误");
        }
        Object redisKey = null;
        Object redisValue = null;
        for (int i = 0; i < args.length; i++) {
            String paramName = parameterNames[i];
            Object obg = args[i];
            if (paramName.equals(values[0])) {
                Field field = obg.getClass().getDeclaredField(values[1]);
                field.setAccessible(true);
                redisValue =  field.get(obg);
                if(! (redisValue instanceof String )){
                    log.error("幂等值必须为String");
                }
            }
            if (paramName.equals(keys[0])) {
                Field field = obg.getClass().getDeclaredField(keys[1]);
                field.setAccessible(true);
                redisKey =  field.get(obg);
                if(! (redisKey instanceof Integer )){
                    log.error("key必须为整数");
                }
            }
        }
        if (redisKey != null && redisValue != null) {
            try {
                if (redisLock.tryLock(redisUtil.makeSkuRedisKey((Integer) redisKey), (String) redisValue, lockExpireTime)) {
                    return point.proceed();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                redisLock.unlock(redisUtil.makeSkuRedisKey((Integer) redisKey), (String) redisValue);
            }
        } else {
            return point.proceed();
        }
        return null;
    }
}

package com.imooc.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {
  public static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);
  /*
   * AOP通知类型
   * 1、前置通知
   * 2、后置通知
   * 3、环绕通知：方法调用之前和之后都分别执行的通知
   * 4、异常通知：方法调用时发生异常则通知
   * 5、最终通知：在方法调用之后执行：类似try/catch的finally
   * */

  /*
   * 切面表达式
   * execution:代表所要执行的表达式主体
   * 第一处 代表方法返回类型  *表示所有类型
   * 第二处 包名代表aop监控的类所在的包
   * 第三处 ..代表该包及其子包下的所有类方法
   * 地四处 *代表类型，*代表所有类
   * 第五处 *(..) *代表类中的方法名 （..）表示方法中的任何参数
   * */
  @Around("execution(* com.imooc.service.impl..*.*(..))")
  public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
    log.info(
        "=======方法开始执行{}.{}=======",
        joinPoint.getTarget().getClass(),
        joinPoint.getSignature().getName());

    // 记录开始时间
    long beginTime = System.currentTimeMillis();
    Object result = joinPoint.proceed(); // 执行目标方法
    // 记录结束时间
    long endTime = System.currentTimeMillis();
    long takeTime = endTime - beginTime;
    if (takeTime > 3000) {
      //log.error("====执行结束，耗时：{}毫秒 ====", takeTime);
    } else if (takeTime > 2000) {
      //log.warn("====执行结束，耗时：{}毫秒 ====", takeTime);
    } else {
     // log.info("====执行结束，耗时：{}毫秒 ====", takeTime);
    }
    return result;
  }
}

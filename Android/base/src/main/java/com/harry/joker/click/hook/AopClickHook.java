//package com.harry.joker.click.hook;
//
//import android.util.Log;
//import android.view.View;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//import java.util.Arrays;
//
///**
// * Author: HarryJoker
// * Created on: 2020-01-02 21:41
// * Description:
// */
//@Aspect
//public class AopClickHook {
////    private static Long sLastclick = 0L;
//    private static final Long FILTER_TIMEM = 1000L;
//
//    /**
//     * 定义切点，标记切点为所有被@AopOnclick注解的方法
//     */
////    @Pointcut("execution(@com.harry.joker.click.hook.AopOnclick * *(..))")
////    public void methodAnnotated() {}
//
//
//    /**
//     * 定义切面方法
//     * @param joinPoint
//     */
//    @Around("execution(* android.view.View.OnClickListener.onClick(..))")
//    public void hookClick(ProceedingJoinPoint joinPoint) {
//        Log.e("AopClickHook", "click:.............");
//        try {
//
//            // 取出方法的注解
//            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//            Method method = methodSignature.getMethod();
//            Annotation[] annotations = method.getDeclaredAnnotations();
//            Log.e("AopClickHook", "annotations:" + Arrays.deepToString(annotations));
//
//            //参数不是一位
//            if (joinPoint.getArgs().length != 1) {
//                Log.e("AopClickHook", "onClick params is more than one .............");
//                return;
//            }
//            //一位参数不是View类型
//            if (!(joinPoint.getArgs()[0] instanceof View)) {
//                Log.e("AopClickHook", "is not View click, is : " + joinPoint.getArgs()[0] + ".............");
//                return;
//            }
//
//            //快速点击
//            if (AopClickUtil.isFastDoubleClick((View) joinPoint.getArgs()[0], FILTER_TIMEM)) {
//                Log.e("AopClickHook", "is fast click .............");
//            }
//
//            // 不是快速点击，执行原方法
//            joinPoint.proceed();
//            Log.e("AopClickHook", "onClick method proceed.............");
//
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//    }
//}
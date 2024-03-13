package com.sky.aop;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Component
@Aspect
public class AutoFiiAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void pt(){}

    /**
     * 根据不同操作类型来完成自动填充信息
     */
    @Before("pt()")
    public void autoFill(JoinPoint joinPoint) throws Exception {
        log.info("开始进行公共字段自动填充...");
        //判断填充的类型是哪种----方法上的注解里的属性
        MethodSignature  signature = (MethodSignature)joinPoint.getSignature();//signature没有获得方法对象的方法，所以转换橙子类得到方法，总结，当前类获取不到合适方法时可以去实现类或者子类查找相应方法
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType type = annotation.value();
        //拿到要填充的字段信息
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }

//        Employee employee = (Employee)args[0];
        Object obj = args[0];

        //获取创建或修改者的信息
        Long idUpdaterOrCreater = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();

        //通过反射填充字段
        if (type.equals(OperationType.INSERT)){
            /*employee.setCreateUser(idUpdaterOrCreater);
            employee.setUpdateUser(idUpdaterOrCreater);
            employee.setCreateTime(LocalDateTime.now());
            employee.setUpdateTime(LocalDateTime.now());*/
            try {
                //通过反射设置对象的属性
                Method setCreateTime = obj.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = obj.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = obj.getClass().getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = obj.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(obj,now);
                setUpdateTime.invoke(obj,now);
                setCreateUser.invoke(obj,idUpdaterOrCreater);
                setUpdateUser.invoke(obj,idUpdaterOrCreater);

            } catch (Exception e) {
                throw new Exception(e);
            }
        }

        if (type.equals(OperationType.UPDATE)){
            try {
                Method setUpdateTime = obj.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = obj.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(obj,now);
                setUpdateUser.invoke(obj,idUpdaterOrCreater);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
    }
}

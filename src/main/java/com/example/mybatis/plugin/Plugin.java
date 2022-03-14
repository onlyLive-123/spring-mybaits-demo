package com.example.mybatis.plugin;

import com.example.mybatis.plugin.annotation.Intercepts;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Plugin implements InvocationHandler {

    Object target;
    Interceptor interceptor;

    public Plugin(Object target, Interceptor interceptor) {
        this.target = target;
        this.interceptor = interceptor;
    }

    public static Object wrap(Object target, Interceptor interceptor) {
        Class<?> clazz = target.getClass();
        Class<?>[] interfaces = clazz.getInterfaces();
        return Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, new Plugin(target, interceptor));
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获取注解上的内容 这里简便了 直接对应statementId
        Intercepts annotation = interceptor.getClass().getAnnotation(Intercepts.class);
        //arg里面的参数 源码是封装成ms传递的 这里省略了
        //就两个参数通过selectList传递过来的 selectList(sqlCommend.getStatementId(), args)
        //通过被代理的原始类的方法判断是否走代理逻辑
        if (annotation.method().equals(target.getClass().getName()+"."+method.getName())) {
            return interceptor.intercept(new Invocation(target, method, args));
        }
        return method.invoke(target, args);
    }
}

package com.example.mybatis.plugin;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
public class Invocation {

    Object target;
    Method method;
    Object[] args;

    public Invocation(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    public Object process() throws InvocationTargetException, IllegalAccessException {
        return this.method.invoke(target, args);
    }
}

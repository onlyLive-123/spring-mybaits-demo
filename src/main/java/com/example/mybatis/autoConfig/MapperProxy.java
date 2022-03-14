package com.example.mybatis.autoConfig;

import com.example.mybatis.config.Configuration;
import com.example.mybatis.config.MapperMethod;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MapperProxy<T> implements InvocationHandler, Serializable {

    Class<T> mapperInterface;
    Configuration configuration;

    public MapperProxy(Class<T> mapperInterface, Configuration configuration) {
        this.mapperInterface = mapperInterface;
        this.configuration = configuration;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //处理sql的相关信息
        MapperMethod mapperMethod = new MapperMethod(configuration, method, this.mapperInterface);
        //真正的调用从这里开始的
        return mapperMethod.execute(args);
    }
}

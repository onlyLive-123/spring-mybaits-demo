package com.example.mybatis.autoConfig;

import com.example.mybatis.config.Configuration;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

@Data
public class MapperFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;
    Configuration configuration;

    public MapperFactoryBean() {

    }

    //此类为工厂bean 初始化该类时可以自己定义实例化对象 这里采用动态代理生成返回的对象 调用类为MapperProxy
    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class[]{mapperInterface}, new MapperProxy<T>(mapperInterface, configuration));
    }

    @Override
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }
}

package com.example.mybatis.session;

import com.example.mybatis.config.Configuration;
import com.example.mybatis.executor.BaseExecutor;
import com.example.mybatis.executor.Executor;
import com.example.mybatis.plugin.Interceptor;
import com.example.mybatis.statement.SqlCommend;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class SqlSessionInvocation implements InvocationHandler, Serializable {

    Configuration configuration;
    SqlCommend sqlCommend;

    public SqlSessionInvocation(Configuration configuration, SqlCommend sqlCommend) {
        this.configuration = configuration;
        this.sqlCommend = sqlCommend;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            //获取一个新的sqlSession
            DefualtSqlSession sqlSession = getSqlSession();

            Object result = method.invoke(sqlSession, args);

            //做事务的提交 todo
            return result;
        } catch (Exception e) {
            //回滚之类的
            throw new RuntimeException(e);
        }

    }

    private DefualtSqlSession getSqlSession() {
        //创建执行器
        Executor executor = new BaseExecutor(configuration, sqlCommend);

        //扫描插件实现
        executor = (Executor) this.pluginAll(executor);

        return new DefualtSqlSession(executor);
    }

    private Object pluginAll(Object executor) {
        //插件
        List<Object> interfaceList = configuration.getPluGinInterfaceList();

        for (Object interceptor : interfaceList) {
            Interceptor instance;
            try {
                instance = (Interceptor) ((Class<?>) interceptor).newInstance();
            } catch (Exception e) {
                continue;
            }

            executor = instance.plugin(executor);
        }
        return executor;
    }
}

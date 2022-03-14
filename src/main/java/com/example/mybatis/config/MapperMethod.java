package com.example.mybatis.config;

import com.example.mybatis.session.SqlSession;
import com.example.mybatis.session.SqlSessionInvocation;
import com.example.mybatis.statement.SqlCommend;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class MapperMethod {


    SqlCommend sqlCommend;
    SqlSession sqlSessionProxy;
    Method method;


    public MapperMethod(Configuration configuration, Method method, Class<?> mapperInterface) {
        this.method = method;
        this.sqlCommend = new SqlCommend();
        String statementId = mapperInterface.getName() + "." + method.getName();
        sqlCommend.setStatementId(statementId);
        sqlCommend.setResultType(method.getReturnType());
        sqlCommend.setParamType(method.getParameterTypes());
        sqlCommend.setSql(configuration.getMappedStatements().get(statementId));

        this.sqlSessionProxy = createSqlSessionProxy(configuration);
    }

    private SqlSession createSqlSessionProxy(Configuration configuration) {
        return (SqlSession) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{SqlSession.class}, new SqlSessionInvocation(configuration, sqlCommend));
    }


    public Object execute(Object[] args) {
        switch (sqlCommend.getType()) {
            case "SELECT":
                Class<?> returnType = this.method.getReturnType();
                if (List.class.equals(returnType)) {
                    return this.sqlSessionProxy.selectList(sqlCommend.getStatementId(), args);
                } else if (!void.class.equals(returnType)) {
                    return this.sqlSessionProxy.selectOne(sqlCommend.getStatementId(), args);
                }
            case "UPDATE":
            case "INSERT":
                return this.sqlSessionProxy.update(sqlCommend.getStatementId(), args);
            case "DELETE":
                return this.sqlSessionProxy.delete(sqlCommend.getStatementId(), args);
        }
        throw new RuntimeException("sql类型异常," + sqlCommend.getType());
    }
}

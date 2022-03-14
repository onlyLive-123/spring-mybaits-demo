package com.example.mybatis.session;

import com.example.mybatis.executor.Executor;
import lombok.Data;

import java.util.List;

@Data
public class DefualtSqlSession implements SqlSession {

    Executor executor;

    public DefualtSqlSession(Executor executor) {
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<Object> objects = this.selectList(statement, parameter);
        if (objects != null && objects.size() > 0) {
            return (T) objects.get(0);
        }
        return null;
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        //可能是插件的代理类 多层
        //执行前sql修改或参数修改 执行后的返回字段修改或者转义
        return this.executor.query(statement, parameter);
    }

    @Override
    public int update(String statement, Object parameter) {
        return this.executor.update(statement, parameter);
    }


    @Override
    public int delete(String statement, Object parameter) {
        return this.executor.delete(statement, parameter);
    }
}

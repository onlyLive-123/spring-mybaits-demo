package com.example.mybatis.executor;

import java.util.List;

public interface Executor {

    <E> List<E> query(String statement, Object parameter);

    int update(String statement, Object parameter);

    int delete(String statement, Object parameter);
}

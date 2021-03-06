package com.example.mybatis.session;

import java.util.List;

public interface SqlSession {

    <T> T selectOne(String statement, Object parameter);

    <E> List<E> selectList(String statement, Object parameter);

    int update(String statement, Object parameter);
    
    int delete(String statement, Object parameter);
}

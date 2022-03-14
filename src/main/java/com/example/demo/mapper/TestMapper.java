package com.example.demo.mapper;

import com.example.mybatis.annotation.TMapper;

import java.util.List;
import java.util.Map;

@TMapper
public interface TestMapper {

    List<Map<String, Object>> selectList(Integer status);

    int update(Map<String,Object> map);

    int insert(Map<String,Object> map);

    Map<String, Object> selectById(Long id);
}

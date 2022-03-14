package com.example.mybatis.config;

import lombok.Data;

import java.util.*;

@Data
public class Configuration {
    //mapper class类集合
    public List<Object> mapperInterfaceList = new ArrayList<>();
    //插件class类集合
    public List<Object> pluGinInterfaceList = new ArrayList<>();
    //statementId -> sql
    public Map<String, String> mappedStatements = new HashMap<>();
    //配置
    Properties properties;
}

package com.example.mybatis.statement;

import lombok.Data;

@Data
public class SqlCommend {

    String statementId;
    String sql;
    Class<?> resultType;
    Class<?>[] paramType;
    String type;

    public void setSql(String sql) {
        this.sql = sql;
        String[] split = sql.split(" ");
        if (split.length > 0) {
            this.type = split[0].toUpperCase();
        }
    }
}

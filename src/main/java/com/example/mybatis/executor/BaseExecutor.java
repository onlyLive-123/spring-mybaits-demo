package com.example.mybatis.executor;

import com.example.mybatis.config.Configuration;
import com.example.mybatis.statement.SqlCommend;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

@Getter
@Slf4j
public class BaseExecutor implements Executor {

    Configuration configuration;
    SqlCommend sqlCommend;
    static String driverClassName = "driver-class-name";
    static String url = "url";
    static String username = "username";
    static String password = "password";


    public BaseExecutor(Configuration configuration, SqlCommend sqlCommend) {
        this.configuration = configuration;
        this.sqlCommend = sqlCommend;
    }

    @Override
    public int update(String statement, Object parameter) {
        Statement st = null;
        try {
            //预处理sql
            String sql = this.getBoundSql(parameter);
            //连接数据库
            st = prepareStatement();
            //执行查询
            st.executeUpdate(sql);
            //处理结果集
            return st.getUpdateCount();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        } finally {
            closeStatement(st);
        }
    }

    @Override
    public int delete(String statement, Object parameter) {
        Statement st = null;
        try {
            //预处理sql
            String sql = this.getBoundSql(parameter);
            //连接数据库
            st = prepareStatement();
            //执行查询
            st.execute(sql);
            //处理结果集
            return st.getUpdateCount();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        } finally {
            closeStatement(st);
        }
    }

    @Override
    public <E> List<E> query(String statement, Object parameter) {
        Statement st = null;
        try {
            //预处理sql
            String sql = this.getBoundSql(parameter);
            //连接数据库
            st = prepareStatement();
            //执行查询
            ResultSet rs = st.executeQuery(sql);
            //处理结果集
            return handlerResultSet(rs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            closeStatement(st);
        }
    }

    private void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //这里只简单实现map和单字段传参
    //源码中GenericTokenParser#parse解析 SqlSourceBuilder#handleToken存储字段顺序
    // 实现流程 使用下标匹配#{和}得到每个字段的名称 添加到参数Mapping 然后替换成?
    // 最后传入的参数会处理成数组的方式传输 交由parameterHandler按顺序取值 parameterHandler可以被插件代理
    private String getBoundSql(Object parameter) throws Exception {
        String sql = this.sqlCommend.getSql();
        Object[] args = (Object[]) parameter;
        if (args.length > 0) {
            if (args[0] instanceof Map) {
                Map<String, Object> map = (Map) args[0];
                for (String str : map.keySet()) {
                    sql = sql.replace("#{" + str + "}",
                            "'" + map.get(str).toString() + "'");
                }
            } else {
                sql = sql.replaceAll("#\\{.*?}", "%s");
                sql = String.format(sql, args);
            }
        }
        log.info("execute sql:{}", sql);
        return sql;
    }

    //resultHandler 可以被插件多层代理
    private <E> List<E> handlerResultSet(ResultSet rs) throws Exception {
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        //获取列数据
        ResultSetMetaData metaData = rs.getMetaData();
        while (rs.next()) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                map.put(metaData.getColumnName(i), rs.getObject(i));
            }
            mapList.add(map);
        }
        log.info("result:{}", mapList);
        return (List<E>) mapList;
    }

    private Statement prepareStatement() {
        try {
            Properties properties = configuration.getProperties();
            Class.forName(properties.getProperty(driverClassName));
            //conn正常是放数据池管理的
            Connection conn = DriverManager.getConnection(
                    properties.getProperty(url),
                    properties.getProperty(username),
                    properties.getProperty(password));
            //这个相当于session 每次用完就关闭
            Statement stmt = conn.createStatement();
            return stmt;
        } catch (Exception e) {
            throw new RuntimeException("数据库连接失败,e:" + e);
        }
    }
}

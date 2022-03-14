package com.example.demo.plugin;

import com.example.mybatis.executor.BaseExecutor;
import com.example.mybatis.plugin.Interceptor;
import com.example.mybatis.plugin.Invocation;
import com.example.mybatis.plugin.annotation.Intercepts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Intercepts(method = "com.example.mybatis.executor.BaseExecutor.query")
public class PluginService implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object target = invocation.getTarget();
            log.info("Sql{}", ((BaseExecutor) target).getSqlCommend().getSql());

            Object[] args = invocation.getArgs();
            log.info("statementId：{}", args[0]);
            log.info("参数：{}", args[1]);
        } catch (Exception e) {
            log.error("插件异常！");
        }
        return invocation.process();
    }
}

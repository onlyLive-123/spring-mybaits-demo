package com.example.mybatis.autoConfig;

import com.example.mybatis.annotation.TMapper;
import com.example.mybatis.config.Configuration;
import com.example.mybatis.plugin.annotation.Intercepts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
public class MapperAutoConfig implements BeanDefinitionRegistryPostProcessor {

    public Properties properties = new Properties();
    Configuration configuration = new Configuration();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //1.扫描mapper接口
        doScanner("com.example.demo", TMapper.class, this.configuration.getMapperInterfaceList());
        //初始化插件
        doScanner("com.example.demo", Intercepts.class, this.configuration.getPluGinInterfaceList());

        //2.扫描sql 这里方便读取存放到配置文件了
        doScannerStatement("application.yml");

        //3.将mapper接口和sql文件保存到MapperFactoryBean,然后注册到容器中去
        doRegister(registry);
    }

    private void doRegister(BeanDefinitionRegistry registry) {
        for (Object object : this.configuration.getMapperInterfaceList()) {
            Class<?> clazz = (Class<?>) object;
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            beanDefinition.setBeanClass(MapperFactoryBean.class);
            beanDefinition.getPropertyValues().add("mapperInterface", clazz.getName());
            beanDefinition.getPropertyValues().add("configuration", this.configuration);
            //注册到容器 beanName是接口全名 beanClass替换成MapperFactoryBean
            registry.registerBeanDefinition(clazz.getName(), beanDefinition);

            log.info("add mapper for " + clazz.getName());
        }
    }

    private void doScannerStatement(String path) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.configuration.setProperties(this.properties);
        for (Object object : this.configuration.getMapperInterfaceList()) {
            Class<?> clazz = (Class<?>) object;
            String name = clazz.getName();
            for (Method method : clazz.getMethods()) {
                String statementId = name + "." + method.getName();
                if (!this.properties.containsKey(statementId)) {
                    throw new RuntimeException("statementId:" + statementId + " not found");
                }
                this.configuration.getMappedStatements().put(statementId, this.properties.getProperty(statementId));
            }
        }
    }

    private void doScanner(String packetPath, Class<? extends Annotation> tMapperClass, List<Object> interfaceList) {
        try {
            URL url = this.getClass().getResource("/" + packetPath.replaceAll("\\.", "\\/"));
            File file = new File(url.getPath());
            for (File listFile : file.listFiles()) {
                if (listFile.isDirectory()) {
                    doScanner(packetPath + "." + listFile.getName(), tMapperClass, interfaceList);
                } else {
                    if (!listFile.getName().endsWith(".class")) continue;
                    Class<?> clazz = Class.forName(packetPath + "." + listFile.getName().replace(".class", ""));
                    if (clazz.isAnnotationPresent(tMapperClass)) {
                        interfaceList.add(clazz);
                        log.info("scanner mapper {}", clazz.getName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("doScanner 异常，:", e);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }


}

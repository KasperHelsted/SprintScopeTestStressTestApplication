package com.tester.stress.scope;

import jakarta.servlet.ServletContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Configuration
public class TenantBeanFactoryPostProcessor {
    public static final String TESTER = "SCOPE";

    public TenantBeanFactoryPostProcessor(ConfigurableListableBeanFactory factory, @Nullable ServletContext servletContext) throws BeansException {
        factory.registerScope(TESTER, new CustomScope(servletContext));
    }
}
package com.maven.neuto.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.Properties;


@Configuration
public class FreeMarkerConfig {


    @Primary
    @Bean
    public FreeMarkerConfigurationFactoryBean bean() {
        FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
        bean.setTemplateLoaderPath("classpath:/templates");
        Properties settings = new Properties();
        settings.put("template_exception_handler", "rethrow");
        settings.put("default_encoding", "UTF-8");
        bean.setFreemarkerSettings(settings);
        return bean;
    }


}

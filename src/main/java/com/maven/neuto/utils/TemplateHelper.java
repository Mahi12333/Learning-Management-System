package com.maven.neuto.utils;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateHelper {

    private final Configuration freemarkerConfig;

    public String buildEmail(String templateName, Map<String, Object> model) throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate("email/" + templateName + ".ftl");
        StringWriter out = new StringWriter();
        template.process(model, out);
        return out.toString();
    }
}

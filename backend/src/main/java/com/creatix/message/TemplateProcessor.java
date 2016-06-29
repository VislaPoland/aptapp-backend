package com.creatix.message;

import com.creatix.message.template.MessageTemplate;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;

abstract class TemplateProcessor {

    private final StringTemplateLoader templateLoader = new StringTemplateLoader();

    public String processTemplate(MessageTemplate model) throws IOException, TemplateException
    {
        templateLoader.putTemplate(model.getTemplateName(), FileUtils.readFileToString(Paths.get("/resources/templates", subPath(), model.getTemplateName()).toFile(), Charset.defaultCharset()));
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setTemplateLoader(templateLoader);
        Template template = cfg.getTemplate(model.getTemplateName());

        try ( StringWriter writer = new StringWriter() ) {
            template.process(model, writer);
            return writer.toString();
        }
    }

    protected abstract String subPath();

}

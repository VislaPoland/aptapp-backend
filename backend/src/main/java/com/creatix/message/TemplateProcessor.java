package com.creatix.message;

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

    private String processReportTemplate(String templateName, Object model) throws IOException, TemplateException
    {
        templateLoader.putTemplate(templateName, FileUtils.readFileToString(Paths.get("/resources/templates", subPath(), templateName).toFile(), Charset.defaultCharset()));
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setTemplateLoader(templateLoader);
        Template template = cfg.getTemplate(templateName);

        try ( StringWriter writer = new StringWriter() ) {
            template.process(model, writer);
            return writer.toString();
        }
    }

    protected abstract String subPath();

}

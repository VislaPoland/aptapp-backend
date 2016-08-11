package com.creatix.message;

import com.creatix.message.template.MessageTemplate;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;

abstract class TemplateProcessor {

    private final StringTemplateLoader templateLoader = new StringTemplateLoader();

    public String processTemplate(MessageTemplate model) throws IOException, TemplateException
    {
        final ClassLoader classLoader = getClass().getClassLoader();
        final String templatePath = Paths.get("templates", subPath(), model.getTemplateName() + ".ftl").toString();
        try ( final InputStream templateResourceStream = classLoader.getResourceAsStream(templatePath) ) {
            if ( templateResourceStream == null ) {
                throw new IOException("Resource " + templatePath + " not found.");
            }

            synchronized ( templateLoader ) {
                templateLoader.putTemplate(model.getTemplateName(), IOUtils.toString(templateResourceStream, Charset.defaultCharset()));
            }
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        Template template;
        synchronized ( templateLoader ) {
            cfg.setTemplateLoader(templateLoader);
            template = cfg.getTemplate(model.getTemplateName());
        }

        try ( StringWriter writer = new StringWriter() ) {
            template.process(model, writer);
            return writer.toString();
        }
    }

    protected abstract String subPath();

}

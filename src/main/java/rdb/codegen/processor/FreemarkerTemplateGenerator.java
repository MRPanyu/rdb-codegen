package rdb.codegen.processor;

import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * This generator uses Freemarker template file to generate output.
 * 
 * @author Panyu
 *
 */
public class FreemarkerTemplateGenerator extends AbstractTemplateGenerator {

	private Configuration cfg;

	@Override
	protected void initialize() throws Exception {
		cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "");
		cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
	}

	@Override
	protected void applyTemplate(Map<String, Object> context, String templateContent, Writer output) throws Exception {
		Template template = new Template("template", templateContent, cfg);
		template.process(context, output);
	}

}

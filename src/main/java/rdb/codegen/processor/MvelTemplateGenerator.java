package rdb.codegen.processor;

import java.io.Writer;
import java.util.Map;

import org.mvel2.templates.TemplateRuntime;

/**
 * This generator uses MVEL({@link https://github.com/mvel/mvel}) template file
 * to generate output.
 * 
 * @author Panyu
 *
 */
public class MvelTemplateGenerator extends AbstractTemplateGenerator {

	@Override
	protected void applyTemplate(Map<String, Object> context, String templateContent, Writer output) throws Exception {
		String result = TemplateRuntime.eval(templateContent, context).toString();
		output.write(result);
	}

}

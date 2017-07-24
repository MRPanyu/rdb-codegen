package rdb.codegen.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import rdb.codegen.framework.model.Database;
import rdb.codegen.framework.model.Table;
import rdb.codegen.framework.utils.Utils;

/**
 * Abstract <code>ModelProcessor</code> to generate output from template file.
 * 
 * @author Panyu
 *
 */
public abstract class AbstractTemplateGenerator extends AbstractConfigurableProcessor {

	public static final String TEMPLATE_FOR_DATABASE = "database";
	public static final String TEMPLATE_FOR_TABLE = "table";

	protected Log logger = LogFactory.getLog(this.getClass());

	protected Properties configProperties;
	protected String templateFor = TEMPLATE_FOR_TABLE;
	protected String templateFile;
	protected String outputFile;
	protected String outputWhen;
	protected String outputEncoding = "UTF-8";

	public Properties getConfigProperties() {
		return configProperties;
	}

	public void setConfigProperties(Properties configProperties) {
		this.configProperties = configProperties;
	}

	/**
	 * Whether this template generates one for each table or one for whole database.
	 */
	public String getTemplateFor() {
		return templateFor;
	}

	/**
	 * Whether this template generates one for each table or one for whole database.
	 */
	public void setTemplateFor(String templateFor) {
		if (!templateFor.equalsIgnoreCase(TEMPLATE_FOR_DATABASE) && !templateFor.equalsIgnoreCase(TEMPLATE_FOR_TABLE)) {
			throw new RuntimeException("templateFor can only be set to \"" + TEMPLATE_FOR_TABLE + "\" or \""
					+ TEMPLATE_FOR_DATABASE + "\".");
		}
		this.templateFor = templateFor.toLowerCase();
	}

	/** Path to template file */
	public String getTemplateFile() {
		return templateFile;
	}

	/** Path to template file */
	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}

	/**
	 * Path to output file, may contain expression relate to table or database.
	 * Default implementation uses an MVEL({@link https://github.com/mvel/mvel})
	 * template format.
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * Path to output file, may contain expression relate to table or database.
	 * Default implementation uses an MVEL({@link https://github.com/mvel/mvel})
	 * template format.
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * Expression of whether to generate the output file for this table, default
	 * implementation uses an MVEL({@link https://github.com/mvel/mvel}) expression.
	 */
	public String getOutputWhen() {
		return outputWhen;
	}

	/**
	 * Expression of whether to generate the output file for this table, default
	 * implementation uses an MVEL({@link https://github.com/mvel/mvel}) expression.
	 */
	public void setOutputWhen(String outputWhen) {
		this.outputWhen = outputWhen;
	}

	public String getOutputEncoding() {
		return outputEncoding;
	}

	public void setOutputEncoding(String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	@Override
	public Database process(Database model) throws Exception {
		initialize();
		String templateContent = Utils.loadResource(templateFile, "UTF-8");
		if (TEMPLATE_FOR_DATABASE.equals(templateFor)) {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("configProperties", configProperties);
			context.put("config", configProperties);
			context.put("model", model);
			context.put("database", model);
			doGenerate(context, templateContent);
		} else {
			for (Table table : model.getTables()) {
				Map<String, Object> context = new HashMap<String, Object>();
				context.put("configProperties", configProperties);
				context.put("config", configProperties);
				context.put("model", model);
				context.put("database", model);
				context.put("table", table);
				doGenerate(context, templateContent);
			}
		}
		return null;
	}

	private void doGenerate(Map<String, Object> context, String templateContent) throws Exception {
		boolean doOutput = true;
		if (StringUtils.isNotBlank(outputWhen)) {
			doOutput = evalOutputWhen(context);
		}
		if (doOutput) {
			String outputPath = evalOutputFile(context);
			logger.info("Writing output file: " + outputPath);
			File outFile = new File(outputPath);
			outFile.getParentFile().mkdirs();
			Writer output = new OutputStreamWriter(new FileOutputStream(outputPath), outputEncoding);
			try {
				applyTemplate(context, templateContent, output);
			} finally {
				output.close();
			}
		}
	}

	/**
	 * Can be overwritten by sub class to perform some initialize action.
	 */
	protected void initialize() throws Exception {
		// default empty
	}

	/**
	 * To be implemented by sub classes, combine the template with context to
	 * generate result
	 * 
	 * @param context
	 *            context containing model and table variables
	 * @param templateContent
	 *            template file content
	 * @param output
	 */
	protected abstract void applyTemplate(Map<String, Object> context, String templateContent, Writer output)
			throws Exception;

	/**
	 * Controls how to evaluate the <code>outputWhen</code> expression. The default
	 * implementation accepts an MVEL expression which returns boolean value.
	 */
	protected boolean evalOutputWhen(Map<String, Object> context) {
		return (Boolean) MVEL.eval(outputWhen, context);
	}

	/**
	 * Controls how to evaluate the <code>outputPath</code> expression. The default
	 * implementation accepts an MVEL template expression.
	 */
	protected String evalOutputFile(Map<String, Object> context) {
		return TemplateRuntime.eval(outputFile, context).toString();
	}

}

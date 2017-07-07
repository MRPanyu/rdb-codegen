package rdb.codegen.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Types;

import org.apache.commons.io.IOUtils;

public class Utils {

	public static String getJdbcTypeName(int jdbcType) {
		try {
			Field[] fields = Types.class.getFields();
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers()) && ((Number) field.get(null)).intValue() == jdbcType) {
					return field.getName();
				}
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String loadResource(String path, String encoding) throws IOException {
		InputStream ins = null;
		if (path.startsWith("classpath:")) {
			String realPath = path.substring("classpath:".length());
			while (realPath.startsWith("/")) {
				realPath = realPath.substring(1);
			}
			ins = Utils.class.getClassLoader().getResourceAsStream(realPath);
		} else {
			String realPath = path;
			if (realPath.startsWith("file://")) {
				realPath = realPath.substring("file://".length());
			} else if (realPath.startsWith("file:")) {
				realPath = realPath.substring("file:".length());
			}
			ins = new FileInputStream(realPath);
		}
		String content = IOUtils.toString(ins, encoding);
		ins.close();
		return content;
	}

}

package rdb.codegen.framework.api;

import rdb.codegen.framework.model.Database;

/**
 * A <code>ModelProcessor</code> can do anything with the model, like adding ext
 * properties or generating files.
 * 
 * @author Panyu
 *
 */
public interface ModelProcessor {

	/**
	 * Processes the model
	 * 
	 * @param model
	 *            Data model
	 * @return A new model object can be returned for further process. If null
	 *         then input model object will be used for further
	 *         <code>ModelProcessor</code>s.
	 * @throws Exception
	 */
	Database process(Database model) throws Exception;

}

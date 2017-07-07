package rdb.codegen.framework.api;

import rdb.codegen.framework.model.Database;

/**
 * A <code>ModelReader</code> constructs a <code>Database</code> model from any
 * source (Database connection, some type of database model files, etc.).
 * 
 * @author Panyu
 *
 */
public interface ModelReader {

	Database read() throws Exception;

}

package rdb.codegen.framework.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for database foreign key constraint.
 * 
 * @author Panyu
 * 
 */
@SuppressWarnings("serial")
public class ForeignKey extends Constraint {

	private String referenceTableName;
	private List<String> referenceColumnNames = new ArrayList<String>();

	public String getReferenceTableName() {
		return referenceTableName;
	}

	public void setReferenceTableName(String referenceTableName) {
		this.referenceTableName = referenceTableName;
	}

	public List<String> getReferenceColumnNames() {
		return referenceColumnNames;
	}

	public void setReferenceColumnNames(List<String> referenceColumnNames) {
		this.referenceColumnNames = referenceColumnNames;
	}

}

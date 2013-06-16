/**
 * 
 */
package org.minnal.generator;

import java.util.ArrayList;
import java.util.List;

import org.minnal.generator.core.ModelGenerator;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author ganeshs
 *
 */
@Parameters(separators = "=", commandDescription = "Generates a model class")
public class CommandGenerateModel implements Command {
	
	@Parameter(description = "The name of the model to create", required=true)
	private List<String> values = new ArrayList<String>();
	
	@Parameter(names = "-fields", converter=FieldConverter.class, variableArity=true, description = "The fields in the model. " +
			"Format name:type:searchable. Type is the java type of the field (string, integer, long, short, char, double, float, date, timestamp, boolean). " +
			"Searchable is a boolean that specifies if the field is a searchable field")
	private List<Field> fields;
	
	@Parameter(names = "-projectDir", description = "The project directory")
	private String projectDir = System.getProperty("user.dir");
	
	@Parameter(names = "-aggregateRoot", description = "Is this model an aggregate root?")
	private boolean aggregateRoot;

	@Override
	public void execute() {
		ModelGenerator generator = new ModelGenerator(this);
		generator.init();
		generator.generate();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return values.get(0);
	}

	/**
	 * @return the fields
	 */
	public List<Field> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	/**
	 * @return the projectDir
	 */
	public String getProjectDir() {
		return projectDir;
	}

	/**
	 * @param projectDir the projectDir to set
	 */
	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
	}

	/**
	 * @return the aggregateRoot
	 */
	public boolean isAggregateRoot() {
		return aggregateRoot;
	}

	/**
	 * @param aggregateRoot the aggregateRoot to set
	 */
	public void setAggregateRoot(boolean aggregateRoot) {
		this.aggregateRoot = aggregateRoot;
	}

	public static class FieldConverter implements IStringConverter<Field> {
		
		public FieldConverter() {
		}

		@Override
		public Field convert(String value) {
			return new Field(value);
		}
		
	}
}

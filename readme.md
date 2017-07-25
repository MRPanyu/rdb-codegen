# A simple customizable code generator for relational databases

##With very simple design:

* Model: Database, Table, Column, PrimaryKey, ForeignKey, Unique, each with an associated java class. All of these are Extendible entities (which has an "ext" map property for setting arbitrary values).

* ModelReader: Read from any source (jdbc connection or sql file maybe?) and builds a Database model.

* ModelProcessor: Do arbitrary things to/with the Database model, such as property enhancement (like determine java class names from table names) or code generation.

* Configuration/Execution: Utility classes to chain ModelReader and ModelProcessors from config file.

##Currently implemented Configuration:

* XmlConfiguration: Reads from xml config file, see config-sample.xml.
* YmlConfiguration: Reads from yml config file, see config-sample.yml.

##Currently implemented ModelReaders:

* OracleJDBCModelReader: for Oracle database.
* MySqlJDBCModelReader: for MySql database.

##Currently implemented ModelProcessors:

* DefaultClassNameSetter/DefaultPropertyNameSetter/DefaultPropertyTypeSetter/ForeignKeyReferenceSetter: enhances table model with custom "ext" properties to be use in templates.
* MvelTemplateGenerator/FreemarkerTemplateGenerator: generates file by template, there is some basic mvel template file (for generating jpa entities) in the mvel-template-jpa-sample folder.
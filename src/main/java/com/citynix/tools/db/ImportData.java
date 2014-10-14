package com.citynix.tools.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
//import org.dbunit.ant.Operation;

/***
 * Imports data in flat xml format into the db. The operation is REFERESH. This
 * is done through DBUnit.
 * 
 * 
 * @author <a href="mailto:mansour.alakeel@gmail.com">Mansour Al Akeel</a>
 * 
 * @requiresDependencyResolution compile
 * @configurator include-project-dependencies
 */
@Mojo(name = "import-data")
public class ImportData extends CommonConfigurationMojo {

    @Parameter(readonly = true, required = true, alias = "data-files")
    private List<File> dataFiles;

    private String metadataHandlerName;
    private String type;
    private String schema;

    public ImportData()
    {
	this.metadataHandlerName = "org.dbunit.database.DefaultMetadataHandler";
	this.type = "REFRESH";
    }

    public void setDataFiles(List<String> dataFiles)
    {
	this.dataFiles = this.fileNamesToList(dataFiles);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {

	IDatabaseConnection connection = null;

	try
	{
	    super.setupClassPath();

	    connection = createConnection();

	    List<File> files = this.dataFiles;

	    super.getLog().info("Loading " + files.size() + " files");

	    for (File file : files)
	    {
		this.load(file, connection);
	    }

	} catch (Exception e)
	{
	    throw new MojoExecutionException("Error executing database operation: " + type, e);
	} finally
	{
	    if (connection != null)
		try
		{
		    connection.close();
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
	}
    }

    IDatabaseConnection createConnection() throws Exception
    {

	// Instantiate JDBC driver
	Class dc = Class.forName(driver);
	Driver driverInstance = (Driver) dc.newInstance();
	Properties info = new Properties();
	info.put("user", username);

	if (password != null)
	{
	    info.put("password", password);
	}

	Connection conn = driverInstance.connect(url, info);

	if (conn == null)
	{
	    // Driver doesn't understand the URL
	    throw new SQLException("No suitable Driver for " + url);
	}
	conn.setAutoCommit(true);

	IDatabaseConnection connection = new DatabaseConnection(conn, schema);
	// DatabaseConfig config = connection.getConfig();
	// config.setFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS,
	// supportBatchStatement);
	// config.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES,
	// useQualifiedTableNames);
	// config.setFeature(DatabaseConfig.FEATURE_DATATYPE_WARNING,
	// datatypeWarning);
	// config.setFeature(DatabaseConfig.FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES,
	// this.skipOracleRecycleBinTables);
	//
	// config.setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN,
	// escapePattern);
	// config.setProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY,
	// new ForwardOnlyResultSetTableFactory());
	//
	// // Setup data type factory
	// IDataTypeFactory dataTypeFactory = (IDataTypeFactory)
	// Class.forName(dataTypeFactoryName).newInstance();
	// config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
	// dataTypeFactory);
	//
	// // Setup metadata handler
	// IMetadataHandler metadataHandler = (IMetadataHandler)
	// Class.forName(metadataHandlerName).newInstance();
	// config.setProperty(DatabaseConfig.PROPERTY_METADATA_HANDLER,
	// metadataHandler);

	return connection;
    }

    private void load(File file, IDatabaseConnection connection) throws Exception
    {
	FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();

	builder.setColumnSensing(true);

	InputStream seedsInputStream = new FileInputStream(file);

	FlatXmlDataSet dataSet = builder.build(seedsInputStream);

	DatabaseOperation.REFRESH.execute(connection, dataSet);

    }

}

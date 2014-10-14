package com.citynix.tools.db;

import java.io.File;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ddlutils.command.CommandException;
import org.apache.ddlutils.command.PlatformConfiguration;
import org.apache.ddlutils.command.WriteSchemaToDatabaseCommand;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/***
 * Loads a file in XML format into the DB. This is done through Apache ddlutils.
 * The schema file format is the format of ddlutils.
 * 
 * @author mansour
 * 
 * @requiresDependencyResolution compile
 * @configurator include-project-dependencies
 */

@Mojo(name = "import-schema")
public class ImportSchema extends CommonConfigurationMojo {

    private BasicDataSource getDateSource()
    {
	BasicDataSource dataSource = new BasicDataSource();
	dataSource.setDriverClassName(super.driver);
	dataSource.setUrl(super.url);
	dataSource.setUsername(super.username);
	dataSource.setPassword(super.password);

	return dataSource;
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {

	List<File> schemaFiles = super.getSchemaFiles();

	if (schemaFiles.size() == 0)
	{
	    throw new IllegalStateException("Schema Files must be specified");
	}

	try
	{
	    Database model = this.readModel(new Database(), schemaFiles);

	    PlatformConfiguration platformConfig = new PlatformConfiguration();

	    BasicDataSource dataSource = getDateSource();

	    platformConfig.setDataSource(dataSource);

	    WriteSchemaToDatabaseCommand command = new WriteSchemaToDatabaseCommand(model, platformConfig);

	    command.execute();

	} catch (CommandException e)
	{
	    throw new MojoFailureException("Error: ", e);
	}
	System.out.println("Hello World");
    }

    protected Database readModel(Database model, List<File> files) throws CommandException
    {
	DatabaseIO reader = new DatabaseIO();

	// reader.setValidateXml(true);
	// reader.setUseInternalDtd(true);

	if (files.isEmpty())
	{
	    throw new CommandException(
		    "Please use either the schemafile attribute or the sub fileset element, but not both");
	}

	for (File f : files)
	{
	    Database curModel;
	    try
	    {
		curModel = readSingleSchemaFile(reader, f);
		model.mergeWith(curModel);
	    } catch (IllegalArgumentException ex)
	    {
		throw new CommandException("Could not merge with schema from file " + f + ": "
			+ ex.getLocalizedMessage(), ex);
	    } catch (Exception e)
	    {
		e.printStackTrace();
	    }

	}
	return model;
    }

    /**
     * Reads a single schema file.
     * 
     * @param reader
     *            The schema reader
     * 
     * @param schemaFile
     *            The schema file
     * 
     * @return The model
     * 
     * @throws Exception
     */
    private Database readSingleSchemaFile(DatabaseIO reader, File schemaFile) throws Exception
    {
	Database model = null;

	if (!schemaFile.isFile())
	{
	    throw new Exception("Path " + schemaFile.getAbsolutePath() + " does not denote a file");
	} else if (!schemaFile.canRead())
	{
	    throw new Exception("Could not read schema file " + schemaFile.getAbsolutePath());
	} else
	{
	    try
	    {
		model = reader.read(schemaFile);
	    } catch (Exception ex)
	    {
		throw new CommandException("Could not read schema file " + schemaFile.getAbsolutePath() + ": "
			+ ex.getLocalizedMessage(), ex);
	    }
	}
	return model;
    }

}

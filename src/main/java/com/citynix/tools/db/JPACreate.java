package com.citynix.tools.db;

import java.net.MalformedURLException;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/***
 * 
 * Reads META-INF/persistence.xml, and create the tables in the DB.
 * 
 * @author <a href="mailto:mansour.alakeel@gmail.com">Mansour Al Akeel</a>
 * 
 */
@Mojo(name = "jpa-create-tables", requiresProject = true, requiresDependencyResolution = ResolutionScope.TEST)
public class JPACreate extends CommonConfigurationMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
	try
	{
	    super.setupClassPath();
	} catch (MalformedURLException e)
	{
	    e.printStackTrace();
	} catch (DependencyResolutionRequiredException e)
	{
	    e.printStackTrace();
	}

	TablesCreator creator;

//	 creator = new OpenJPATablesCreator();

	creator = new OpenEjbTablesCreator();

	creator.setDataBaseCredentials(super.driver, super.url, super.username, super.password);

	super.getLog().info("Executing create tables ");

	creator.create();

	getLog().info("Completed create tables ");
	System.out.println(this.mavenProject);
    }

}

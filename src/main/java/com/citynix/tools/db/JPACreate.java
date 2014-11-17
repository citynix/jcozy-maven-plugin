package com.citynix.tools.db;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;

import com.citynix.tools.db.internal.OpenEJBProcessRunner;

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
	    OpenEJBProcessRunner runner = new OpenEJBProcessRunner();

	    URL[] urls = super.setupClassLoader();

	    runner.setUrls(urls);

	    String javaAgent = super.getOpenJPA_javaagent_path();

	    runner.setJavaAgentPath(javaAgent);

	    getLog().info("Executing create tables ");

	    runner.exec(driver, url, username, password);

	    getLog().info("Completed create tables ");

	} catch (MalformedURLException e)
	{
	    e.printStackTrace();
	} catch (DuplicateRealmException e)
	{
	    e.printStackTrace();
	} catch (DependencyResolutionRequiredException e)
	{
	    e.printStackTrace();
	} catch (UnsupportedEncodingException e)
	{
	    e.printStackTrace();
	} catch (IOException e)
	{
	    e.printStackTrace();
	} catch (InterruptedException e)
	{
	    e.printStackTrace();
	}

    }

}

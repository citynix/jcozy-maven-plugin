package com.citynix.tools.db;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

abstract class CommonConfigurationMojo extends AbstractMojo {

    @Parameter(readonly = true, required = true)
    protected String driver;

    @Parameter(readonly = true, required = true)
    protected String url;

    @Parameter(readonly = true, required = true)
    protected String username;

    @Parameter(readonly = true, required = true)
    protected String password;

    @Parameter(readonly = true, required = false)
    protected String format = "flat";

    @Parameter(readonly = true, required = false)
    protected boolean datatypeWarning = false;

    @Parameter(readonly = true, required = false, alias = "data-files")
    protected List<File> dataFiles;

    @Parameter(readonly = true, required = false, property = "schema-files")
    private List<File> schemaFiles = new LinkedList<File>();

    @Component
    protected MavenProject mavenProject;

    protected final List<File> fileNamesToList(List<String> fileNames)
    {
	List<File> tmp = new LinkedList<File>();
	for (String fn : fileNames)
	{
	    tmp.add(new File(fn));
	}
	return tmp;
    }

    public void setSchemaFiles(List<String> schemaFiles)
    {
	this.schemaFiles = this.fileNamesToList(schemaFiles);
    }

    public void setSrc(String src)
    {
	String msg = "Can not set <src> on this Mojo. Please use <data-files> instead !";
	// throw new IllegalStateException(msg);
	getLog().warn(msg);
    }

    protected List<File> getSchemaFiles()
    {
	return this.schemaFiles;
    }

    private ClassLoader classLoader;

    protected void setupClassPath() throws MalformedURLException, DependencyResolutionRequiredException
    {

	// synchronized (this)
	// {
	// if (classLoader != null)
	// return;
	// }
	// synchronized (this)
	// {
	List<URL> urls = new ArrayList<URL>();

	List<String> paths = new LinkedList<String>();

	List<String> compilePath = mavenProject.getCompileClasspathElements();

	List<String> testPath = mavenProject.getTestClasspathElements();

	List<String> runtimePath = mavenProject.getRuntimeClasspathElements();

	List<String> systemPath = mavenProject.getSystemClasspathElements();

	List<Dependency> dependencies = mavenProject.getCompileDependencies();

	this.merge(paths, compilePath);
	this.merge(paths, testPath);
	this.merge(paths, runtimePath);
	this.merge(paths, systemPath);

	getLog().info("Scanning ... ");

	for (Object object : paths)
	{
	    String path = (String) object;

	    getLog().info("Adding " + path + " to classpath");

	    urls.add(new File(path).toURL());
	}

	ClassLoader parent = Thread.currentThread().getContextClassLoader();

	parent = this.getClass().getClassLoader();

	URL[] a = urls.toArray(new URL[urls.size()]);
	ClassLoader contextClassLoader = URLClassLoader.newInstance(a, parent);

	Thread.currentThread().setContextClassLoader(contextClassLoader);

	return;
	// }
    }

    private void merge(List<String> original, List<String> additional)
    {
	for (String s : additional)
	{
	    if (!original.contains(s))
		original.add(s);
	}
    }

}

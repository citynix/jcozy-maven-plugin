package com.citynix.tools.db;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;

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

    @Parameter(property = "project")
    protected MavenProject mavenProject;

    @Parameter(property = "descriptor")
    private PluginDescriptor descriptor;

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

    // private ClassLoader classLoader;

    protected URL[] setupClassLoader() throws MalformedURLException, DependencyResolutionRequiredException, DuplicateRealmException,
	    UnsupportedEncodingException, MojoFailureException
    {

	getLog().debug("Scanning classpath ... ");

	String packaging = this.mavenProject.getPackaging();

	if (!packaging.equals("bundle"))
	{
	    throw new MojoFailureException("Packaging must be bundle");
	}

	URL urls[] = this.getClassPathURLs();
	List<URL> allURLs = new ArrayList<URL>();

	for (URL url : urls)
	{
	    getLog().debug("Adding " + url + " to project classpath");
	    allURLs.add(url);
	}

	// Adding plugin URL to both class path
	URL pluginURL = this.currentJarURL();
	allURLs.add(pluginURL);

	URL[] array = allURLs.toArray(new URL[allURLs.size()]);

	// ClassLoader cl = new URLClassLoader(array,
	// Thread.currentThread().getContextClassLoader());
	//
	// Thread.currentThread().setContextClassLoader(cl);

	return array;

    }

    private URL currentJarURL()
    {
	String plugingJarPath = CommonConfigurationMojo.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	String decodedPath;
	try
	{
	    decodedPath = URLDecoder.decode(plugingJarPath, "UTF-8");
	    URL pluginURL = (new File(decodedPath)).toURI().toURL();
	    return pluginURL;

	} catch (UnsupportedEncodingException e)
	{
	    e.printStackTrace();
	} catch (MalformedURLException e)
	{
	    e.printStackTrace();
	}
	return null;
    }

    private URL[] getClassPathURLs() throws DependencyResolutionRequiredException, MalformedURLException, UnsupportedEncodingException
    {
	List<URL> paths = new ArrayList<URL>();

	List<String> compilePath = mavenProject.getCompileClasspathElements();

	List<String> runtimePath = mavenProject.getRuntimeClasspathElements();

	List<String> systemPath = mavenProject.getSystemClasspathElements();

	this.merge(paths, compilePath);
	this.merge(paths, runtimePath);
	this.merge(paths, systemPath);

	URL[] a = new URL[paths.size()];

	return paths.toArray(a);
    }

    private void merge(List<URL> original, List<String> additional)
    {
	for (String s : additional)
	{
	    try
	    {
		File f = new File(s);
		URL tmp = f.toURI().toURL();

		// if (!s.contains("openejb"))
		if (!original.contains(tmp))
		    original.add(tmp);

	    } catch (MalformedURLException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    protected final String getOpenJPA_javaagent_path()
    {
	String openjpaId = "org.apache.openejb.patch";
	String openjpaGroupId = "openjpa";

	List<Artifact> artifacts = new LinkedList<Artifact>();

	artifacts.addAll(this.mavenProject.getRuntimeArtifacts());
	artifacts.addAll(this.mavenProject.getCompileArtifacts());

	for (Artifact artifact : artifacts)
	{
	    String aId = artifact.getArtifactId();
	    String gId = artifact.getGroupId();

	    if (openjpaGroupId.equals(aId) && openjpaId.equals(gId))
		return artifact.getFile().getAbsolutePath();
	}

	return null;
    }
}

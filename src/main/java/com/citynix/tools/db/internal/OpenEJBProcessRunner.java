package com.citynix.tools.db.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class OpenEJBProcessRunner {

    private URL[] urls;

    private String javaAgentPath;

    public int exec(String driver, String url, String username, String password) throws IOException, InterruptedException
    {
	if (this.javaAgentPath.equals(""))
	    throw new NullPointerException("javaagent path must be set");

	String javaHome = System.getProperty("java.home");

	String javaBin = javaHome + File.separator + "bin" + File.separator + "java";

	StringBuilder classpathBuilder = new StringBuilder();

	for (URL u : this.urls)
	{
	    classpathBuilder.append(u.getPath().trim());
	    classpathBuilder.append(File.pathSeparator);
	}

	classpathBuilder.append(".");

	String className = OpenEJBProcess.class.getCanonicalName();

	String classpath = classpathBuilder.toString();
	StringBuilder commandBuilder = new StringBuilder();

	commandBuilder.append(javaBin);

	commandBuilder.append(" -Djdbc.driver=" + driver);
	commandBuilder.append(" -Djdbc.url=" + url);
	commandBuilder.append(" -Djdbc.username=" + username);
	commandBuilder.append(" -Djdbc.password=" + password + "");

	commandBuilder.append(" -cp ");
	commandBuilder.append(classpath);
	commandBuilder.append(" ");

	// commandBuilder.append(" -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005 ");

	commandBuilder.append(" -javaagent:" + this.javaAgentPath.trim() + " ");

	commandBuilder.append(className);

	String command = commandBuilder.toString();

	Process process = Runtime.getRuntime().exec(command);

	InputStream is = process.getInputStream();
	InputStreamReader isr = new InputStreamReader(is);
	BufferedReader br = new BufferedReader(isr);
	String line;

	while ((line = br.readLine()) != null)
	{
	    System.out.println(line);
	}

	process.waitFor();

	int exitStatus = process.exitValue();

	return exitStatus;

    }

    public void setUrls(URL[] urls)
    {
	this.urls = urls;
    }

    public void setJavaAgentPath(String javaAgentPath)
    {
	this.javaAgentPath = javaAgentPath;
    }

}

package com.citynix.tools.db;

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

public class JPACreateTest extends AbstractMojoTestCase {

    private JPACreate testMojo;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
    {
	Mojo mojo;
	try
	{
	    super.setUp();

	    File testPom = new File(super.getBasedir(), "src/test/resources/unit/pom/pom-1.xml");

	    mojo = super.lookupMojo("jpa-create-tables", testPom);

	    this.testMojo = (JPACreate) mojo;

	} catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    @Test
    public void testCreate() throws Exception
    {
	assertNotNull(testMojo);
    }

    public void testExecute()
    {
	try
	{
	    this.testMojo.execute();
	} catch (MojoExecutionException e)
	{
	    e.printStackTrace();
	} catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

}

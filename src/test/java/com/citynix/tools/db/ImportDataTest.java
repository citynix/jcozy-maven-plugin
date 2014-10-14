package com.citynix.tools.db;

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ImportDataTest extends AbstractMojoTestCase {

    private ImportData testMojo;

    // @Rule
    // public MojoRule rule = new MojoRule()
    // {
    // @Override
    // protected void before() throws Throwable
    // {
    // }
    //
    // @Override
    // protected void after()
    // {
    // }
    // };

    /**
     * @see junit.framework.TestCase#setUp()
     */
    // @Before
    protected void setUp()
    {
	Mojo mojo;
	try
	{
	    super.setUp();

	    File testPom = new File(super.getBasedir(), "src/test/resources/unit/pom/pom-1.xml");

	    mojo = super.lookupMojo("import-data", testPom);

	    this.testMojo = (ImportData) mojo;

	} catch (Exception e)
	{
	    e.printStackTrace();
	}

    }

    // @After
    protected void tearDown() throws Exception
    {
	super.tearDown();
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

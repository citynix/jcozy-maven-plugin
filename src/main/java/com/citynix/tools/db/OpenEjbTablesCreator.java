package com.citynix.tools.db;

import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.citynix.tools.db.internal.EJBTestLocal;

class OpenEjbTablesCreator implements TablesCreator {

    private String driver;
    private String url;
    private String username;
    private String password;

    public void setDataBaseCredentials(String driver, String url, String username, String password)
    {
	this.driver = driver;
	this.url = url;
	this.username = username;
	this.password = password;
    }

    @Override
    public void create()
    {
	Properties properties = new Properties();

	properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");

	// properties.put(OpenEjbContainer.OPENEJB_EMBEDDED_REMOTABLE, "true");
	properties.put("openejb.validation.output.level", "VERBOSE");

	properties.put("jdbc/dataSource", "new://Resource?type=DataSource");
	properties.put("jdbc/dataSource.JdbcDriver", this.driver);
	properties.put("jdbc/dataSource.JdbcUrl", this.url);
	properties.put("jdbc/dataSource.UserName", this.username);
	properties.put("jdbc/dataSource.Password", this.password);

	properties.setProperty("javax.persistence.transactionType", "RESOURCE_LOCAL");

	Context ctx;
	try
	{
	     org.apache.openejb.core.LocalInitialContextFactory f;
	    ctx = new InitialContext(properties);

	    EJBTestLocal ejbTestLocal = (EJBTestLocal) ctx.lookup("EJBTestBeanLocal");

	    EntityManager entityManager = null;

	    EntityManagerFactory entityManagerFactory;

	    entityManagerFactory = ejbTestLocal.getEntityManagerFactory();

	    entityManager = entityManagerFactory.createEntityManager();
	    entityManager.close();

	} catch (NamingException e)
	{
	    e.printStackTrace();
	}
    }

}

package com.citynix.tools.db;

import java.util.Properties;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.citynix.tools.db.internal.EJBTestBean;

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
	    ctx = new InitialContext(properties);

	    JNDITree printer = new JNDITree(ctx);

	    System.out.println("Printing tree");

	    printer.printJNDITree("");

	    NamingEnumeration<Binding> list = ctx.listBindings("java:openejb/");

	    while (list.hasMore())
	    {
		Binding item = list.next();
		System.out.println(item.getClassName() + " :: " + "java:openejb/" + item.getName());
	    }

	    Object obj = ctx.lookup("EJBTestLocalLocal");

	    System.out.println("Found object type: " + obj.getClass());

	    EJBTestBean ejbTestLocal = (EJBTestBean) obj;

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

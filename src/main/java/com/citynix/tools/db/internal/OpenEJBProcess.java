package com.citynix.tools.db.internal;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;

public class OpenEJBProcess {

    public static void main(String[] args) throws ClassNotFoundException
    {

	Properties properties = new Properties();

	properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");

	properties.put("openejb.validation.output.level", "VERBOSE");

	String driver = System.getProperty("jdbc.driver");
	String url = System.getProperty("jdbc.url");
	String username = System.getProperty("jdbc.username");
	String password = System.getProperty("jdbc.password");

	properties.put("jdbc/dataSource", "new://Resource?type=DataSource");
	properties.put("jdbc/dataSource.JdbcDriver", driver.trim());
	properties.put("jdbc/dataSource.JdbcUrl", url);
	properties.put("jdbc/dataSource.UserName", username);
	properties.put("jdbc/dataSource.Password", password);

	// properties.setProperty("javax.persistence.transactionType",
	// "RESOURCE_LOCAL");

	properties.put("openjpa.RuntimeUnenhancedClasses", "supported");

	properties.put("openjpa.Log", "File=org.apache.openjpa.log, DefaultLevel=TRACE, Tool=TRACE, Runtime=TRACE, SQL=TRACE");
	properties.put("openjpa.ConnectionFactoryProperties", "PrintParameters=true, PrettyPrint=true, PrettyPrintLineLength=80");
	// properties.put("openjpa.TransactionMode", "local");

	Context ctx;

	try
	{
	    ctx = new InitialContext(properties);

	    EJBTestBean ejbTestLocal;

	    EntityManager entityManager = null;

	    Object obj;

	    obj = ctx.lookup("PersistenceAccessor_ManagedLocal");

	    ejbTestLocal = (EJBTestBean) obj;

	    entityManager = ejbTestLocal.getEntityManager();

	    if (entityManager != null)
	    {
		// System.out.println("Found managed: " + obj.getClass());

		Object o2 = ctx.lookup("java:openejb/TransactionManager");

		TransactionManager txMgr = (TransactionManager) o2;

		txMgr.begin();
		entityManager.flush();
		txMgr.commit();

	    } else
	    {
		obj = ctx.lookup("PersistenceAccessor_UnManagedLocal");

		ejbTestLocal = (EJBTestBean) obj;

		EntityManagerFactory entityManagerFactory = ejbTestLocal.getEntityManagerFactory();

		// System.out.println("Found UnManaged : " + obj.getClass());

		entityManager = entityManagerFactory.createEntityManager();

		entityManager.close();
	    }

	} catch (Exception e)
	{
	    e.printStackTrace(System.out);
	}
    }

}

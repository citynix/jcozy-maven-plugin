package com.citynix.tools.db.internal;

import java.util.Properties;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.apache.openejb.assembler.classic.ReloadableEntityManagerFactory;

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
	properties.put("jdbc/dataSource.JdbcUrl", url.trim());
	properties.put("jdbc/dataSource.UserName", username.trim());
	properties.put("jdbc/dataSource.Password", password.trim());

	properties.setProperty("javax.persistence.transactionType", "RESOURCE_LOCAL");

	properties.put("openjpa.RuntimeUnenhancedClasses", "supported");

	properties.put("openjpa.Log", "File=org.apache.openjpa.log, DefaultLevel=TRACE, Tool=TRACE, Runtime=TRACE, SQL=TRACE");
	properties.put("openjpa.ConnectionFactoryProperties", "PrintParameters=true, PrettyPrint=true, PrettyPrintLineLength=80");
	// properties.put("openjpa.TransactionMode", "local");

	Context ctx;

	try
	{

	    ctx = new InitialContext(properties);

	    // printContext(ctx, "java:openejb");
	    // printContext(ctx, "java:openejb/PersistenceUnit");

	    Context puc = (Context) ctx.lookup("java:openejb/PersistenceUnit");

	    ReloadableEntityManagerFactory factory = null;

	    NamingEnumeration<Binding> tmp = puc.listBindings("");

	    factory = (ReloadableEntityManagerFactory) tmp.next().getObject();

	    EntityManager entityManager = factory.createEntityManager();

	    EntityManager delegate = (EntityManager) entityManager.getDelegate();

	    entityManager = delegate;

	    PersistenceUnitTransactionType txType = factory.info().getTransactionType();

	    String puName = factory.info().getPersistenceUnitName();

	    System.out.println("Found " + txType + " for persistence unit " + puName);

	    entityManager.getTransaction().begin();
	    entityManager.flush();
	    entityManager.getTransaction().commit();
	    entityManager.close();

	} catch (Exception e)
	{
	    e.printStackTrace(System.out);
	}
    }

    private static void printContext(Context ctx, String name) throws NamingException
    {
	System.out.println("Printing context " + name);
	NamingEnumeration<NameClassPair> list = ctx.list(name);
	while (list.hasMore())
	{
	    NameClassPair pair = list.next();
	    System.out.println(pair.getClassName() + "-->" + pair.getName());
	}

    }

}

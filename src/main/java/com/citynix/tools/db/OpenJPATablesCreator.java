package com.citynix.tools.db;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceUnitTransactionType;

class OpenJPATablesCreator implements TablesCreator {

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
	Properties props = new Properties();

	props.setProperty("openjpa.Log", "DefaultLevel=TRACE, Tool=TRACE, Runtime=TRACE, SQL=TRACE");
	props.setProperty("openjpa.ConnectionFactoryProperties", "PrintParameters=true, PrettyPrint=true, PrettyPrintLineLength=80");
	props.put("openjpa.RuntimeUnenhancedClasses", " supported");

	props.setProperty("javax.persistence.jdbc.driver", this.driver);
	props.setProperty("javax.persistence.jdbc.url", this.url);
	props.setProperty("javax.persistence.jdbc.user", this.username);
	props.setProperty("javax.persistence.jdbc.password", this.password);

	props.setProperty("javax.persistence.transactionType", PersistenceUnitTransactionType.RESOURCE_LOCAL.name());

	EntityManagerFactory emf = Persistence.createEntityManagerFactory(null, props);

	EntityManager em = emf.createEntityManager(props);

	boolean open = em.isOpen();

	em.close();
	emf.close();
    }
}

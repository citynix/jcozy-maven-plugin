package com.citynix.tools.db.internal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

public interface EJBTestLocal {
    EntityManager getEntityManager();
 
    EntityManagerFactory getEntityManagerFactory();

}
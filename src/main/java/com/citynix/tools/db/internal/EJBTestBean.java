package com.citynix.tools.db.internal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public interface EJBTestBean {

    EntityManager getEntityManager();
 
    EntityManagerFactory getEntityManagerFactory();

}
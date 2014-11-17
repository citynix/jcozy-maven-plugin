package com.citynix.tools.db.internal;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

/* 
 * This class is used internally to get a reference of EntityManagerFactory and EntityManager
 *  
 */
@Stateless
public class PersistenceAccessor_Managed implements EJBTestBean {

    @PersistenceContext
    private EntityManager entityManager;

    // java:openejb/TransactionManager

    // @PersistenceUnit
    // private EntityManagerFactory entityManagerFactory;

    public EntityManager getEntityManager()
    {
	return entityManager;
    }

    public EntityManagerFactory getEntityManagerFactory()
    {
	return null;
    }

}

package com.citynix.tools.db.internal;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/* 
 * This class is used internally to get a reference of EntityManagerFactory and EntityManager
 *  
 */
@Stateless
public class EJBTestBean implements EJBTestLocal {

    @PersistenceContext
    private EntityManager entityManager;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    public EntityManager getEntityManager()
    {
	return entityManager;
    }

    public EntityManagerFactory getEntityManagerFactory()
    {
	return entityManagerFactory;
    }

}

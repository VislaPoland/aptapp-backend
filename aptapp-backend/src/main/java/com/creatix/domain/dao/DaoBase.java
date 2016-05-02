package com.creatix.domain.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Base class for all repository classes.
 */
@Repository
@Transactional
class DaoBase<T, ID> {

    @PersistenceContext
    EntityManager em;

    public void persist(T entity) {
        em.persist(entity);
    }

    public void delete(T entity) {
        em.remove(entity);
    }
}

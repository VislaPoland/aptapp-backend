package com.creatix.domain.dao;

import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * Base class for all repository classes.
 */
@Repository
@Transactional
class DaoBase<T, ID> {
    @PersistenceContext
    EntityManager em;

    Class<T> type;

    DaoBase() {
        this.type = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), DaoBase.class);
    }

    public T findById(ID id) {
        return em.find(type, id);
    }

    public void persist(T entity) {
        em.persist(entity);
    }

    public void delete(T entity) {
        em.remove(entity);
    }
}

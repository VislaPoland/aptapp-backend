package com.creatix.domain.dao;

import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;
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
@Component
@Transactional
abstract class DaoBase<T, ID> {
    @PersistenceContext
    EntityManager em;

    Class<T> type;

    @SuppressWarnings("unchecked")
    DaoBase() {
        final Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(getClass(), DaoBase.class);
        this.type = (Class<T>) classes[0];
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

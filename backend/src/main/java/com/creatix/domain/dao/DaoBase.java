package com.creatix.domain.dao;

import com.querydsl.jpa.JPQLQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Base class for all repository classes.
 */
@Component
@Transactional
abstract class DaoBase<T, ID> {

    @PersistenceContext
    EntityManager em;

    @Autowired
    JPQLQueryFactory queryFactory;

    private Class<T> type;

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

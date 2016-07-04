package com.creatix.domain.dao;

import com.creatix.domain.entity.Property;
import com.creatix.domain.entity.account.AssistantPropertyManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.creatix.domain.entity.account.QAssistantPropertyManager.assistantPropertyManager;

@Repository
@Transactional
public class AssistantPropertyManagerDao extends DaoBase<AssistantPropertyManager, Long> {

    public List<AssistantPropertyManager> findByProperty(Property property) {
        return queryFactory
                .selectFrom(assistantPropertyManager)
                .where(assistantPropertyManager.deletedAt.isNull()
                        .and(assistantPropertyManager.manager.managedProperty.eq(property)))
                .fetch();
    }

}

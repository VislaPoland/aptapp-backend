package com.creatix.domain.dao;

import com.creatix.domain.entity.store.PredefinedMessage;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PredefinedMessageDao extends DaoBase<PredefinedMessage, Long> {
}

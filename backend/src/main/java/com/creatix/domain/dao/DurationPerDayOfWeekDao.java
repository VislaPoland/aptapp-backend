package com.creatix.domain.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.creatix.domain.entity.store.DurationPerDayOfWeek;


/**
 * @author <a href="mailto:martin@thinkcreatix.com.com">martin dupal</a>
 */
@Repository
@Transactional
public class DurationPerDayOfWeekDao extends DaoBase<DurationPerDayOfWeek, Long> {
}

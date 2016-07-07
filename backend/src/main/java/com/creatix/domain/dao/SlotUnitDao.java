package com.creatix.domain.dao;

import com.creatix.domain.entity.store.SlotUnit;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.creatix.domain.entity.store.QSlotUnit.slotUnit;

@Repository
@Transactional
public class SlotUnitDao extends DaoBase<SlotUnit, Long> {

    public List<SlotUnit> findAll() {
        return queryFactory.selectFrom(slotUnit).fetch();
    }

}

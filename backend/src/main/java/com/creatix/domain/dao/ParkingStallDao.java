package com.creatix.domain.dao;

import com.creatix.domain.entity.ParkingStall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ParkingStallDao extends DaoBase<ParkingStall, Long> {
}

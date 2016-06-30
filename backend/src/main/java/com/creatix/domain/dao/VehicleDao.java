package com.creatix.domain.dao;

import com.creatix.domain.entity.Vehicle;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class VehicleDao extends DaoBase<Vehicle, Long> {
}

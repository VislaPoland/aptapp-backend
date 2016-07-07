package com.creatix.domain.dao;

import com.creatix.domain.entity.store.Address;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AddressDao extends DaoBase<Address, Long> {
}

package com.creatix.domain.dao.business;

import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.entity.store.business.DiscountCoupon;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Repository
@Transactional
public class DiscountCouponDao extends DaoBase<DiscountCoupon, Long> {

}

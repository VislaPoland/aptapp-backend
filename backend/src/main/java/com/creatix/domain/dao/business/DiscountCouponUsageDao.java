package com.creatix.domain.dao.business;

import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.entity.store.business.DiscountCouponUsage;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@Repository
@Transactional
public class DiscountCouponUsageDao extends DaoBase<DiscountCouponUsage, DiscountCouponUsage.IdKey> {


}

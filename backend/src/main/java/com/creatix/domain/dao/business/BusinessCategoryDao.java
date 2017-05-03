package com.creatix.domain.dao.business;

import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.entity.store.business.BusinessCategory;
import com.creatix.domain.entity.store.business.QBusinessCategory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Tomas Michalek on 13/04/2017.
 */
@Repository
@Transactional
public class BusinessCategoryDao extends DaoBase<BusinessCategory, Long> {


    public List<BusinessCategory> listAllCategories() {
        return queryFactory.selectFrom(QBusinessCategory.businessCategory).fetch();
    }


}

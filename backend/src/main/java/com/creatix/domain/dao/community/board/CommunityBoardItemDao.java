package com.creatix.domain.dao.community.board;

import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.dto.community.board.SearchRequest;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import com.creatix.domain.entity.store.community.board.CommunityBoardItem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

import static com.creatix.domain.entity.store.community.board.QCommunityBoardItem.communityBoardItem;

/**
 * Created by kvimbi on 10/05/2017.
 */
@Repository
@Transactional
public class CommunityBoardItemDao extends DaoBase<CommunityBoardItem, Long> {


    public List<CommunityBoardItem> listByProperty(Property property, long offset, long limit) {
        return queryFactory.selectFrom(communityBoardItem)
                .where(communityBoardItem.property.eq(property))
                .orderBy(communityBoardItem.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    public List<CommunityBoardItem> searchFromRequest(Property property, SearchRequest searchRequest, CommunityBoardCategory category) {

        //Select from
        JPQLQuery<CommunityBoardItem> query = queryFactory.selectFrom(communityBoardItem);

        //where
        BooleanExpression wherePredicate = communityBoardItem.property.eq(property);
        if (null != category) {
            wherePredicate = wherePredicate.and(communityBoardItem.category.eq(category));
        }
        if (null != searchRequest.getTitle()) {
            wherePredicate = wherePredicate.and(communityBoardItem.title.like(searchRequest.getTitle()));
        }
        if (null != searchRequest.getDescription()) {
            wherePredicate = wherePredicate.and(communityBoardItem.description.like(searchRequest.getDescription()));
        }
        query = query.where(wherePredicate);

        // order by
        switch (searchRequest.getOrderBy()) {
            case TITLE:
                query = query.orderBy(
                        searchRequest.getOrderDesc() ? communityBoardItem.title.desc() : communityBoardItem.title.asc()
                );
                break;
            case CREATED:
                query = query.orderBy(
                        searchRequest.getOrderDesc() ? communityBoardItem.createdAt.desc() : communityBoardItem.createdAt.asc()
                );
                break;
            case MODIFIED:
                query = query.orderBy(
                        searchRequest.getOrderDesc() ? communityBoardItem.updatedAt.desc() : communityBoardItem.updatedAt.asc()
                );
                break;
            case PRICE:
                query = query.orderBy(
                        searchRequest.getOrderDesc() ? communityBoardItem.price.desc() : communityBoardItem.price.asc()
                );
                break;
        }

        // limit
        query = query.offset(searchRequest.getOffset())
                .limit(searchRequest.getLimit());

        return query.fetch();
    }
}

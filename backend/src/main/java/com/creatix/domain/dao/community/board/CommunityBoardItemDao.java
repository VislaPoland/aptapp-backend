package com.creatix.domain.dao.community.board;

import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.dto.community.board.SearchRequest;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import com.creatix.domain.entity.store.community.board.CommunityBoardItem;
import com.creatix.domain.enums.community.board.CommunityBoardStatusType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.util.List;

import static com.creatix.domain.entity.store.community.board.QCommunityBoardItem.communityBoardItem;

/**
 * Created by Tomas Michalek on 10/05/2017.
 */
@Repository
@Transactional
public class CommunityBoardItemDao extends DaoBase<CommunityBoardItem, Long> {

    @Nonnull
    public List<CommunityBoardItem> listByPropertyAndStatus(@Nonnull Property property, @Nullable Long ownerId, List<CommunityBoardStatusType> statusTypes, Long startId, long limit) {


        BooleanExpression wherePredicate = communityBoardItem.property.eq(property);
        wherePredicate = wherePredicate.and(communityBoardItem.communityBoardStatus.in(statusTypes));
        if (null != startId) {
            CommunityBoardItem firstItem = findById(startId);
            if (null != firstItem) {
                wherePredicate = wherePredicate.and(communityBoardItem.createdAt.loe(firstItem.getCreatedAt()));
            }
        }
        if (null != ownerId) {
            wherePredicate = wherePredicate.and(communityBoardItem.account.id.eq(ownerId));
        }
        return queryFactory.selectFrom(communityBoardItem)
                .where(wherePredicate)
                .orderBy(communityBoardItem.createdAt.desc())
                .limit(limit)
                .fetch();
    }

    @Nonnull
    public List<CommunityBoardItem> searchFromRequest(@Nonnull Property property, @Nonnull List<CommunityBoardStatusType> statusTypes, long pageSize, @Nonnull SearchRequest searchRequest, @Nullable CommunityBoardCategory category) {

        //Select from
        JPQLQuery<CommunityBoardItem> query = queryFactory.selectFrom(communityBoardItem);

        //where
        BooleanExpression wherePredicate = communityBoardItem.property.eq(property)
                .and(
                        communityBoardItem.communityBoardStatus.eq(CommunityBoardStatusType.OPEN)
                );
        if (null != category) {
            wherePredicate = wherePredicate.and(communityBoardItem.category.eq(category));
        }
        if (null != searchRequest.getTitle()) {
            wherePredicate = wherePredicate.and(communityBoardItem.title.like(searchRequest.getTitle()));
        }
        if (null != searchRequest.getDescription()) {
            wherePredicate = wherePredicate.and(communityBoardItem.description.like(searchRequest.getDescription()));
        }
        if (null != searchRequest.getStartId()) {
            CommunityBoardItem firstItem = findById(searchRequest.getStartId());
            if (null != firstItem) {
                wherePredicate = wherePredicate.and(communityBoardItem.createdAt.loe(firstItem.getCreatedAt()));
            }
        }
        wherePredicate = wherePredicate.and(communityBoardItem.communityBoardStatus.in(statusTypes));
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
        query = query.limit(pageSize);

        return query.fetch();
    }

    @Nonnull
    public List<CommunityBoardItem> listByPropertyAnCategories(@Nonnull Property property, @Nullable Long ownerId, @Nonnull List<CommunityBoardStatusType> statusTypes, @Nonnull List<CommunityBoardCategory> categoryList, @Nullable Long startId, long pageSize) {
        BooleanExpression wherePredicate = communityBoardItem.property.eq(property);
        wherePredicate = wherePredicate.and(communityBoardItem.category.in(categoryList));
        wherePredicate = wherePredicate.and(communityBoardItem.communityBoardStatus.in(statusTypes));
        if (null != startId) {
            CommunityBoardItem firstItem = findById(startId);
            if (null != firstItem) {
                wherePredicate = wherePredicate.and(communityBoardItem.createdAt.loe(firstItem.getCreatedAt()));
            }
        }
        if (null != ownerId) {
            wherePredicate = wherePredicate.and(communityBoardItem.account.id.eq(ownerId));
        }
        return queryFactory.selectFrom(communityBoardItem)
                .where(wherePredicate)
                .orderBy(communityBoardItem.createdAt.desc())
                .limit(pageSize)
                .fetch();
    }

}

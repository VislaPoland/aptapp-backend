package com.creatix.domain.dao.community.board;

import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import com.creatix.domain.entity.store.community.board.QCommunityBoardCategory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Tomas Michalek on 11/05/2017.
 */
@Repository
@Transactional
public class CommunityBoardCategoryDao extends DaoBase<CommunityBoardCategory, Long> {
    public List<CommunityBoardCategory> listAll() {
        return queryFactory.selectFrom(QCommunityBoardCategory.communityBoardCategory).fetch();
    }
}

package com.creatix.domain.dao.community.board;

import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by kvimbi on 11/05/2017.
 */
@Repository
@Transactional
public class CommunityBoardCategoryDao extends DaoBase<CommunityBoardCategory, Long> {
}

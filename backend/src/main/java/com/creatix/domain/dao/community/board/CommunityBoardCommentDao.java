package com.creatix.domain.dao.community.board;

import com.creatix.domain.dao.DaoBase;
import com.creatix.domain.entity.store.community.board.CommunityBoardComment;
import com.creatix.domain.entity.store.community.board.CommunityBoardItem;
import static com.creatix.domain.entity.store.community.board.QCommunityBoardComment.communityBoardComment;

import com.creatix.domain.enums.community.board.CommunityBoardCommentStatusType;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Tomas Michalek on 12/05/2017.
 */
@Repository
@Transactional
public class CommunityBoardCommentDao extends DaoBase<CommunityBoardComment, Long> {

    public List<CommunityBoardComment> listParentComments(CommunityBoardItem communityBoardItem) {
        return queryFactory.selectFrom(communityBoardComment).where(
                communityBoardComment.communityBoardItem.eq(communityBoardItem).and(
                        communityBoardComment.parentComment.isNull().and(
                                communityBoardComment.status.ne(CommunityBoardCommentStatusType.DELETED)
                        )
                )
        ).fetch();
    }
}

package com.creatix.domain.dao;

import static com.creatix.domain.entity.store.QPredefinedMessagePhoto.predefinedMessagePhoto;

import com.creatix.domain.entity.store.PredefinedMessagePhoto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * DAO for PredefinedMessagePhoto.
 * @author <a href="mailto:martin@thinkcreatix.com">martin dupal</a>
 */
@Repository
@Transactional
public class PredefinedMessagePhotoDao extends DaoBase<PredefinedMessagePhoto, Long> {

    public PredefinedMessagePhoto findByPredefinedMessageIdAndFileName(Long predefinedMessageId, String fileName) {
        return queryFactory.selectFrom(predefinedMessagePhoto)
                .where(predefinedMessagePhoto.predefinedMessage.id.eq(predefinedMessageId).and(predefinedMessagePhoto.fileName.eq(fileName)))
                .fetchOne();
    }
}

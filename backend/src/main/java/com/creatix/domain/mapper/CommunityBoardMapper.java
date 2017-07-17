package com.creatix.domain.mapper;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.dto.community.board.*;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import com.creatix.domain.entity.store.community.board.CommunityBoardComment;
import com.creatix.domain.entity.store.community.board.CommunityBoardItem;
import com.creatix.domain.entity.store.community.board.CommunityBoardItemPhoto;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.util.Objects;

/**
 * Created by Tomas Michalek on 10/05/2017.
 */
@Component
public class CommunityBoardMapper extends ConfigurableMapper {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    protected void configure(MapperFactory factory) {
        super.configure(factory);

        factory.classMap(CommunityBoardItem.class, CommunityBoardItemDto.class)
                .byDefault()
                .register();

        factory.classMap(CommunityBoardItemPhoto.class, CommunityBoardItemPhotoDto.class)
                .byDefault()
                .customize(new CustomMapper<CommunityBoardItemPhoto, CommunityBoardItemPhotoDto>() {
                    @Override
                    public void mapAtoB(CommunityBoardItemPhoto communityBoardItemPhoto, CommunityBoardItemPhotoDto communityBoardItemPhotoDto, MappingContext context) {
                        try {
                            communityBoardItemPhotoDto.setFileUrl( applicationProperties.buildBackendUrl(
                                    String.format(
                                            "api/attachments/%d/%s",
                                            communityBoardItemPhoto.getId(),
                                            communityBoardItemPhoto.getFileName()
                                    )
                            ).toString());
                        } catch (MalformedURLException e) {
                            throw new IllegalStateException("Failed to create download URL", e);
                        }
                    }
                })
                .register();

        factory.classMap(CommunityBoardCategory.class, CommunityBoardCategoryDto.class)
                .byDefault()
                .register();

        factory.classMap(Account.class, CommunityBoardItemAuthorDto.class)
                .byDefault()
                .register();
    }

    public CommunityBoardItemDto toCommunityBoardItem(@NotNull CommunityBoardItem communityBoardItem) {
        Objects.requireNonNull(communityBoardItem, "Community board item must not be null");
        return this.map(communityBoardItem, CommunityBoardItemDto.class);
    }

    public CommunityBoardItem toCommunityBoardItem(@NotNull CommunityBoardItemDto communityBoardItemDto) {
        Objects.requireNonNull(communityBoardItemDto, "Community board item must not be null");
        return this.map(communityBoardItemDto, CommunityBoardItem.class);
    }

    public CommunityBoardItemPhotoDto toCommunityBoardItemPhoto(@NotNull CommunityBoardItemPhoto communityBoardItemPhoto) {
        Objects.requireNonNull(communityBoardItemPhoto, "Board photo must not be null");
        return this.map(communityBoardItemPhoto, CommunityBoardItemPhotoDto.class);
    }

    public CommunityBoardCategoryDto toCommunityBoardCategoryDto(@NotNull CommunityBoardCategory communityBoardCategory) {
        Objects.requireNonNull(communityBoardCategory, "Board item category must not be null");
        return this.map(communityBoardCategory, CommunityBoardCategoryDto.class);
    }

    public CommunityBoardCommentDto toCommunityBoardComment(@NotNull CommunityBoardComment communityBoardComment) {
        Objects.requireNonNull(communityBoardComment, "Comment object can not be null");
        return this.map(communityBoardComment, CommunityBoardCommentDto.class);
    }

    public CommunityBoardComment toCommunityBoardComment(@NotNull CommunityBoardCommentDto communityBoardCommentDto) {
        Objects.requireNonNull(communityBoardCommentDto, "Comment object can not be null");
        return this.map(communityBoardCommentDto, CommunityBoardComment.class);
    }

}

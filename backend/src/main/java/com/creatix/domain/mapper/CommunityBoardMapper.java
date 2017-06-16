package com.creatix.domain.mapper;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.dao.community.board.CommunityBoardCategoryDao;
import com.creatix.domain.dto.community.board.*;
import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.SubTenant;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.community.board.CommunityBoardCategory;
import com.creatix.domain.entity.store.community.board.CommunityBoardComment;
import com.creatix.domain.entity.store.community.board.CommunityBoardItem;
import com.creatix.domain.entity.store.community.board.CommunityBoardItemPhoto;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Created by Tomas Michalek on 10/05/2017.
 */
@Component
public class CommunityBoardMapper extends ConfigurableMapper {

    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private CommunityBoardCategoryDao communityBoardCategoryDao;

    @Override
    protected void configure(MapperFactory factory) {
        super.configure(factory);

        factory.getConverterFactory().registerConverter(new PassThroughConverter(OffsetDateTime.class, OffsetDateTime.class));

        factory.classMap(CommunityBoardItem.class, CommunityBoardItemDto.class)
                .exclude("category")
                .byDefault()
                .field("showEmailAddress", "privacySettings.showEmailAddress")
                .field("showPhoneNumber", "privacySettings.showPhoneNumber")
                .field("showApartmentNumber", "privacySettings.showApartmentNumber")
                .fieldAToB("category", "category")
                .customize(new CustomMapper<CommunityBoardItem, CommunityBoardItemDto>() {
                    @Override
                    public void mapBtoA(CommunityBoardItemDto communityBoardItemDto, CommunityBoardItem communityBoardItem, MappingContext context) {
                        if (null != communityBoardItemDto.getCategory()) {
                            communityBoardItem.setCategory(
                                    communityBoardCategoryDao.findById(communityBoardItemDto.getCategory().getId())
                            );
                        }
                    }
                })
                .register();

        factory.classMap(CommunityBoardItemEditRequest.class, CommunityBoardItem.class)
                .exclude("category")
                .byDefault()
                .field("privacySettings.showEmailAddress", "showEmailAddress")
                .field("privacySettings.showPhoneNumber", "showPhoneNumber")
                .field("privacySettings.showApartmentNumber", "showApartmentNumber")
                .fieldBToA("category", "category")
                .customize(new CustomMapper<CommunityBoardItemEditRequest, CommunityBoardItem>() {
                    @Override
                    public void mapAtoB(CommunityBoardItemEditRequest communityBoardItemEditRequest, CommunityBoardItem communityBoardItem, MappingContext context) {
                        if (null != communityBoardItemEditRequest.getCategory()) {
                            communityBoardItem.setCategory(
                                    communityBoardCategoryDao.findById(communityBoardItemEditRequest.getCategory().getId())
                            );
                        }
                    }
                })
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
                .fieldAToB("id", "userId")
                .fieldBToA("userId", "id")
                .exclude("apartment")
                .byDefault()
                .customize(new CustomMapper<Account, CommunityBoardItemAuthorDto>() {
                    @Override
                    public void mapAtoB(Account account, CommunityBoardItemAuthorDto communityBoardItemAuthorDto, MappingContext context) {
                        Apartment apartment = null;
                        switch (account.getRole()) {
                            case Tenant:
                                apartment = ((Tenant) account).getApartment();
                                break;
                            case SubTenant:
                                apartment = ((SubTenant) account).getApartment();
                                break;
                            default:
                                break;
                        }
                        if (null != apartment) {
                            communityBoardItemAuthorDto.setApartment(map(apartment, CommunityBoardApartmentInfo.class));
                        }

                    }
                })
                .register();

        factory.classMap(CommunityBoardComment.class, CommunityBoardCommentDto.class)
                .byDefault()
                .register();

        factory.classMap(Apartment.class, CommunityBoardApartmentInfo.class)
                .byDefault()
                .register();

        factory.classMap(CommunityBoardCommentEditRequest.class, CommunityBoardComment.class)
                .byDefault()
                .register();
    }

    public CommunityBoardItemDto toCommunityBoardItem(@NotNull CommunityBoardItem communityBoardItem) {
        Objects.requireNonNull(communityBoardItem, "Community board item must not be null");
        return this.map(communityBoardItem, CommunityBoardItemDto.class);
    }

    public CommunityBoardItem toCommunityBoardItem(@NotNull CommunityBoardItemEditRequest communityBoardItem) {
        Objects.requireNonNull(communityBoardItem, "Community board item must not be null");
        return this.map(communityBoardItem, CommunityBoardItem.class);
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

    public CommunityBoardComment toCommunityBoardComment(@NotNull CommunityBoardCommentEditRequest communityBoardCommentDto) {
        Objects.requireNonNull(communityBoardCommentDto, "Comment object can not be null");
        return this.map(communityBoardCommentDto, CommunityBoardComment.class);
    }

}

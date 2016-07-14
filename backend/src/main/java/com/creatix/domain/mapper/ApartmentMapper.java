package com.creatix.domain.mapper;

import com.creatix.domain.dto.apartment.ApartmentDto;
import com.creatix.domain.entity.store.account.TenantBase;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Component
public final class ApartmentMapper extends ConfigurableMapper {

    protected void configure(MapperFactory mapperFactory) {
        super.configure(mapperFactory);

        //region Tenant
        mapperFactory.classMap(TenantBase.class, ApartmentDto.TenantDto.class)
                .byDefault()
                .field("primaryEmail", "email")
                .field("primaryPhone", "phone")
                .register();
        //endregion
    }

    //region Account
    public ApartmentDto.TenantDto toApartmentTenant(@NotNull TenantBase tenant) {
        Objects.requireNonNull(tenant);

        return this.map(tenant, ApartmentDto.TenantDto.class);
    }
    //endregion

}

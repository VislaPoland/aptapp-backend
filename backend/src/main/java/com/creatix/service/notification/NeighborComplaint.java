package com.creatix.service.notification;

import com.creatix.domain.entity.store.Apartment;
import com.creatix.domain.entity.store.account.TenantBase;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Created by Tomas Sedlak on 19.10.2017.
 */
class NeighborComplaint {

    private final long complainerAccountId;

    private final String complainerApartmentUnit;
    private final String complainerMessage;

    NeighborComplaint(@Nonnull TenantBase complainer, String complainerMessage) {
        this.complainerAccountId = complainer.getId();
        this.complainerApartmentUnit = Optional.ofNullable(complainer.getApartment()).map(Apartment::getUnitNumber).orElse(null);
        this.complainerMessage = complainerMessage;
    }

    public long getComplainerAccountId() {
        return complainerAccountId;
    }

    public String getComplainerMessage() {
        return complainerMessage;
    }

    public String getComplainerApartmentUnit() {
        return complainerApartmentUnit;
    }


}

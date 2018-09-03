package com.creatix.service.notification;

import com.creatix.domain.entity.store.account.Account;
import com.creatix.domain.entity.store.account.Tenant;

import javax.annotation.Nonnull;

/**
 * Created by Tomas Sedlak on 19.10.2017.
 */
class NeighborComplaint {

    private final long complainerAccountId;

    private final String complainerAppartmentUnit;
    private final String complainerMessage;

    NeighborComplaint(@Nonnull Tenant complainer, String complainerMessage) {
        this.complainerAccountId = complainer.getId();
        this.complainerAppartmentUnit = complainer.getApartment().getUnitNumber();
        this.complainerMessage = complainerMessage;
    }

    public long getComplainerAccountId() {
        return complainerAccountId;
    }

    public String getComplainerMessage() {
        return complainerMessage;
    }

    public String getComplainerAppartmentUnit() {
        return complainerAppartmentUnit;
    }


}

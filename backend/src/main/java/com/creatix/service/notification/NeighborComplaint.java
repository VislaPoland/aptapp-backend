package com.creatix.service.notification;

import com.creatix.domain.entity.store.account.Account;

import javax.annotation.Nonnull;

/**
 * Created by Tomas Sedlak on 19.10.2017.
 */
class NeighborComplaint {

    private final long complainerAccountId;

    NeighborComplaint(@Nonnull Account complainer) {
        this.complainerAccountId = complainer.getId();
    }

    public long getComplainerAccountId() {
        return complainerAccountId;
    }
}

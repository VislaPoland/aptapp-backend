package com.creatix.service.notification;

import com.creatix.domain.entity.store.account.Account;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Created by Tomas Sedlak on 19.10.2017.
 */
public class Neighbor {
    private final long accountId;

    public Neighbor(@Nonnull Account account) {
        this.accountId = account.getId();
    }

    public long getAccountId() {
        return accountId;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        Neighbor neighbor = (Neighbor) o;
        return accountId == neighbor.accountId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", accountId)
                .toString();
    }
}

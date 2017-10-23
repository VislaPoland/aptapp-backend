package com.creatix.service.notification;

import com.creatix.domain.entity.store.account.Account;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Created by Tomas Sedlak on 19.10.2017.
 */
public class NeighborRelation {
    private final long accountIdLeft;
    private final long accountIdRight;

    public NeighborRelation(@Nonnull Account accountLeft, @Nonnull Account accountRight) {
        this.accountIdLeft = accountLeft.getId();
        this.accountIdRight = accountRight.getId();
    }

    public long getAccountIdLeft() {
        return accountIdLeft;
    }

    public long getAccountIdRight() {
        return accountIdRight;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        NeighborRelation that = (NeighborRelation) o;
        return accountIdLeft == that.accountIdLeft && accountIdRight == that.accountIdRight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountIdLeft, accountIdRight);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id_left", accountIdLeft)
                .append("id_right", accountIdRight)
                .toString();
    }
}

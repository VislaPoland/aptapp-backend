package com.creatix.domain.entity.store.business;

import com.creatix.domain.entity.store.account.Account;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Tomas Michalek on 12/04/2017.
 */
@Entity
@Data
@Accessors(chain = true)
public class DiscountCouponUsage {

    @Embeddable
    @Data
    public static class IdKey implements Serializable {

        public IdKey() {
        }

        public IdKey(Account account, DiscountCoupon discountCoupon) {
            this.account = account;
            this.discountCoupon = discountCoupon;
        }

        @OneToOne(optional = false)
        private Account account;

        @OneToOne(optional = false)
        private DiscountCoupon discountCoupon;
    }

    @EmbeddedId
    IdKey id;

    @Column(nullable = false)
    private int usesLeft;

}

package com.creatix.domain.dao;

import com.creatix.domain.entity.store.account.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Repository
@Transactional
public class AbstractAccountDao<T extends Account> extends DaoBase<T, Long> {

    @Override
    public void persist(T account) {
        if ( account.getCreatedAt() == null ) {
            account.setCreatedAt(new Date());
            account.setUpdatedAt(new Date());
        }
        else {
            account.setUpdatedAt(new Date());
        }

        super.persist(account);
    }

    public String prepareStringInput(String string) {
        return (string == null || string.equals("null")) ? null : "'" + string + "'";
    }

    public String generateSQLUpdateParams(T account) {
        return "dtype=" + prepareStringInput(account.getRole().toString()) +
                ", action_token=" + prepareStringInput(account.getActionToken()) +
                ", action_token_valid_until=" + prepareStringInput(String.valueOf(account.getActionTokenValidUntil())) +
                ", active=" + account.getActive() +
                ", first_name=" + prepareStringInput(account.getFirstName()) +
                ", last_name=" + prepareStringInput(account.getLastName()) +
                ", password_hash=" + prepareStringInput(account.getPasswordHash()) +
                ", primary_email=" + prepareStringInput(account.getPrimaryEmail()) +
                ", primary_phone=" + prepareStringInput(account.getPrimaryPhone()) +
                ", role=" + prepareStringInput(account.getRole().toString()) +
                ", secondary_email=" + prepareStringInput(account.getSecondaryEmail()) +
                ", secondary_phone=" + prepareStringInput(account.getSecondaryPhone()) +
                ", deleted_at=" + prepareStringInput(String.valueOf(account.getDeletedAt())) +
                ", updated_at=" + prepareStringInput(OffsetDateTime.now().toString()) +
                ", created_at=" + prepareStringInput(String.valueOf(account.getCreatedAt())) +
                ", is_neighborhood_notification_enable=" + account.getIsNeighborhoodNotificationEnable();
    }

}

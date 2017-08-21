package com.creatix.service.message;

import com.creatix.domain.dao.PredefinedMessageDao;
import com.creatix.domain.dao.PropertyDao;
import com.creatix.domain.dto.property.message.CreatePredefinedMessageRequest;
import com.creatix.domain.dto.property.message.PredefinedMessageDto;
import com.creatix.domain.entity.store.PredefinedMessage;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * Created by Tomas Sedlak on 21.8.2017.
 */
@Service
public class PredefinedMessageService {

    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private PredefinedMessageDao predefinedMessageDao;
    @Autowired
    private AuthorizationManager authorization;

    @Transactional
    @RoleSecured
    public List<PredefinedMessage> getPredefinedMessages(@NotNull Long propertyId) {
        Objects.requireNonNull(propertyId, "property id");
        final Property property = propertyDao.findById(propertyId);
        authorization.checkRead(property);
        return property.getPredefinedMessages();
    }

    @Transactional
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public PredefinedMessage createFromRequest(@NotNull CreatePredefinedMessageRequest req, @NotNull Long propertyId) {
        Objects.requireNonNull(req, "create new redefined message request");
        Objects.requireNonNull(propertyId, "property id");

        final Property property = propertyDao.findById(propertyId);
        if ( property == null ) {
            throw new EntityNotFoundException(String.format("Property id=%d not found", propertyId));
        }
        authorization.checkWrite(property);

        final PredefinedMessage message = new PredefinedMessage();
        message.setBody(req.getBody());
        message.setProperty(property);
        predefinedMessageDao.persist(message);

        return message;
    }

    @Transactional
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public PredefinedMessage updateFromRequest(PredefinedMessageDto dto) {
        Objects.requireNonNull(dto, "predefined message dto");

        final PredefinedMessage message = predefinedMessageDao.findById(dto.getId());
        if ( message == null ) {
            throw new EntityNotFoundException(String.format("Predefined message id=%d nto found", dto.getId()));
        }

        authorization.checkWrite(message.getProperty());
        message.setBody(dto.getBody());
        predefinedMessageDao.persist(message);

        return message;
    }

    @Transactional
    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public PredefinedMessage deleteById(@NotNull Long messageId) {
        Objects.requireNonNull(messageId, "predefined message id");

        final PredefinedMessage message = predefinedMessageDao.findById(messageId);
        if ( message == null ) {
            throw new EntityNotFoundException(String.format("Predefined message id=%d nto found", messageId));
        }

        authorization.checkWrite(message.getProperty());
        predefinedMessageDao.delete(message);

        return message;
    }
}

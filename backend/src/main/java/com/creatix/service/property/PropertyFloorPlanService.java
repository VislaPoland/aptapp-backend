package com.creatix.service.property;

import com.creatix.controller.exception.AptValidationException;
import com.creatix.domain.entity.store.CsvRecord;
import com.creatix.domain.enums.AccountRole;
import com.creatix.message.MessageDeliveryException;
import com.creatix.security.RoleSecured;
import com.creatix.service.TenantService;
import com.creatix.service.apartment.ApartmentService;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.mail.MessagingException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PropertyFloorPlanService {

    private final Validator validator;
    private final ApartmentService apartmentService;
    private final TenantService tenantService;
    private static final String SEPARATOR = ";";

    private void checkConsistencyCsvRecord(CsvRecord csvRecord) throws AptValidationException {
        Set<ConstraintViolation<CsvRecord>> violations = validator.validate(csvRecord);
        if (!violations.isEmpty()) {
            throw new AptValidationException(String.format("Record %s has inconsistent data.\n%s", csvRecord.getRowDesc(), violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList())));
        }
    }

    @RoleSecured({AccountRole.Administrator})
    @ParametersAreNonnullByDefault
    public void createFloorPlanFromCsv(Long propertyId, MultipartFile csvFile) throws AptValidationException, IOException, MessageDeliveryException, TemplateException, MessagingException {
        Objects.requireNonNull(propertyId);
        Objects.requireNonNull(csvFile);

        if (csvFile.isEmpty()) {
            throw new IllegalArgumentException("Attached csv file is empty");
        }

        byte[] bytes = csvFile.getBytes();
        String completeData = new String(bytes).replace("\"", "");
        String[] rows = completeData.split("\\r?\\n");

        //"First Name", "Last Name", "Floor", "Unit", "Email", "Primary phone"
        final String[] columns = rows[0].split(SEPARATOR);
        if (columns.length != 6) {
            throw new AptValidationException("Column names of csv file are not in correct format");
        }

        for ( int i = 1; i < rows.length; i++ ) {
            CsvRecord csvRecord;
            String[] record = rows[i].split(SEPARATOR);

            if (record.length < 5) {
                throw new AptValidationException(String.format("Record on row %d has inconsistent data", i + 1));
            } else if (record.length == 5) {
                csvRecord = new CsvRecord(record[0], record[1], record[2], record[3], record[4], null);
            } else {
                csvRecord = new CsvRecord(record[0], record[1], record[2], record[3], record[4], record[5]);
            }
            checkConsistencyCsvRecord(csvRecord);

            Long generatedApartmentId = apartmentService.generateApartment(propertyId, csvRecord.getFloor(), csvRecord.getUnitNumber());
            tenantService.generateTenant(generatedApartmentId, csvRecord.getFirstName(), csvRecord.getLastName(), csvRecord.getEmail(), csvRecord.getPhoneNumber());
        }

    }

}

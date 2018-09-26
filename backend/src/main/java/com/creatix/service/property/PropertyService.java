package com.creatix.service.property;

import com.creatix.configuration.FileUploadProperties;
import com.creatix.domain.Mapper;
import com.creatix.domain.dao.*;
import com.creatix.domain.dto.property.CreatePropertyRequest;
import com.creatix.domain.dto.property.PropertyStatsDto;
import com.creatix.domain.dto.property.UpdatePropertyRequest;
import com.creatix.domain.entity.store.Property;
import com.creatix.domain.entity.store.PropertyPhoto;
import com.creatix.domain.entity.store.account.PropertyOwner;
import com.creatix.domain.entity.store.account.SubTenant;
import com.creatix.domain.entity.store.account.Tenant;
import com.creatix.domain.entity.store.account.TenantBase;
import com.creatix.domain.enums.AccountRole;
import com.creatix.domain.enums.PropertyStatus;
import com.creatix.security.AuthorizationManager;
import com.creatix.security.RoleSecured;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyService {
    @Autowired
    private PropertyDao propertyDao;
    @Autowired
    private PropertyOwnerDao propertyOwnerDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private Mapper mapper;
    @Autowired
    private AuthorizationManager authorizationManager;
    @Autowired
    private FileUploadProperties uploadProperties;
    @Autowired
    private PropertyPhotoDao propertyPhotoDao;

    private static String[] columns = { "First Name", "Last Name", "Primary phone", "Primary email", "Created date", "Token valid until" };


    private <T, ID> T getOrElseThrow(ID id, DaoBase<T, ID> dao, EntityNotFoundException ex) {
        final T item = dao.findById(id);
        if ( item == null ) {
            throw ex;
        }
        return item;
    }

    @RoleSecured
    public List<Property> getAllProperties() {
        return propertyDao.findAll().stream()
                .filter(p -> authorizationManager.canRead(p))
                .collect(Collectors.toList());
    }

    @RoleSecured(AccountRole.Administrator)
    public Property createFromRequest(@NotNull CreatePropertyRequest request) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(request.getPropertyOwnerId(), "Property owner ID is null");

        final Property property = mapper.toProperty(request);
        final PropertyOwner propertyOwner = getOrElseThrow(request.getPropertyOwnerId(), propertyOwnerDao,
                new EntityNotFoundException(String.format("Property owner %d not found", request.getPropertyOwnerId())));
        property.setOwner(propertyOwner);
        property.setStatus(PropertyStatus.Draft);
        if ( StringUtils.isBlank(property.getTimeZone()) ) {
            property.setTimeZone(TimeZone.getDefault().toString());
        }
        propertyDao.persist(property);
        return property;
    }

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public Property updateFromRequest(Long propertyId, @NotNull UpdatePropertyRequest request) {
        Objects.requireNonNull(request);

        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property %d not found", propertyId)));

        // we are not distinguish Administrator vs PO/PM, so update is same as write
        if ( authorizationManager.canWrite(property) ) {
            if ( request.getPropertyOwnerId() != null ) {
                final PropertyOwner propertyOwner = propertyOwnerDao.findById(request.getPropertyOwnerId());
                if ( propertyOwner == null ) {
                    throw new EntityNotFoundException(String.format("Property owner %d not found", request.getPropertyOwnerId()));
                }
                property.setOwner(propertyOwner);
            }
            mapper.fillProperty(request, property);
            if ( StringUtils.isBlank(property.getTimeZone()) ) {
                property.setTimeZone(TimeZone.getDefault().toString());
            }

            if (request.getThrottleFastMinutes() != null) {
                property.setThrottleFastMinutes(request.getThrottleFastMinutes());
            }
            if (request.getLockoutHours() != null ) {
                property.setLockoutHours(request.getLockoutHours());
            }

            if (request.getThrottleSlowLimit() != null) {
                property.setThrottleSlowLimit(request.getThrottleSlowLimit());
            }

            if (request.getEnableEmailEscalation() != null) {
                property.setEnableEmailEscalation(request.getEnableEmailEscalation());
            }

            if (request.getEnableSmsEscalation() != null) {
                property.setEnableSmsEscalation(request.getEnableSmsEscalation());
            }

            propertyDao.persist(property);
            return property;
        }

        throw new SecurityException(String.format("You are not eligible to update info about property with id=%d", propertyId));
    }

    @RoleSecured(AccountRole.Administrator)
    public Property deleteProperty(long propertyId) {
        final Property property = getOrElseThrow(propertyId, propertyDao, new EntityNotFoundException(String.format("Property %d not found", propertyId)));
        property.setDeleteDate(new Date());
        propertyDao.persist(property);
        return property;
    }

    @RoleSecured
    public Property getProperty(@NotNull Long propertyId) {
        Objects.requireNonNull(propertyId);
        final Property property = this.propertyDao.findById(propertyId);
        if ( property == null ) {
            throw new EntityNotFoundException(String.format("Property id=%d not found", propertyId));
        }
        authorizationManager.checkRead(property);

        return property;
    }

    private List<Tenant> getTenants(Long propertyId){
        return tenantDao.findByProperty(propertyId);
    }

    private String returnCsvRow(TenantBase tenant){
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(tenant.getFirstName())
                .add(tenant.getLastName())
                .add(tenant.getPrimaryPhone())
                .add(tenant.getPrimaryEmail())
                .add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone( ZoneId.systemDefault() ).format(tenant.getCreatedAt().toInstant()))
                .add(tenant.getActive() ? "Yes" : "No");
        return joiner.toString();
    }

    private Row addRowToSheet(Sheet sheet, int rowPosition,TenantBase tenant){
        Row row = sheet.createRow(rowPosition);
        row.createCell(0).setCellValue(tenant.getFirstName());
        row.createCell(1).setCellValue(tenant.getLastName());
        row.createCell(2).setCellValue(tenant.getPrimaryPhone());
        row.createCell(3).setCellValue(tenant.getPrimaryEmail());
        row.createCell(4).setCellValue(tenant.getCreatedAt().toString());

        if(tenant.getActionTokenValidUntil() != null )
            row.createCell(5).setCellValue(tenant.getActionTokenValidUntil().toString());
        else row.createCell(5).setCellValue("");

        return row;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public String generateCsvResponse(Long propertyId){
        String csvResponse = "First Name,Last Name,Primary Phone,Primary Email,Created At,Active users";
        for(Tenant tenant: getTenants(propertyId)){
            csvResponse+="\n"+returnCsvRow(tenant);
            Set<SubTenant> subTenants = tenant.getSubTenants();
            for(SubTenant subTenant:subTenants){
               csvResponse+="\n"+returnCsvRow(subTenant);
            }
        }
        return csvResponse;
    }

    public String generateAllCsvResponse(){
        Map<String, String> allTenant = propertyDao.findAll().stream().collect(Collectors.toMap(property -> property.getName(), property -> generateCsvResponse(property.getId())));
        return allTenant.entrySet().stream().map(entry ->  entry.getKey() + "\n" + entry.getValue() + "\n").collect(Collectors.joining("\n"));
    }

    public Workbook generateXlsxResponse(Long propertyId){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report");

        // create header
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
        //content
        int rowPosition = 1;
        for(Tenant tenant: getTenants(propertyId)){
            addRowToSheet(sheet,rowPosition++, tenant );
            Set<SubTenant> subTenants = tenant.getSubTenants();
            for(SubTenant subTenant:subTenants){
                addRowToSheet(sheet, rowPosition++, subTenant );
            }
        }

        // Resize all columns to fit the content size
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        return workbook;
    }

    @RoleSecured
    public Property storePropertyPhotos(MultipartFile[] files, long propertyId) throws IOException {

        final Property property = getProperty(propertyId);

        for ( MultipartFile file : files ) {

            // move uploaded file to file repository
            final String fileName = String.format("%d-%d-%s", property.getId(), property.getPhotos().size(), file.getOriginalFilename());
            final Path photoFilePath = Paths.get(uploadProperties.getRepositoryPath(), fileName);
            Files.createDirectories(photoFilePath.getParent());
            file.transferTo(photoFilePath.toFile());

            final PropertyPhoto photo = new PropertyPhoto();
            photo.setProperty(property);
            photo.setFileName(fileName);
            photo.setFilePath(photoFilePath.toString());
            propertyPhotoDao.persist(photo);

            property.getPhotos().add(photo);
        }

        return property;
    }

    public PropertyPhoto getPropertyPhoto(@NotNull Long propertyId, @NotNull String fileName) {
        Objects.requireNonNull(propertyId, "Property id is null");
        Objects.requireNonNull(fileName, "File name is null");

        final PropertyPhoto photo = propertyPhotoDao.findByPropertyIdAndFileName(propertyId, fileName);
        if ( photo == null ) {
            throw new EntityNotFoundException(String.format("Photo id=%s not found", fileName));
        }

        return photo;
    }

    @RoleSecured({AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.PropertyOwner, AccountRole.Administrator})
    public PropertyPhoto deletePropertyPhoto(Long propertyPhotoId) throws IOException {
        final PropertyPhoto photo = propertyPhotoDao.findById(propertyPhotoId);
        if ( photo == null ) {
            throw new EntityNotFoundException(String.format("Photo id=%d not found", propertyPhotoId));
        }

        propertyPhotoDao.delete(photo);

        Files.deleteIfExists(new File(photo.getFilePath()).toPath());

        return photo;
    }

    @RoleSecured({AccountRole.Administrator, AccountRole.PropertyOwner, AccountRole.PropertyManager, AccountRole.AssistantPropertyManager})
    public @NotNull PropertyStatsDto getPropertyStats(@NotNull Long propertyId) {
        Objects.requireNonNull(propertyId, "property id");

        final PropertyStatsDto stats = new PropertyStatsDto();
        stats.setId(propertyId);
        stats.setEmployeeCount(accountDao.countByRoleAndActivationStatus(EnumSet.of(AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security), null));
        stats.setActivatedEmployeeCount(accountDao.countByRoleAndActivationStatus(EnumSet.of(AccountRole.PropertyManager, AccountRole.AssistantPropertyManager, AccountRole.Maintenance, AccountRole.Security), true));
        stats.setResidentCount(accountDao.countByRoleAndActivationStatus(EnumSet.of(AccountRole.Tenant, AccountRole.SubTenant), null));
        stats.setActivatedResidentCount(accountDao.countByRoleAndActivationStatus(EnumSet.of(AccountRole.Tenant, AccountRole.SubTenant), true));

        return stats;
    }
}

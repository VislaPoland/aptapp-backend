package com.creatix.domain;

import com.creatix.configuration.ApplicationProperties;
import com.creatix.domain.dao.AssistantPropertyManagerDao;
import com.creatix.domain.dao.ManagedEmployeeDao;
import com.creatix.domain.dto.AddressDto;
import com.creatix.domain.dto.PageableDataResponse;
import com.creatix.domain.dto.account.*;
import com.creatix.domain.dto.apartment.ApartmentDto;
import com.creatix.domain.dto.apartment.BasicApartmentDto;
import com.creatix.domain.dto.apartment.PersistApartmentRequest;
import com.creatix.domain.dto.notification.NotificationDto;
import com.creatix.domain.dto.notification.NotificationPhotoDto;
import com.creatix.domain.dto.notification.maintenance.CreateMaintenanceNotificationRequest;
import com.creatix.domain.dto.notification.maintenance.MaintenanceNotificationDto;
import com.creatix.domain.dto.notification.neighborhood.CreateNeighborhoodNotificationRequest;
import com.creatix.domain.dto.notification.neighborhood.NeighborhoodNotificationDto;
import com.creatix.domain.dto.notification.security.CreateSecurityNotificationRequest;
import com.creatix.domain.dto.notification.security.SecurityNotificationDto;
import com.creatix.domain.dto.property.*;
import com.creatix.domain.dto.property.slot.*;
import com.creatix.domain.dto.tenant.ParkingStallDto;
import com.creatix.domain.dto.tenant.PersistTenantRequest;
import com.creatix.domain.dto.tenant.TenantDto;
import com.creatix.domain.dto.tenant.VehicleDto;
import com.creatix.domain.dto.tenant.subs.PersistSubTenantRequest;
import com.creatix.domain.dto.tenant.subs.SubTenantDto;
import com.creatix.domain.entity.store.*;
import com.creatix.domain.entity.store.account.*;
import com.creatix.domain.entity.store.notification.*;
import com.creatix.domain.enums.AccountRole;
import com.creatix.security.AuthorizationManager;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class Mapper {

    private MapperFactory mapperFactory;

    @Autowired
    private ManagedEmployeeDao managedEmployeeDao;
    @Autowired
    private AssistantPropertyManagerDao assistantPropertyManagerDao;
    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private AuthorizationManager authorizationManager;

    @Autowired
    public Mapper(MapperFactory mapperFactory) {
        this.mapperFactory = mapperFactory;
        this.configure(mapperFactory);
    }

    private String createNotificationPhotoDownloadUrl(NotificationPhoto photo) throws MalformedURLException, URISyntaxException {
        return applicationProperties.buildBackendUrl(String.format("api/notifications/%d/photos/%s", photo.getNotification().getId(), photo.getFileName())).toString();
    }
    private String createPropertyPhotoDownloadUrl(PropertyPhoto photo) throws MalformedURLException, URISyntaxException {
        return applicationProperties.buildBackendUrl(String.format("api/properties/%d/photos/%s", photo.getProperty().getId(), photo.getFileName())).toString();
    }

    private void configure(MapperFactory mapperFactory) {

        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(OffsetDateTime.class, OffsetDateTime.class));
        mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(LocalTime.class, LocalTime.class));


        mapperFactory.classMap(Account.class, AccountDto.class)
                .byDefault()
                .customize(new CustomMapper<Account, AccountDto>() {
                    @Override
                    public void mapAtoB(Account account, AccountDto accountDto, MappingContext context) {
                        super.mapAtoB(account, accountDto, context);

                        if ( account instanceof Tenant ) {
                            final Apartment apartment = ((Tenant) account).getApartment();
                            if ( apartment != null ) {
                                final Property property = apartment.getProperty();
                                PropertyDto details = toPropertyDto(property);
                                accountDto.setProperty(details);
                                accountDto.setApartment(toApartmentDto(apartment));
                            }
                        }
                        else if ( account instanceof PropertyManager ) {
                            final Property managedProperty = ((PropertyManager) account).getManagedProperty();
                            if ( managedProperty != null ) {
                                accountDto.setProperty(toPropertyDto(managedProperty));
                            }
                        }
                        else if ( account instanceof ManagedEmployee ) {
                            final Property managedProperty = ((ManagedEmployee) account).getManager().getManagedProperty();
                            if ( managedProperty != null ) {
                                accountDto.setProperty(toPropertyDto(managedProperty));
                            }
                        }
                        else if ( account instanceof AssistantPropertyManager ) {
                            final Property managedProperty = ((AssistantPropertyManager) account).getManager().getManagedProperty();
                            if ( managedProperty != null ) {
                                accountDto.setProperty(toPropertyDto(managedProperty));
                            }
                        }
                        else if ( account instanceof SubTenant ) {
                            final Apartment apartment = ((SubTenant) account).getApartment();
                            if ( apartment != null ) {
                                accountDto.setProperty(toPropertyDto(apartment.getProperty()));
                                accountDto.setApartment(toApartmentDto(apartment));
                            }
                        }
                    }
                })
                .register();

        mapperFactory.classMap(Account.class, BasicAccountDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(PropertyOwner.class, PropertyOwnerDto.class)
                .byDefault()
                .customize(new CustomMapper<PropertyOwner, PropertyOwnerDto>() {
                    @Override
                    public void mapAtoB(PropertyOwner a, PropertyOwnerDto b, MappingContext context) {
                        b.setOwnedProperties(new ArrayList<>());
                        if ( a.getOwnedProperties() != null ) {
                            a.getOwnedProperties().stream().filter(property -> property.getDeleteDate() == null).forEach(property -> {
                                b.getOwnedProperties().add(toPropertyDto(property));
                            });
                        }
                    }
                })
                .register();
        mapperFactory.classMap(PropertyPhoto.class, PropertyPhotoDto.class)
                .byDefault()
                .customize(new CustomMapper<PropertyPhoto, PropertyPhotoDto>() {
                    @Override
                    public void mapAtoB(PropertyPhoto a, PropertyPhotoDto b, MappingContext context) {
                        try {
                            b.setFileUrl(createPropertyPhotoDownloadUrl(a));
                        }
                        catch ( MalformedURLException | URISyntaxException e ) {
                            throw new IllegalStateException("Failed to create download URL", e);
                        }
                    }
                })
                .register();
        mapperFactory.classMap(Property.class, PropertyDto.class)
                .exclude("managers")
                .byDefault()
                .field("address.fullAddress", "fullAddress")
                .customize(
                        new CustomMapper<Property, PropertyDto>() {
                            @Override
                            public void mapAtoB(Property property, PropertyDto propertyDto, MappingContext context) {
                                super.mapAtoB(property, propertyDto, context);

                                propertyDto.setAssistantManagers(
                                        assistantPropertyManagerDao.findByProperty(property).stream()
                                                .filter(Account::getActive)
                                                .filter(a -> a.getDeletedAt() == null)
                                                .map(e -> mapperFactory.getMapperFacade().map(e, BasicAccountDto.class))
                                                .collect(Collectors.toList())
                                );
                                propertyDto.setEmployees(
                                        managedEmployeeDao.findByProperty(property).stream()
                                                .filter(Account::getActive)
                                                .filter(a -> a.getDeletedAt() == null)
                                                .map(e -> mapperFactory.getMapperFacade().map(e, BasicAccountDto.class))
                                                .collect(Collectors.toList())
                                );
                                if ( property.getManagers() != null ) {
                                    propertyDto.setManagers(property.getManagers().stream()
                                            .filter(Account::getActive)
                                            .filter(a -> a.getDeletedAt() == null)
                                            .map(e -> mapperFactory.getMapperFacade().map(e, BasicAccountDto.class))
                                            .collect(Collectors.toList())
                                    );
                                }
                            }
                        }
                )
                .register();

        mapperFactory.classMap(Facility.class, PropertyDto.FacilityDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Contact.class, PropertyDto.ContactDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(PropertyOwner.class, PropertyDto.OwnerDto.class)
                .byDefault()
                .field("primaryEmail", "email")
                .field("primaryPhone", "phone")
                .field("website", "web")
                .register();

        mapperFactory.classMap(Notification.class, NotificationDto.class)
                .byDefault()
                .customize(new CustomMapper<Notification, NotificationDto>() {
                    @Override
                    public void mapAtoB(Notification notification, NotificationDto notificationDto, MappingContext context) {

                        if ( !(authorizationManager.hasCurrentAccount()) || authorizationManager.hasAnyOfRoles(AccountRole.Tenant) ) {
                            // only authenticated non-tenant accounts are allowed to see author of the notification
                            notificationDto.setAuthor(null);
                        }
                    }
                })
                .register();
        mapperFactory.classMap(SecurityNotification.class, SecurityNotificationDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(MaintenanceNotification.class, MaintenanceNotificationDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(NeighborhoodNotification.class, NeighborhoodNotificationDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(NotificationPhoto.class, NotificationPhotoDto.class)
                .byDefault()
                .customize(new CustomMapper<NotificationPhoto, NotificationPhotoDto>() {
                    @Override
                    public void mapAtoB(NotificationPhoto a, NotificationPhotoDto b, MappingContext context) {
                        try {
                            b.setFileUrl(createNotificationPhotoDownloadUrl(a));
                        }
                        catch ( MalformedURLException | URISyntaxException e ) {
                            throw new IllegalStateException("Failed to create download URL", e);
                        }
                    }
                })
                .register();



        mapperFactory.classMap(CreateSecurityNotificationRequest.class, SecurityNotification.class)
                .byDefault()
                .register();

        mapperFactory.classMap(CreateMaintenanceNotificationRequest.class, MaintenanceNotification.class)
                .byDefault()
                .register();

        mapperFactory.classMap(CreateNeighborhoodNotificationRequest.class, NeighborhoodNotification.class)
                .byDefault()
                .register();
        mapperFactory.classMap(ApartmentNeighbor.class, ApartmentDto.NeighborApartmentDto.class)
                .byDefault()
                .field("apartment.id", "id")
                .field("apartment.floor", "floor")
                .register();
        mapperFactory.classMap(ApartmentNeighbors.class, ApartmentDto.NeighborsDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(PersistApartmentRequest.class, Apartment.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Apartment.class, BasicApartmentDto.class)
                .field("tenant.fullName", "tenantName")
                .byDefault()
                .register();

        mapperFactory.classMap(Apartment.class, ApartmentDto.class)
                .byDefault()
                .field("tenant.fullName", "fullName")
                .field("tenant.primaryEmail", "primaryEmail")
                .field("property.id", "propertyId")
                .field("tenant.id", "tenantId")
                .register();

        mapperFactory.classMap(AddressDto.class, Address.class)
                .byDefault()
                .register();

        mapperFactory.classMap(CreatePropertyRequest.class, Property.class)
                .byDefault()
                .register();
        mapperFactory.classMap(UpdatePropertyRequest.class, Property.class)
                .byDefault()
                .register();

        mapperFactory.classMap(PersistTenantRequest.class, Tenant.class)
                .exclude("parkingStalls")
                .exclude("vehicles")
                .byDefault()
                .customize(new CustomMapper<PersistTenantRequest, Tenant>() {
                    @Override
                    public void mapAtoB(PersistTenantRequest request, Tenant tenant, MappingContext context) {
                        mapVehicles(request, tenant);
                        mapParkingStalls(request, tenant);
                    }

                    private void mapVehicles(PersistTenantRequest request, Tenant tenant) {
                        final List<VehicleDto> vehicleDtoList = request.getVehicles() != null ? request.getVehicles() : Collections.emptyList();
                        if ( tenant.getVehicles() == null ) {
                            tenant.setVehicles(new HashSet<>());
                        }

                        final Map<Long, Vehicle> idToVehicleMap = tenant.getVehicles().stream()
                                .collect(Collectors.toMap(Vehicle::getId, Function.identity()));

                        vehicleDtoList.forEach(vehicleDto -> {
                            if ( vehicleDto.getId() == null ) {
                                final Vehicle vehicle = mapperFactory.getMapperFacade().map(vehicleDto, Vehicle.class);
                                tenant.addVehicle(vehicle);
                            }
                            else {
                                final Vehicle vehicle = idToVehicleMap.get(vehicleDto.getId());
                                if ( vehicle != null ) {
                                    mapperFactory.getMapperFacade().map(vehicleDto, vehicle);
                                    // remove updated vehicle from map
                                    idToVehicleMap.remove(vehicle.getId());
                                }
                            }
                        });

                        // all vehicles that are now in map were not present in the input data --> remove them from tenant vehicle set
                        idToVehicleMap.values().forEach(tenant::removeVehicle);
                    }

                    private void mapParkingStalls(PersistTenantRequest request, Tenant tenant) {
                        final List<ParkingStallDto> parkingStallDtoList = request.getParkingStalls() != null ? request.getParkingStalls() : Collections.emptyList();
                        if ( tenant.getParkingStalls() == null ) {
                            tenant.setParkingStalls(new HashSet<>());
                        }

                        final Map<Long, ParkingStall> idToParkingStallMap = tenant.getParkingStalls().stream()
                                .collect(Collectors.toMap(ParkingStall::getId, Function.identity()));

                        parkingStallDtoList.forEach(parkingStallDto -> {
                            if ( parkingStallDto.getId() == null ) {
                                final ParkingStall parkingStall = mapperFactory.getMapperFacade().map(parkingStallDto, ParkingStall.class);
                                tenant.addParkingStall(parkingStall);
                            }
                            else {
                                final ParkingStall parkingStall = idToParkingStallMap.get(parkingStallDto.getId());
                                if ( parkingStall != null ) {
                                    mapperFactory.getMapperFacade().map(parkingStallDto, parkingStall);
                                    // remove updated parking stall from map
                                    idToParkingStallMap.remove(parkingStall.getId());
                                }
                            }
                        });

                        // all parking stalls that are now in map were not present in the input data --> remove them from tenant parking stall set
                        idToParkingStallMap.values().forEach(tenant::removeParkingStall);
                    }
                })
                .register();

        mapperFactory.classMap(Tenant.class, TenantDto.class)
                .field("apartment.property", "property")
                .field("apartment.id", "apartmentId")
                .byDefault()
                .register();

        mapperFactory.classMap(Vehicle.class, VehicleDto.class)
                .exclude("licensePlate")
                .byDefault()
                .register();

        mapperFactory.classMap(ParkingStall.class, ParkingStallDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(SubTenant.class, SubTenantDto.class)
                .byDefault()
                .field("primaryPhone", "phone")
                .field("primaryEmail", "email")
                .field("parentTenant.id", "parentTenantId")
                .register();

        mapperFactory.classMap(SubTenant.class, TenantDto.SubTenantDto.class)
                .byDefault()
                .field("primaryPhone", "phone")
                .field("primaryEmail", "email")
                .register();

        mapperFactory.classMap(PersistSubTenantRequest.class, SubTenant.class)
                .byDefault()
                .field("phone", "primaryPhone")
                .field("email", "primaryEmail")
                .register();

        mapperFactory.classMap(UpdateAccountProfileRequest.class, Account.class)
                .exclude("firstName")
                .exclude("lastName")
                .exclude("primaryPhone")
                .byDefault()
                .customize(new CustomMapper<UpdateAccountProfileRequest, Account>() {
                    @Override
                    public void mapAtoB(UpdateAccountProfileRequest request, Account account, MappingContext context) {
                        if ( authorizationManager.canModifyAllProfileInfo() ) {
                            BeanUtils.copyProperties(request, account);
                        }
                        if ( account instanceof PropertyOwner ) {
                            ((PropertyOwner) account).setWebsite(request.getWebsite());
                        }
                    }
                })
                .register();
        mapperFactory.classMap(PersistAdministratorRequest.class, Account.class)
                .byDefault()
                .register();
        mapperFactory.classMap(PersistPropertyOwnerRequest.class, PropertyOwner.class)
                .byDefault()
                .register();
        mapperFactory.classMap(PersistPropertyManagerRequest.class, PropertyManager.class)
                .byDefault()
                .register();
        mapperFactory.classMap(PersistSecurityGuyRequest.class, SecurityEmployee.class)
                .byDefault()
                .register();
        mapperFactory.classMap(PersistMaintenanceGuyRequest.class, MaintenanceEmployee.class)
                .byDefault()
                .register();
        mapperFactory.classMap(PersistAssistantPropertyManagerRequest.class, AssistantPropertyManager.class)
                .byDefault()
                .register();

        mapperFactory.classMap(PersistEventSlotRequest.class, EventSlot.class)
                .field("durationMinutes", "unitDurationMinutes")
                .byDefault()
                .register();
        mapperFactory.classMap(MaintenanceSlot.class, MaintenanceSlotDto.class)
                .exclude("reservations")
                .byDefault()
                .customize(new CustomMapper<MaintenanceSlot, MaintenanceSlotDto>() {
                    @Override
                    public void mapAtoB(MaintenanceSlot slot, MaintenanceSlotDto slotDto, MappingContext context) {
                        if ( slot.getReservations() == null ) {
                            slotDto.setReservations(null);
                        }
                        else {
                            slotDto.setReservations(slot.getReservations().stream()
                                    .filter(r -> authorizationManager.canRead(r))
                                    .map(r -> toMaintenanceReservationDto(r))
                                    .collect(Collectors.toList()));
                        }
                    }
                })
                .register();
        mapperFactory.classMap(MaintenanceReservation.class, MaintenanceReservationDto.class)
                .exclude("slot")    // prevent recursive mapping
                .exclude("units")   // prevent recursive mapping
                .byDefault()
                .register();
        mapperFactory.classMap(MaintenanceSlotSchedule.class, MaintenanceSlotScheduleDto.class)
                .byDefault()
                .register();
        mapperFactory.classMap(EventSlot.class, EventSlotDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(SlotUnit.class, SlotUnitDto.class)
                .byDefault()
                .field("slot.id", "slotId")
                .customize(new CustomMapper<SlotUnit, SlotUnitDto>() {
                    @Override
                    public void mapAtoB(SlotUnit a, SlotUnitDto b, MappingContext context) {
                        b.setBeginTime(a.getSlot().getBeginTime().plusMinutes(a.getSlot().getUnitDurationMinutes() * a.getOffset()));
                        b.setEndTime(b.getBeginTime().plusMinutes(a.getSlot().getUnitDurationMinutes()));
                    }
                })
                .register();
    }

    public PropertyPhotoDto toPropertyPhotoDto(@NotNull PropertyPhoto propertyPhoto) {
        Objects.requireNonNull(propertyPhoto, "Property photo is null");
        return mapperFactory.getMapperFacade().map(propertyPhoto, PropertyPhotoDto.class);
    }

    public void fillApartment(@NotNull PersistApartmentRequest req, @NotNull Apartment ap) {
        Objects.requireNonNull(req);
        Objects.requireNonNull(ap);
        mapperFactory.getMapperFacade().map(req, ap);
    }

    public void fillAccount(@NotNull UpdateAccountProfileRequest req, @NotNull Account acc) {
        Objects.requireNonNull(req);
        Objects.requireNonNull(acc);

        mapperFactory.getMapperFacade().map(req, acc);
    }

    public void fillAccount(PersistAssistantPropertyManagerRequest req, AssistantPropertyManager acc) {
        mapperFactory.getMapperFacade().map(req, acc);
    }

    public void fillAccount(PersistSecurityGuyRequest req, SecurityEmployee acc) {
        mapperFactory.getMapperFacade().map(req, acc);
    }

    public void fillAccount(PersistMaintenanceGuyRequest req, MaintenanceEmployee acc) {
        mapperFactory.getMapperFacade().map(req, acc);
    }

    public void fillAccount(PersistPropertyManagerRequest req, Account acc) {
        mapperFactory.getMapperFacade().map(req, acc);
    }

    public void fillAccount(PersistAdministratorRequest req, Account acc) {
        mapperFactory.getMapperFacade().map(req, acc);
    }

    public void fillAccount(PersistPropertyOwnerRequest req, Account acc) {
        mapperFactory.getMapperFacade().map(req, acc);
    }

    public Tenant toTenant(@NotNull PersistTenantRequest request) {
        Objects.requireNonNull(request);
        return mapperFactory.getMapperFacade().map(request, Tenant.class);
    }

    public void fillTenant(@NotNull PersistTenantRequest dto, @NotNull Tenant entity) {
        Objects.requireNonNull(dto);
        Objects.requireNonNull(entity);

        mapperFactory.getMapperFacade().map(dto, entity);
    }

    public void fillProperty(@NotNull UpdatePropertyRequest dto, @NotNull Property entity) {
        Objects.requireNonNull(dto);
        Objects.requireNonNull(entity);

        mapperFactory.getMapperFacade().map(dto, entity);
    }

    public Property toProperty(@NotNull CreatePropertyRequest request) {
        Objects.requireNonNull(request);
        return mapperFactory.getMapperFacade().map(request, Property.class);
    }

    public ApartmentDto toApartmentDto(@NotNull Apartment apartment) {
        Objects.requireNonNull(apartment);
        return mapperFactory.getMapperFacade().map(apartment, ApartmentDto.class);
    }

    public AccountDto toAccountDto(Account account) {
        Objects.requireNonNull(account);
        return mapperFactory.getMapperFacade().map(account, AccountDto.class);
    }

    public PropertyDto toPropertyDto(@NotNull Property property) {
        Objects.requireNonNull(property);
        return mapperFactory.getMapperFacade().map(property, PropertyDto.class);
    }

    public NotificationDto toNotificationDto(@NotNull Notification n) {
        Objects.requireNonNull(n);
        return mapperFactory.getMapperFacade().map(n, NotificationDto.class);
    }

    public SecurityNotificationDto toSecurityNotificationDto(@NotNull SecurityNotification n) {
        Objects.requireNonNull(n);
        return mapperFactory.getMapperFacade().map(n, SecurityNotificationDto.class);
    }

    public MaintenanceNotificationDto toMaintenanceNotificationDto(@NotNull MaintenanceNotification n) {
        Objects.requireNonNull(n);
        return mapperFactory.getMapperFacade().map(n, MaintenanceNotificationDto.class);
    }

    public NeighborhoodNotificationDto toNeighborhoodNotificationDto(@NotNull NeighborhoodNotification n) {
        Objects.requireNonNull(n);
        return mapperFactory.getMapperFacade().map(n, NeighborhoodNotificationDto.class);
    }

    public SecurityNotification fromSecurityNotificationRequest(@NotNull CreateSecurityNotificationRequest dto) {
        Objects.requireNonNull(dto);
        return mapperFactory.getMapperFacade().map(dto, SecurityNotification.class);
    }

    public MaintenanceNotification fromMaintenanceNotificationRequest(@NotNull CreateMaintenanceNotificationRequest dto) {
        Objects.requireNonNull(dto);
        return mapperFactory.getMapperFacade().map(dto, MaintenanceNotification.class);
    }

    public NeighborhoodNotification fromNeighborhoodNotificationRequest(@NotNull CreateNeighborhoodNotificationRequest dto) {
        Objects.requireNonNull(dto);
        return mapperFactory.getMapperFacade().map(dto, NeighborhoodNotification.class);
    }

    public TenantDto toTenantDto(@NotNull Tenant tenant) {
        Objects.requireNonNull(tenant);
        return mapperFactory.getMapperFacade().map(tenant, TenantDto.class);
    }

    public VehicleDto toVehicleDto(@NotNull Vehicle vehicle) {
        Objects.requireNonNull(vehicle);
        return mapperFactory.getMapperFacade().map(vehicle, VehicleDto.class);
    }

    public ParkingStallDto toParkingStallDto(@NotNull ParkingStall parkingStall) {
        Objects.requireNonNull(parkingStall);

        return mapperFactory.getMapperFacade().map(parkingStall, ParkingStallDto.class);
    }

    public SubTenantDto toSubTenantDto(@NotNull SubTenant subTenant) {
        Objects.requireNonNull(subTenant);

        return mapperFactory.getMapperFacade().map(subTenant, com.creatix.domain.dto.tenant.subs.SubTenantDto.class);
    }

    public SubTenant toSubTenant(@NotNull PersistSubTenantRequest request) {
        Objects.requireNonNull(request);

        return mapperFactory.getMapperFacade().map(request, SubTenant.class);
    }

    public void fillSubTenant(@NotNull PersistSubTenantRequest request, @NotNull SubTenant entity) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(entity);

        mapperFactory.getMapperFacade().map(request, entity);
    }

    public MaintenanceSlotDto toMaintenanceSlotDto(@NotNull MaintenanceSlot slot) {
        Objects.requireNonNull(slot);
        return mapperFactory.getMapperFacade().map(slot, MaintenanceSlotDto.class);
    }

    public MaintenanceReservationDto toMaintenanceReservationDto(@NotNull MaintenanceReservation reservation) {
        Objects.requireNonNull(reservation);
        return mapperFactory.getMapperFacade().map(reservation, MaintenanceReservationDto.class);
    }

    public MaintenanceSlotScheduleDto toMaintenanceSlotScheduleDto(@Autowired MaintenanceSlotSchedule schedule) {
        Objects.requireNonNull(schedule);
        return mapperFactory.getMapperFacade().map(schedule, MaintenanceSlotScheduleDto.class);
    }

    public EventSlotDto toEventSlotDto(@NotNull EventSlot slot) {
        Objects.requireNonNull(slot);
        return mapperFactory.getMapperFacade().map(slot, EventSlotDto.class);
    }

    public void fillEventSlot(@NotNull PersistEventSlotRequest req, @NotNull EventSlot slot) {
        Objects.requireNonNull(req);
        Objects.requireNonNull(slot);
        mapperFactory.getMapperFacade().map(req, slot);
    }

    public SlotDto toSlotDto(@NotNull Slot slot) {
        Objects.requireNonNull(slot);
        return mapperFactory.getMapperFacade().map(slot, SlotDto.class);
    }

    public <T, R> PageableDataResponse<List<R>> toPageableDataResponse(@NotNull PageableDataResponse<List<T>> response, @NotNull Function<T, R> mappingFunction) {
        Objects.requireNonNull(response);
        Objects.requireNonNull(mappingFunction);

        return new PageableDataResponse<>(response.getData().stream()
                .map(mappingFunction)
                .collect(Collectors.toList()), response.getPageSize(), response.getNextId());
    }
}

package com.creatix.domain.enums;

public enum ContactType {
    Police,
    MedicalEmergency,
    FireService,
    Business,
    Reception,
    MaintenanceEmergency,
    Other,

    //TODO: after running migration (change 'OtherEmergency' to 'Maintenance Emergency' and 'OtherUseful' to 'Reception') and change ion web and mobile app delete Deprecated enums
    @Deprecated
    OtherUseful,
    @Deprecated
    OtherEmergency
}

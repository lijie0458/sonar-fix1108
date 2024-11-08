package com.dogfood.aa20240808.domain.structure.anonymous; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import com.dogfood.aa20240808.domain.entities.OtherReturnStorageDetailsEntity; 
import com.dogfood.aa20240808.domain.entities.MaterialDetailsEntity; 
import com.dogfood.aa20240808.domain.entities.Storage_locationEntity; 
import com.dogfood.aa20240808.domain.entities.MeasuringUnitEntity; 
import com.dogfood.aa20240808.domain.entities.LCAPUser; 
import com.dogfood.aa20240808.domain.entities.Warehouse_levelTwoEntity; 
import com.dogfood.aa20240808.domain.entities.OtherReturnStorageEntity; 
import com.dogfood.aa20240808.domain.entities.OtherInStorageDetailsEntity; 
import com.dogfood.aa20240808.domain.entities.OtherInStorageEntity; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class AnonymousStructure_F2A18FEA2AB31F66360F783F068A12BE {

    public OtherReturnStorageEntity otherReturnStorage;
    public OtherInStorageEntity otherInStorage;
    public LCAPUser lCAPUser;
    public OtherReturnStorageDetailsEntity otherReturnStorageDetails;
    public OtherInStorageDetailsEntity otherInStorageDetails;
    public MaterialDetailsEntity materialDetails;
    public MeasuringUnitEntity measuringUnit;
    public Warehouse_levelTwoEntity warehouse_levelTwo;
    public Storage_locationEntity storage_location;
    public OtherReturnStorageEntity getOtherReturnStorage() {
        return otherReturnStorage;
    } 

    public void setOtherReturnStorage(OtherReturnStorageEntity otherReturnStorage) {
        this.otherReturnStorage = otherReturnStorage; 
    } 

    public OtherInStorageEntity getOtherInStorage() {
        return otherInStorage;
    } 

    public void setOtherInStorage(OtherInStorageEntity otherInStorage) {
        this.otherInStorage = otherInStorage; 
    } 

    public LCAPUser getLCAPUser() {
        return lCAPUser;
    } 

    public void setLCAPUser(LCAPUser lCAPUser) {
        this.lCAPUser = lCAPUser; 
    } 

    public OtherReturnStorageDetailsEntity getOtherReturnStorageDetails() {
        return otherReturnStorageDetails;
    } 

    public void setOtherReturnStorageDetails(OtherReturnStorageDetailsEntity otherReturnStorageDetails) {
        this.otherReturnStorageDetails = otherReturnStorageDetails; 
    } 

    public OtherInStorageDetailsEntity getOtherInStorageDetails() {
        return otherInStorageDetails;
    } 

    public void setOtherInStorageDetails(OtherInStorageDetailsEntity otherInStorageDetails) {
        this.otherInStorageDetails = otherInStorageDetails; 
    } 

    public MaterialDetailsEntity getMaterialDetails() {
        return materialDetails;
    } 

    public void setMaterialDetails(MaterialDetailsEntity materialDetails) {
        this.materialDetails = materialDetails; 
    } 

    public MeasuringUnitEntity getMeasuringUnit() {
        return measuringUnit;
    } 

    public void setMeasuringUnit(MeasuringUnitEntity measuringUnit) {
        this.measuringUnit = measuringUnit; 
    } 

    public Warehouse_levelTwoEntity getWarehouse_levelTwo() {
        return warehouse_levelTwo;
    } 

    public void setWarehouse_levelTwo(Warehouse_levelTwoEntity warehouse_levelTwo) {
        this.warehouse_levelTwo = warehouse_levelTwo; 
    } 

    public Storage_locationEntity getStorage_location() {
        return storage_location;
    } 

    public void setStorage_location(Storage_locationEntity storage_location) {
        this.storage_location = storage_location; 
    } 


}

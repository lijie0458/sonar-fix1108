package com.dogfood.aa20240808.domain.structure.anonymous; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.math.BigDecimal; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class AnonymousStructure_0D09C006DE97D933B2138BB4B7F9750C {

    public Long id;
    public String materialCode;
    public String material_name;
    public String specification;
    public String unitName;
    public BigDecimal total;
    public Long getId() {
        return id;
    } 

    public void setId(Long id) {
        this.id = id; 
    } 

    public String getMaterialCode() {
        return materialCode;
    } 

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode; 
    } 

    public String getMaterial_name() {
        return material_name;
    } 

    public void setMaterial_name(String material_name) {
        this.material_name = material_name; 
    } 

    public String getSpecification() {
        return specification;
    } 

    public void setSpecification(String specification) {
        this.specification = specification; 
    } 

    public String getUnitName() {
        return unitName;
    } 

    public void setUnitName(String unitName) {
        this.unitName = unitName; 
    } 

    public BigDecimal getTotal() {
        return total;
    } 

    public void setTotal(BigDecimal total) {
        this.total = total; 
    } 


}
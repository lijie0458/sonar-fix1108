package com.dogfood.aa20240808.domain.structure.apis.codewave2024; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class PwdSecurityStructure {

    public String pwdCategory;

    public String getPwdCategory() {
        return pwdCategory;
    } 

    public void setPwdCategory(String pwdCategory) {
        this.pwdCategory = pwdCategory; 
    } 

}

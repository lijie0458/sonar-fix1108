package com.dogfood.aa20240808.repository; 

import java.util.List; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_47C167E7217746A55100F50A57F637C0; 
import org.apache.ibatis.annotations.Param; 

public interface LoadDepartmentByUsernameCustomizeServiceMapper {

List<AnonymousStructure_47C167E7217746A55100F50A57F637C0> getAnonymousStructure_47C167E7217746A55100F50A57F637C0(@Param("UserName") String UserName); 

Long countAnonymousStructure_47C167E7217746A55100F50A57F637C0(@Param("UserName") String UserName); 

}

package com.dogfood.aa20240808.repository; 

import java.util.List; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_FF3D47421C85AD91C2FDAE6821984202; 
import org.apache.ibatis.annotations.Param; 

public interface LCAPGetDeptListCustomizeServiceMapper {

List<AnonymousStructure_FF3D47421C85AD91C2FDAE6821984202> getAnonymousStructure_FF3D47421C85AD91C2FDAE6821984202(@Param("deptId") String deptId); 

List<AnonymousStructure_FF3D47421C85AD91C2FDAE6821984202> getAnonymousStructure_FF3D47421C85AD91C2FDAE68219842021(); 

}
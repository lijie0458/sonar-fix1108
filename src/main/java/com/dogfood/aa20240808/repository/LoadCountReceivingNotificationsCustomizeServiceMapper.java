package com.dogfood.aa20240808.repository; 

import java.util.List; 
import org.apache.ibatis.annotations.Param; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_F6C9B32C78FC00A2E7FD3832ABA02CCB; 

public interface LoadCountReceivingNotificationsCustomizeServiceMapper {

List<AnonymousStructure_F6C9B32C78FC00A2E7FD3832ABA02CCB> getSql1(@Param("DateTime") String DateTime); 

}

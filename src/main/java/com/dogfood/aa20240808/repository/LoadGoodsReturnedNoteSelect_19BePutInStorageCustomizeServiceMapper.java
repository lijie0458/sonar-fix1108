package com.dogfood.aa20240808.repository; 

import java.util.List; 
import org.apache.ibatis.annotations.Param; 

public interface LoadGoodsReturnedNoteSelect_19BePutInStorageCustomizeServiceMapper {

List<com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_611269E21EE6FE18529B487D4B579CBA> getAnonymousStructure_611269E21EE6FE18529B487D4B579CBA(@Param("lcap_71860C77_page") Long lcap_71860C77_page, @Param("lcap_F7BD60B7_size") Long lcap_F7BD60B7_size); 

Long countAnonymousStructure_611269E21EE6FE18529B487D4B579CBA(@Param("lcap_71860C77_page") Long lcap_71860C77_page, @Param("lcap_F7BD60B7_size") Long lcap_F7BD60B7_size); 


}

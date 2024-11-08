package com.dogfood.aa20240808.repository.process;

import org.apache.ibatis.annotations.Param;

public interface ACTGEPROPERTYMapper {

    String selectValueByName(@Param("name") String name);

    void insert(@Param("name") String name, @Param("value") String value, @Param("rev") Long rev);

    void updateValueByName(@Param("name") String name, @Param("value") String value);
}

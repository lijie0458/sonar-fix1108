package com.dogfood.aa20240808.repository.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.BigDecimal;
import com.dogfood.aa20240808.domain.entities.ModuleTableEntity;
import com.dogfood.aa20240808.repository.ReferenceHandleMapper;
import com.dogfood.aa20240808.service.dto.filters.AbstractQueryFilter;
import org.apache.ibatis.annotations.Param;
/**
* auto generate ModuleTableEntity Mapper
*
* @author sys
*/
public interface ModuleTableEntityMapper extends ReferenceHandleMapper {

    int insert(ModuleTableEntity bean);

    int batchInsert(List<ModuleTableEntity> beans);

    int updateBy(ModuleTableEntity bean, List<String> updateFields, AbstractQueryFilter filter);

    int deleteBy(@Param("filter") AbstractQueryFilter filter);

    int createOrUpdate(ModuleTableEntity bean);

    int update(ModuleTableEntity bean, List<String> updateFields);

    int batchUpdate(List<ModuleTableEntity> beans, List<String> updateFields);

    int delete(Long id);

    int batchDelete(List<Long> ids);

    ModuleTableEntity selectOne(Long id);


    List<ModuleTableEntity> selectList(@Param("filter") AbstractQueryFilter filter);

    int count(@Param("filter") AbstractQueryFilter filter);
}
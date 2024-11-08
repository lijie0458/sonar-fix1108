package com.dogfood.aa20240808.repository.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.BigDecimal;
import com.dogfood.aa20240808.domain.entities.MaterialEntity;
import com.dogfood.aa20240808.repository.ReferenceHandleMapper;
import com.dogfood.aa20240808.service.dto.filters.AbstractQueryFilter;
import org.apache.ibatis.annotations.Param;
/**
* auto generate MaterialEntity Mapper
*
* @author sys
*/
public interface MaterialEntityMapper extends ReferenceHandleMapper {

    int insert(MaterialEntity bean);

    int batchInsert(List<MaterialEntity> beans);

    int updateBy(MaterialEntity bean, List<String> updateFields, AbstractQueryFilter filter);

    int deleteBy(@Param("filter") AbstractQueryFilter filter);

    int createOrUpdate(MaterialEntity bean);

    int update(MaterialEntity bean, List<String> updateFields);

    int batchUpdate(List<MaterialEntity> beans, List<String> updateFields);

    int delete(Long id);

    int batchDelete(List<Long> ids);

    MaterialEntity selectOne(Long id);


    List<MaterialEntity> selectList(@Param("filter") AbstractQueryFilter filter);

    int count(@Param("filter") AbstractQueryFilter filter);
}
package com.dogfood.aa20240808.repository.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.BigDecimal;
import com.dogfood.aa20240808.domain.entities.AppCacheEntity;
import com.dogfood.aa20240808.repository.ReferenceHandleMapper;
import com.dogfood.aa20240808.service.dto.filters.AbstractQueryFilter;
import org.apache.ibatis.annotations.Param;
/**
* auto generate AppCacheEntity Mapper
*
* @author sys
*/
public interface AppCacheEntityMapper extends ReferenceHandleMapper {

    int insert(AppCacheEntity bean);

    int batchInsert(List<AppCacheEntity> beans);

    int updateBy(AppCacheEntity bean, List<String> updateFields, AbstractQueryFilter filter);

    int deleteBy(@Param("filter") AbstractQueryFilter filter);

    int createOrUpdate(AppCacheEntity bean);

    int update(AppCacheEntity bean, List<String> updateFields);

    int batchUpdate(List<AppCacheEntity> beans, List<String> updateFields);

    int delete(Long id);

    int batchDelete(List<Long> ids);

    AppCacheEntity selectOne(Long id);


    List<AppCacheEntity> selectList(@Param("filter") AbstractQueryFilter filter);

    int count(@Param("filter") AbstractQueryFilter filter);
}
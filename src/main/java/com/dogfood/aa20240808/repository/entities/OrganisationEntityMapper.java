package com.dogfood.aa20240808.repository.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.BigDecimal;
import com.dogfood.aa20240808.domain.entities.OrganisationEntity;
import com.dogfood.aa20240808.repository.ReferenceHandleMapper;
import com.dogfood.aa20240808.service.dto.filters.AbstractQueryFilter;
import org.apache.ibatis.annotations.Param;
/**
* auto generate OrganisationEntity Mapper
*
* @author sys
*/
public interface OrganisationEntityMapper extends ReferenceHandleMapper {

    int insert(OrganisationEntity bean);

    int batchInsert(List<OrganisationEntity> beans);

    int updateBy(OrganisationEntity bean, List<String> updateFields, AbstractQueryFilter filter);

    int deleteBy(@Param("filter") AbstractQueryFilter filter);

    int createOrUpdate(OrganisationEntity bean);

    int update(OrganisationEntity bean, List<String> updateFields);

    int batchUpdate(List<OrganisationEntity> beans, List<String> updateFields);

    int delete(Long id);

    int batchDelete(List<Long> ids);

    OrganisationEntity selectOne(Long id);


    List<OrganisationEntity> selectList(@Param("filter") AbstractQueryFilter filter);

    int count(@Param("filter") AbstractQueryFilter filter);
}
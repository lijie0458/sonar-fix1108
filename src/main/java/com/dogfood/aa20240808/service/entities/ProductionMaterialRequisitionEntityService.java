package com.dogfood.aa20240808.service.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.apache.commons.lang3.StringUtils;

import com.dogfood.aa20240808.context.UserContext;
import com.dogfood.aa20240808.domain.entities.ProductionMaterialRequisitionEntity;
import com.dogfood.aa20240808.domain.enumeration.*;
import com.dogfood.aa20240808.domain.enumeration.ApprovalStatusEnumEnum;
import com.dogfood.aa20240808.domain.enumeration.DistinctionEnumEnum;
import com.dogfood.aa20240808.domain.structure.*;
import com.dogfood.aa20240808.exception.HttpCodeException;
import com.dogfood.aa20240808.repository.entities.ProductionMaterialRequisitionEntityMapper;
import com.dogfood.aa20240808.service.dto.filters.*;
import com.dogfood.aa20240808.service.dto.filters.atomic.*;
import com.dogfood.aa20240808.service.dto.filters.logic.binary.matching.*;
import com.dogfood.aa20240808.service.dto.filters.logic.unary.*;
import com.dogfood.aa20240808.service.entities.AbstractService;
import com.dogfood.aa20240808.service.entities.inner.RelationInnerService;
import com.dogfood.aa20240808.util.CommonFunctionUtil;
import com.dogfood.aa20240808.util.ExcelUtil;
import com.dogfood.aa20240808.util.SnowflakeIdWorker;
import java.time.LocalDate;
/**
* auto generate ProductionMaterialRequisitionEntityService ServiceImpl
*
* @author sys
*/
@Service
public class ProductionMaterialRequisitionEntityService extends AbstractService {
    @Resource
    private ProductionMaterialRequisitionEntityMapper mapper;
    @Resource
    private RelationInnerService relationInnerService;
    @Resource
    private ProductionMaterialRequisitionEntityService entityService;

    private Map<String, String> entityFieldNameTitleMap = new LinkedHashMap<String, String>();
    private Map<String, String> entityFieldTitleNameMap = new LinkedHashMap<String, String>();
    private Map<String, String> entityFiledColumnNameMap = new LinkedHashMap<>();

    public ProductionMaterialRequisitionEntityService() {
        entityFieldTitleNameMap.put("单据编号", "documentNumber");
        entityFiledColumnNameMap.put("documentNumber", "document_number");
        entityFieldTitleNameMap.put("领料部门", "department");
        entityFiledColumnNameMap.put("department", "department");
        entityFieldTitleNameMap.put("领料员", "employee");
        entityFiledColumnNameMap.put("employee", "employee");
        entityFieldTitleNameMap.put("申请日期", "requestDate");
        entityFiledColumnNameMap.put("requestDate", "request_date");
        entityFieldTitleNameMap.put("摘要", "summary");
        entityFiledColumnNameMap.put("summary", "summary");
        entityFieldTitleNameMap.put("附件", "attachment");
        entityFiledColumnNameMap.put("attachment", "attachment");
        entityFieldTitleNameMap.put("审批状态", "approvalStatus");
        entityFiledColumnNameMap.put("approvalStatus", "approval_status");
        entityFieldTitleNameMap.put("区别字段（领料/退料）", "issueReturnDistinction");
        entityFiledColumnNameMap.put("issueReturnDistinction", "issue_return_distinction");
        entityFieldTitleNameMap.put("保管员", "guardian");
        entityFiledColumnNameMap.put("guardian", "guardian");
        entityFieldTitleNameMap.put("创建者", "createdBy");
        entityFiledColumnNameMap.put("createdBy", "created_by");
        entityFieldTitleNameMap.put("领料单号", "sourceDocumentNumber");
        entityFiledColumnNameMap.put("sourceDocumentNumber", "source_document_number");
        for (String fieldName : entityFieldNameTitleMap.keySet()) {
            String fieldTitle = entityFieldNameTitleMap.get(fieldName);
            entityFieldNameTitleMap.put(fieldName, fieldTitle);
        }
    }

    /**
    * auto gen list method
    **/
    public List<ProductionMaterialRequisitionEntity> list(AbstractQueryFilter queryFilter) {
        if (null == queryFilter) {
            queryFilter = new UnaryExpressionFilter();
        }
        CommonFunctionUtil.preHandleQueryExpression(queryFilter, entityFiledColumnNameMap);
        return mapper.selectList(queryFilter);
    }

    /**
    * auto gen count method
    **/
    public long count(AbstractQueryFilter queryFilter) {
        if (null == queryFilter) {
            queryFilter = new UnaryExpressionFilter();
        }
        CommonFunctionUtil.preHandleQueryExpression(queryFilter, entityFiledColumnNameMap);
        return mapper.count(queryFilter);
    }

    /**
    * auto gen export method
    **/
    public ResponseEntity<org.springframework.core.io.Resource> export(AbstractQueryFilter queryFilter, String fields, HttpServletRequest request) {
        try {
            Map<String, String> exportFieldMap = entityFieldNameTitleMap;
            if (fields != null && !"".equals(fields.trim())) {
                for (String filedName : fields.split(",")) {
                    exportFieldMap = new LinkedHashMap<String, String>();
                    exportFieldMap.put(filedName, entityFieldNameTitleMap.get(filedName));
                }
            }

            List<ProductionMaterialRequisitionEntity> data = list(queryFilter);
            String storeFilePath = ExcelUtil.write(data, ProductionMaterialRequisitionEntity.class, exportFieldMap);
            org.springframework.core.io.Resource resource = null;
            String contentType = null;
            resource = new FileUrlResource(storeFilePath);
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ProductionMaterialRequisitionEntity.class.getSimpleName() + ".xlsx\"")
                .body(resource);
        } catch (Exception e) {
            throw new HttpCodeException(500, e);
        }
    }

        /**
         * auto gen get method
         **/
        public ProductionMaterialRequisitionEntity get( String documentNumber ) { 
            if ( documentNumber == null ) { 
                throw new HttpCodeException(400, ErrorCodeEnum.PARAM_PRIMARY_KEY_REQUIRED.code);
            }

            ProductionMaterialRequisitionEntity entity = mapper.selectOne( documentNumber ); 

            if (null == entity) {
                throw new HttpCodeException(404, ErrorCodeEnum.DATA_NOT_EXIST.code);
            }
            return entity;
        }

    /**
    * auto gen create method
    **/
    @Transactional(rollbackFor = Exception.class)
    public ProductionMaterialRequisitionEntity create(ProductionMaterialRequisitionEntity entity) {
        if (null == entity) {
            throw new HttpCodeException(400, "create param is required");
        }
        // fill default value
        UserContext.UserInfo currentUserInfo = UserContext.getCurrentUserInfo();
        String currentUserName = null == currentUserInfo ? null : currentUserInfo.getUserName();
        entity.setCreatedBy(StringUtils.isBlank(entity.getCreatedBy()) ? currentUserName : entity.getCreatedBy());
        paramValidate(entity);
        mapper.insert(entity);
        return entity;
    }

    /**
    * auto gen batch create method
    **/
    @Transactional(rollbackFor = Exception.class)
    public List<ProductionMaterialRequisitionEntity> batchCreate(List<ProductionMaterialRequisitionEntity> entities) {
        if (null == entities || entities.isEmpty()) {
            throw new HttpCodeException(400, ErrorCodeEnum.PARAM_NOTHING_TODO.code);
        }

        UserContext.UserInfo currentUserInfo = UserContext.getCurrentUserInfo();
        String currentUserName = null == currentUserInfo ? null : currentUserInfo.getUserName();

        // fill default value
        List<ProductionMaterialRequisitionEntity> batchList = new ArrayList<>(100);
        for (ProductionMaterialRequisitionEntity entity : entities) {
            entity.setCreatedBy(StringUtils.isBlank(entity.getCreatedBy()) ? currentUserName : entity.getCreatedBy());
            paramValidate(entity);
            if (batchList.size() >= 100) {
                mapper.batchInsert(batchList);
                batchList.clear();
            }
            batchList.add(entity);
        }
        if (batchList.size() > 0) {
            mapper.batchInsert(batchList);
        }
        return entities;
    }

    public void beforeUpdate(ProductionMaterialRequisitionEntity entity) {
        if (null == entity) {
            throw new HttpCodeException(400, ErrorCodeEnum.PARAM_REQUIRED.code, "ProductionMaterialRequisitionEntity");
        }
        if ( entity.getDocumentNumber() == null ) { 
            throw new HttpCodeException(400, ErrorCodeEnum.PARAM_PRIMARY_KEY_REQUIRED.code);
        }
    }

    /**
    * auto gen update method
    **/
    @Transactional
    public ProductionMaterialRequisitionEntity update(ProductionMaterialRequisitionEntity entity, List<String> updateFields) {
        beforeUpdate(entity);

        // updateFields为null时，默认全量更新
        if (null != updateFields && updateFields.size() == 1 &&  updateFields.contains("documentNumber")) {
            return entity;
        } else {
            int rows = mapper.update(entity, updateFields);
            if (rows <= 0) {
                throw new HttpCodeException(404, ErrorCodeEnum.DATA_NOT_EXIST.code, entity.getClass().getName());
            }

            return get(entity.getDocumentNumber());
        }
    }

    /**
    * auto gen batchUpdate method
    **/
    @Transactional(rollbackFor = Exception.class)
    public List<ProductionMaterialRequisitionEntity> batchUpdate(List<ProductionMaterialRequisitionEntity> entities, List<String> updateFields) {
        if (null == entities || entities.isEmpty()) {
            throw new HttpCodeException(400, ErrorCodeEnum.PARAM_NOTHING_TODO.code);
        }

        if (updateFields != null && updateFields.size() == 1 && updateFields.contains("documentNumber")) {
            // 进行局部更新的字段是主键，这种情况是没意义，直接返回就好
            return entities;
        }
        UserContext.UserInfo currentUserInfo = UserContext.getCurrentUserInfo();
        String currentUserName = null == currentUserInfo ? null : currentUserInfo.getUserName();
        List<ProductionMaterialRequisitionEntity> batchList = new ArrayList<>(100);
        for (ProductionMaterialRequisitionEntity entity : entities) {
            if (entity.getDocumentNumber() == null ) {
                throw new HttpCodeException(400, ErrorCodeEnum.PARAM_PRIMARY_KEY_REQUIRED.code);
            }

            batchList.add(entity);
            if (batchList.size() >= 100) {
                mapper.batchUpdate(batchList, updateFields);
                batchList.clear();
            }
        }

        if (batchList.size() > 0) {
            mapper.batchUpdate(batchList, updateFields);
            batchList.clear();
        }

        return entities;
    }

    /**
    * auto gen delete method
    **/
    @Transactional(rollbackFor = Exception.class)
    public long delete( String documentNumber ) { 
        if ( documentNumber == null ) { 
            throw new HttpCodeException(400, ErrorCodeEnum.PARAM_PRIMARY_KEY_REQUIRED.code);
        }
        ProductionMaterialRequisitionEntity entity = mapper.selectOne( documentNumber ); 

        if (null == entity) {
            return 0;
        }
        relationInnerService.onDelete(entity);
        return mapper.delete( documentNumber ); 
    }

    /**
    * auto gen batch delete method
    **/
    @Transactional(rollbackFor = Exception.class)
    public long batchDelete(List<String> ids) {
        if (null == ids || ids.isEmpty()) {
            throw new HttpCodeException(400, ErrorCodeEnum.PARAM_NOTHING_TODO.code);
        }

        AbstractQueryFilter inQueryFilter = new InQueryFilter()
            .left(new ColumnQueryFilter("ProductionMaterialRequisition", "production_material_requisition", "documentNumber", "document_number"))
            .right(new ListLiteralQueryFilter(ids));
        CommonFunctionUtil.preHandleQueryExpression(inQueryFilter, entityFiledColumnNameMap);
        List<ProductionMaterialRequisitionEntity>  entities = mapper.selectList(inQueryFilter);
        for (ProductionMaterialRequisitionEntity entity : entities) {
            relationInnerService.onDelete(entity);
        }

        return mapper.batchDelete(ids);
    }

    /**
    * auto gen importFile method
    **/
    @Transactional(rollbackFor = Exception.class)
    public String importFile(MultipartFile file) {
        String type;
        String[] items = "\\.".split(Objects.requireNonNull(file.getOriginalFilename()));
        if (items.length > 1) {
            type = items[items.length - 1];
            if (!"xls".equalsIgnoreCase(type) && !"xlsx".equalsIgnoreCase(type)) {
                return "不支持的文件类型";
            }
        } else {
            return "不支持的文件类型";
        }

        try {
            List<ProductionMaterialRequisitionEntity> data = ExcelUtil.read(file.getInputStream(), type, ProductionMaterialRequisitionEntity.class, entityFieldTitleNameMap);
            entityService.batchCreate(data);
            return "ok";
        } catch (Exception e) {
            throw new HttpCodeException(500, e);
        }
    }
    /**
    * auto gen createOrUpdate method
    **/
    @Transactional(rollbackFor = Exception.class)
    public ProductionMaterialRequisitionEntity createOrUpdate(ProductionMaterialRequisitionEntity entity, List<String> updateFields) {
        if (null == entity) {
            throw new HttpCodeException(400, ErrorCodeEnum.PARAM_REQUIRED.code, ProductionMaterialRequisitionEntity.class);
        }

        if ( entity.getDocumentNumber() == null ) { 
            // insert
            entity = entityService.create(entity);
        }  else {
            ProductionMaterialRequisitionEntity existEntity = mapper.selectOne(entity.getDocumentNumber()); 
            if (null == existEntity) {
                // insert
                UserContext.UserInfo currentUserInfo = UserContext.getCurrentUserInfo();
                String currentUserName = null == currentUserInfo ? null : currentUserInfo.getUserName();
                entity.setCreatedBy(StringUtils.isBlank(entity.getCreatedBy()) ? currentUserName : entity.getCreatedBy());
                paramValidate(entity);
                mapper.createOrUpdate(entity);
            } else {
                // updateFields为null时，默认全量更新
                entity = entityService.update(entity, updateFields);
            }
        }
        return entity;
    }

    /**
    * auto gen updateBy method
    **/
    @Transactional(rollbackFor = Exception.class)
    public long updateBy(ProductionMaterialRequisitionEntity entity, List<String> updateFields, AbstractQueryFilter filter) {

        if (null == entity) {
            throw new HttpCodeException(400, ErrorCodeEnum.PARAM_REQUIRED.code, ProductionMaterialRequisitionEntity.class);
        }

        // updateFields为null时，默认全量更新
        if (null == filter) {
            throw new HttpCodeException(400, ErrorCodeEnum.PARAM_REQUIRED.code, AbstractQueryFilter.class);
        }

        if (null != updateFields && updateFields.size() == 1 &&  updateFields.contains("documentNumber")) {
            return 0;
        } else {
            UserContext.UserInfo currentUserInfo = UserContext.getCurrentUserInfo();
            String currentUserName = null == currentUserInfo ? null : currentUserInfo.getUserName();
            CommonFunctionUtil.preHandleQueryExpression(filter, entityFiledColumnNameMap);
            entity.setCreatedBy(StringUtils.isBlank(entity.getCreatedBy()) ? currentUserName : entity.getCreatedBy());
            return mapper.updateBy(entity, updateFields, filter);
        }
    }

    /**
    * auto gen deleteBy method
    **/
    @Transactional(rollbackFor = Exception.class)
    public long deleteBy(AbstractQueryFilter filter) {
        if (null == filter) {
            throw new HttpCodeException(400, ErrorCodeEnum.PARAM_REQUIRED.code, AbstractQueryFilter.class);
        }

        CommonFunctionUtil.preHandleQueryExpression(filter, entityFiledColumnNameMap);
        return mapper.deleteBy(filter);
    }
}
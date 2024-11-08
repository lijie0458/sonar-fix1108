package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import java.util.ArrayList; 
import java.util.List; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_38593AA815D055EA90DAB07FE4542F93; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_D99966CE0A06FAAEEADD3A36C43F7DFF; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import com.dogfood.aa20240808.util.LogicCallUtils; 
import com.dogfood.aa20240808.repository.PERGetPerResMappingByPermissionIdCustomizeServiceMapper; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.config.Constants; 

/**
 * PER-根据权限ID查询已添加的资源
 * 
 * @author sys
 * 
 * @date 2024-11-8 10:18
 * 
 * @version 1.0
 * 
 * @BelongsProject mybatis审计日志
 * 
 * @BelongsPackage src/main/java/com/dogfood/aa20240808/service/logics
 */
@Service
public class PERGetPerResMappingByPermissionIdCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private PERGetPerResMappingByPermissionIdCustomizeServiceMapper pERGetPerResMappingByPermissionIdCustomizeServiceMapper;

    public List<AnonymousStructure_D99966CE0A06FAAEEADD3A36C43F7DFF> pERGetPerResMappingByPermissionId(Long permissionId) {
        List<AnonymousStructure_D99966CE0A06FAAEEADD3A36C43F7DFF> result = new ArrayList<>();
        if ((CommonFunctionUtil.notEquals(permissionId, null))) {
            result = CommonFunctionUtil.createListPage(pERGetPerResMappingByPermissionIdCustomizeServiceMapper.getAnonymousStructure_D99966CE0A06FAAEEADD3A36C43F7DFF(permissionId), pERGetPerResMappingByPermissionIdCustomizeServiceMapper.countAnonymousStructure_D99966CE0A06FAAEEADD3A36C43F7DFF(permissionId).intValue(), AnonymousStructure_38593AA815D055EA90DAB07FE4542F93.class).list; 
        } else {
            LCAP_LOGGER.error(CommonFunctionUtil.toString("传入参数permissionId为null"));
            LCAP_LOGGER.info("abort!"); 
            LogicCallUtils.abort(); 
        } 

        return result;
    } 

}

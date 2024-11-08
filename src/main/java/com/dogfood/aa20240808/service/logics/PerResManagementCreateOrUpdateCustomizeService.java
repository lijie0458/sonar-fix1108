package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import java.util.ArrayList; 
import com.dogfood.aa20240808.service.entities.LCAPPerResMappingService; 
import com.dogfood.aa20240808.service.entities.LCAPPermissionService; 
import com.dogfood.aa20240808.domain.entities.LCAPPerResMapping; 
import java.util.List; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_38593AA815D055EA90DAB07FE4542F93; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_D99966CE0A06FAAEEADD3A36C43F7DFF; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import com.dogfood.aa20240808.domain.entities.LCAPResource; 
import org.springframework.transaction.annotation.Transactional; 
import org.slf4j.Logger; 
import com.dogfood.aa20240808.repository.PerResManagementCreateOrUpdateCustomizeServiceMapper; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.config.Constants; 
import com.dogfood.aa20240808.domain.entities.LCAPPermission; 

/**
 * 权限资源管理页面的创建与更新功能-ljl
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
public class PerResManagementCreateOrUpdateCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LCAPPermissionService lCAPPermissionService;

    @Autowired
    private PerResManagementCreateOrUpdateCustomizeServiceMapper perResManagementCreateOrUpdateCustomizeServiceMapper;

    @Autowired
    private LCAPPerResMappingService lCAPPerResMappingService;

    @Transactional()
    public void perResManagementCreateOrUpdate(LCAPPermission input, Boolean isUpdate, List<LCAPResource> selectedResData) {
        Long perId = 0L;
        LCAPPerResMapping variable1 = new LCAPPerResMapping();
        List<LCAPPerResMapping> variable2 = new ArrayList<>();
        List<AnonymousStructure_D99966CE0A06FAAEEADD3A36C43F7DFF> variable3 = new ArrayList<>();
        if (isUpdate) {
            perId = lCAPPermissionService.update(input, null).id; 
            // 存储要删除的数据
            variable3 = CommonFunctionUtil.createListPage(perResManagementCreateOrUpdateCustomizeServiceMapper.getAnonymousStructure_D99966CE0A06FAAEEADD3A36C43F7DFF(perId), perResManagementCreateOrUpdateCustomizeServiceMapper.countAnonymousStructure_D99966CE0A06FAAEEADD3A36C43F7DFF(perId).intValue(), AnonymousStructure_38593AA815D055EA90DAB07FE4542F93.class).list; 
        } else {
            perId = lCAPPermissionService.create(input).id; 
        } 

        for (Long index = 0L; index < selectedResData.size(); index++) {
            LCAPResource item = selectedResData.get(index.intValue());
            variable1.permissionId = perId; 
            variable1.resourceId = item.id; 
            CommonFunctionUtil.add(variable2, CommonFunctionUtil.clone(variable1));
        } 

        if ((CommonFunctionUtil.length(variable2).compareTo(0L) > 0)) {
            lCAPPerResMappingService.batchCreate(variable2);
        } else {
        } 

        if (isUpdate) {
            // 创建数据后再删除，防止提前删除页面权限断开
            if ((CommonFunctionUtil.length(variable3).compareTo(0L) > 0)) {
                lCAPPerResMappingService.batchDelete(CommonFunctionUtil.listTransform(variable3, (item) -> item.lCAPPerResMapping.id));
            } else {
            } 

        } else {
        } 

        return ;
    } 

}

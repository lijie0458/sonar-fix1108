package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_239C4CA8D6FC913CFC0B92C26D945258; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import com.dogfood.aa20240808.repository.LoadUserManagementSelect_1DepartmentCustomizeServiceMapper; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.config.Constants; 

/**
 * 
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
public class LoadUserManagementSelect_1DepartmentCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LoadUserManagementSelect_1DepartmentCustomizeServiceMapper loadUserManagementSelect_1DepartmentCustomizeServiceMapper;

    public AnonymousStructure_239C4CA8D6FC913CFC0B92C26D945258 loadUserManagementSelect_1Department(Long page, Long size) {
        AnonymousStructure_239C4CA8D6FC913CFC0B92C26D945258 result = new AnonymousStructure_239C4CA8D6FC913CFC0B92C26D945258();
        result = CommonFunctionUtil.createListPage(loadUserManagementSelect_1DepartmentCustomizeServiceMapper.getAnonymousStructure_3421ED028B001C489CD794910A15D0C0(page, size), loadUserManagementSelect_1DepartmentCustomizeServiceMapper.countAnonymousStructure_3421ED028B001C489CD794910A15D0C0(page, size).intValue(), AnonymousStructure_239C4CA8D6FC913CFC0B92C26D945258.class); 
        return result;
    } 

}

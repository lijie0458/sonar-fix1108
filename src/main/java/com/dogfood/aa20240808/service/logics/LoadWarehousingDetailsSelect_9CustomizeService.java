package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_239C4CA8D6FC913CFC0B92C26D945258; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import com.dogfood.aa20240808.repository.LoadWarehousingDetailsSelect_9CustomizeServiceMapper; 
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
public class LoadWarehousingDetailsSelect_9CustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LoadWarehousingDetailsSelect_9CustomizeServiceMapper loadWarehousingDetailsSelect_9CustomizeServiceMapper;

    public AnonymousStructure_239C4CA8D6FC913CFC0B92C26D945258 loadWarehousingDetailsSelect_9(Long page, Long size) {
        AnonymousStructure_239C4CA8D6FC913CFC0B92C26D945258 result = new AnonymousStructure_239C4CA8D6FC913CFC0B92C26D945258();
        result = CommonFunctionUtil.createListPage(loadWarehousingDetailsSelect_9CustomizeServiceMapper.getAnonymousStructure_3421ED028B001C489CD794910A15D0C0(page, size), loadWarehousingDetailsSelect_9CustomizeServiceMapper.countAnonymousStructure_3421ED028B001C489CD794910A15D0C0(page, size).intValue(), AnonymousStructure_239C4CA8D6FC913CFC0B92C26D945258.class); 
        return result;
    } 

}

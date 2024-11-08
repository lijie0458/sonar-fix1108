package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import com.dogfood.aa20240808.repository.LoadWarehouseInfoCustomizeServiceMapper; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.config.Constants; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_D68ADEA2FD6A42F4478E1BA20619B4E3; 

/**
 * 拿到仓库信息-cmk
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
public class LoadWarehouseInfoCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LoadWarehouseInfoCustomizeServiceMapper loadWarehouseInfoCustomizeServiceMapper;

    public AnonymousStructure_D68ADEA2FD6A42F4478E1BA20619B4E3 loadWarehouseInfo() {
        AnonymousStructure_D68ADEA2FD6A42F4478E1BA20619B4E3 result = new AnonymousStructure_D68ADEA2FD6A42F4478E1BA20619B4E3();
        result = CommonFunctionUtil.createListPage(loadWarehouseInfoCustomizeServiceMapper.getAnonymousStructure_ABA4D5CA228B2CA9185E2A3A875C5FA2(), loadWarehouseInfoCustomizeServiceMapper.countAnonymousStructure_ABA4D5CA228B2CA9185E2A3A875C5FA2().intValue(), AnonymousStructure_D68ADEA2FD6A42F4478E1BA20619B4E3.class); 
        return result;
    } 

}

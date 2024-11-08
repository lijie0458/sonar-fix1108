package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import java.util.List; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_ABB51C5744639B1F415A73CB19AE729B; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import com.dogfood.aa20240808.repository.LoadSelectInventoryByCodeAndCustomizeServiceMapper; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.config.Constants; 

/**
 * 通过仓库和物料名称查询库存明细
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
public class LoadSelectInventoryByCodeAndCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LoadSelectInventoryByCodeAndCustomizeServiceMapper loadSelectInventoryByCodeAndCustomizeServiceMapper;

    public AnonymousStructure_ABB51C5744639B1F415A73CB19AE729B loadSelectInventoryByCodeAnd(Long warehouse, List<String> Code, List<String> batchNumber) {
        AnonymousStructure_ABB51C5744639B1F415A73CB19AE729B result = new AnonymousStructure_ABB51C5744639B1F415A73CB19AE729B();
        result = CommonFunctionUtil.createListPage(loadSelectInventoryByCodeAndCustomizeServiceMapper.getAnonymousStructure_D064F9FB058DBE7CA5880F9FE42F0344(warehouse, Code, batchNumber), loadSelectInventoryByCodeAndCustomizeServiceMapper.countAnonymousStructure_D064F9FB058DBE7CA5880F9FE42F0344(warehouse, Code, batchNumber).intValue(), AnonymousStructure_ABB51C5744639B1F415A73CB19AE729B.class); 
        return result;
    } 

}

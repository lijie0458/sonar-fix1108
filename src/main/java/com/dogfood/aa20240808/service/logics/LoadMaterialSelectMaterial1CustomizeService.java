package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_5F55CB4B9BA2141C1A7AB94E4A6BCF0C; 
import com.dogfood.aa20240808.repository.LoadMaterialSelectMaterial1CustomizeServiceMapper; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
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
public class LoadMaterialSelectMaterial1CustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LoadMaterialSelectMaterial1CustomizeServiceMapper loadMaterialSelectMaterial1CustomizeServiceMapper;

    public AnonymousStructure_5F55CB4B9BA2141C1A7AB94E4A6BCF0C loadMaterialSelectMaterial1() {
        AnonymousStructure_5F55CB4B9BA2141C1A7AB94E4A6BCF0C result = new AnonymousStructure_5F55CB4B9BA2141C1A7AB94E4A6BCF0C();
        result = CommonFunctionUtil.createListPage(loadMaterialSelectMaterial1CustomizeServiceMapper.getAnonymousStructure_988572FC06FE621688549C0BC53D9030(), loadMaterialSelectMaterial1CustomizeServiceMapper.countAnonymousStructure_988572FC06FE621688549C0BC53D9030().intValue(), AnonymousStructure_5F55CB4B9BA2141C1A7AB94E4A6BCF0C.class); 
        return result;
    } 

}

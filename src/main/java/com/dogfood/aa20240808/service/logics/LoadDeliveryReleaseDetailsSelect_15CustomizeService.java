package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import com.dogfood.aa20240808.repository.LoadDeliveryReleaseDetailsSelect_15CustomizeServiceMapper; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_0F432FDC73BF41FE20EF251CF1801606; 
import com.dogfood.aa20240808.domain.entities.Storage_locationEntity; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.config.Constants; 

/**
 * 发货通知时选择仓库后查询对应仓库下的库位
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
public class LoadDeliveryReleaseDetailsSelect_15CustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LoadDeliveryReleaseDetailsSelect_15CustomizeServiceMapper loadDeliveryReleaseDetailsSelect_15CustomizeServiceMapper;

    public Storage_locationEntity loadDeliveryReleaseDetailsSelect_15(Long warehouseId) {
        Storage_locationEntity result = new Storage_locationEntity();
        result = CommonFunctionUtil.get(CommonFunctionUtil.createListPage(loadDeliveryReleaseDetailsSelect_15CustomizeServiceMapper.getAnonymousStructure_D8ABCE1399E60C92DC80402C8BAAD8EB(warehouseId), loadDeliveryReleaseDetailsSelect_15CustomizeServiceMapper.countAnonymousStructure_D8ABCE1399E60C92DC80402C8BAAD8EB(warehouseId).intValue(), AnonymousStructure_0F432FDC73BF41FE20EF251CF1801606.class).list, 0L).storage_location; 
        return result;
    } 

}

package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import com.dogfood.aa20240808.repository.LoadCountPurchaseorderCustomizeServiceMapper; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.config.Constants; 

/**
 * 查询当前申请条数，创建采购订单用作单号
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
public class LoadCountPurchaseorderCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LoadCountPurchaseorderCustomizeServiceMapper loadCountPurchaseorderCustomizeServiceMapper;

    public String loadCountPurchaseorder() {
        String StringNum = "";
        String result = "";
        StringNum = CommonFunctionUtil.convert((loadCountPurchaseorderCustomizeServiceMapper.getSql1() + 1L), String.class); 
        if ((CommonFunctionUtil.equals(CommonFunctionUtil.length(StringNum), 1L))) {
            result = new StringBuilder().append(CommonFunctionUtil.toString(CommonFunctionUtil.formatDate(CommonFunctionUtil.currDate("global"), "yyyyMMdd"))).append(CommonFunctionUtil.toString("-000")).append(CommonFunctionUtil.toString(StringNum)).toString(); 
        } else if ((CommonFunctionUtil.equals(CommonFunctionUtil.length(StringNum), 2L))) {
            result = new StringBuilder().append(CommonFunctionUtil.toString(CommonFunctionUtil.formatDate(CommonFunctionUtil.currDate("global"), "yyyyMMdd"))).append(CommonFunctionUtil.toString("-00")).append(CommonFunctionUtil.toString(StringNum)).toString(); 
        } else if ((CommonFunctionUtil.equals(CommonFunctionUtil.length(StringNum), 3L))) {
            result = new StringBuilder().append(CommonFunctionUtil.toString(CommonFunctionUtil.formatDate(CommonFunctionUtil.currDate("global"), "yyyyMMdd"))).append(CommonFunctionUtil.toString("-0")).append(CommonFunctionUtil.toString(StringNum)).toString(); 
        } else {
            result = new StringBuilder().append(CommonFunctionUtil.toString(CommonFunctionUtil.formatDate(CommonFunctionUtil.currDate("global"), "yyyyMMdd"))).append(CommonFunctionUtil.toString("-")).append(CommonFunctionUtil.toString(StringNum)).toString(); 
        } 

        return result;
    } 

}
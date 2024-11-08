package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import com.dogfood.aa20240808.repository.IAMJudgeOpenedIdentityCustomizeServiceMapper; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_C9CB86B6B1982E48ADBFCDD820B60594; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.config.Constants; 

/**
 * IAM-判断身份源表中是否存在开启的身份源
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
public class IAMJudgeOpenedIdentityCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private IAMJudgeOpenedIdentityCustomizeServiceMapper iAMJudgeOpenedIdentityCustomizeServiceMapper;

    public Boolean iAMJudgeOpenedIdentity() {
        AnonymousStructure_C9CB86B6B1982E48ADBFCDD820B60594 tmp = new AnonymousStructure_C9CB86B6B1982E48ADBFCDD820B60594();
        Boolean result = false;
        tmp = CommonFunctionUtil.createListPage(iAMJudgeOpenedIdentityCustomizeServiceMapper.getAnonymousStructure_D41D8CD98F00B204E9800998ECF8427E(), iAMJudgeOpenedIdentityCustomizeServiceMapper.countAnonymousStructure_D41D8CD98F00B204E9800998ECF8427E().intValue(), AnonymousStructure_C9CB86B6B1982E48ADBFCDD820B60594.class); 
        if ((CommonFunctionUtil.equals(tmp.total, 0L))) {
            result = false; 
        } else {
            result = true; 
        } 

        return result;
    } 

}

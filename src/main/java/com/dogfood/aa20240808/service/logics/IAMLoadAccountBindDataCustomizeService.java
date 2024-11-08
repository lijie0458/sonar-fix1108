package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import java.util.ArrayList; 
import com.dogfood.aa20240808.repository.IAMLoadAccountBindDataCustomizeServiceMapper; 
import java.util.List; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import com.dogfood.aa20240808.domain.structure.AccountBindStructureStructure; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_E6204F88FA3760A005F0E371750B535E; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.config.Constants; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_22E188A54D4B66D541C0CA978F47CF23; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_4340F7F1981452AE335921E6533B20F0; 

/**
 * IAM-加载账号绑定组件列表数据
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
public class IAMLoadAccountBindDataCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private IAMLoadAccountBindDataCustomizeServiceMapper iAMLoadAccountBindDataCustomizeServiceMapper;

    public List<AccountBindStructureStructure> iAMLoadAccountBindData(String userId) {
        List<AnonymousStructure_E6204F88FA3760A005F0E371750B535E> tmp1 = new ArrayList<>();
        List<Long> tmp2 = new ArrayList<>();
        AccountBindStructureStructure accountBindStructure = new AccountBindStructureStructure();
        List<AccountBindStructureStructure> result = new ArrayList<>();
        tmp1 = CommonFunctionUtil.createListPage(iAMLoadAccountBindDataCustomizeServiceMapper.getAnonymousStructure_E6204F88FA3760A005F0E371750B535E(), iAMLoadAccountBindDataCustomizeServiceMapper.countAnonymousStructure_E6204F88FA3760A005F0E371750B535E().intValue(), AnonymousStructure_22E188A54D4B66D541C0CA978F47CF23.class).list; 
        tmp2 = CommonFunctionUtil.listTransform(CommonFunctionUtil.createListPage(iAMLoadAccountBindDataCustomizeServiceMapper.getAnonymousStructure_CF0A20C3E9EE29F8B74D4541D72927DF(userId), iAMLoadAccountBindDataCustomizeServiceMapper.countAnonymousStructure_CF0A20C3E9EE29F8B74D4541D72927DF(userId).intValue(), AnonymousStructure_4340F7F1981452AE335921E6533B20F0.class).list, (item) -> item.thirdIdentity.identityId); 
        for (Long index = 0L; index < tmp1.size(); index++) {
            AnonymousStructure_E6204F88FA3760A005F0E371750B535E item = tmp1.get(index.intValue());
            accountBindStructure.identitySource = item.identitySourceConfig; 
            if ((CommonFunctionUtil.length(tmp2).compareTo(0L) > 0)) {
                if (CommonFunctionUtil.contains(tmp2, item.identitySourceConfig.id)) {
                    accountBindStructure.isBind = true; 
                } else {
                    accountBindStructure.isBind = false; 
                } 

            } else {
                accountBindStructure.isBind = false; 
            } 

            CommonFunctionUtil.add(result, CommonFunctionUtil.clone(accountBindStructure));
        } 

        CommonFunctionUtil.listSort(result, (item) -> item.isBind, false);
        return result;
    } 

}

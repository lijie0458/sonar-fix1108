package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_90BB04F104917B26166C550B4A1B0632; 
import com.dogfood.aa20240808.repository.LCAPGetUserListCustomizeServiceMapper; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.config.Constants; 

/**
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
public class LCAPGetUserListCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LCAPGetUserListCustomizeServiceMapper lCAPGetUserListCustomizeServiceMapper;

    public AnonymousStructure_90BB04F104917B26166C550B4A1B0632 lCAPGetUserList(String queryParam) {
        AnonymousStructure_90BB04F104917B26166C550B4A1B0632 result = new AnonymousStructure_90BB04F104917B26166C550B4A1B0632();
        result = CommonFunctionUtil.createListPage(lCAPGetUserListCustomizeServiceMapper.getAnonymousStructure_47C167E7217746A55100F50A57F637C0(queryParam), lCAPGetUserListCustomizeServiceMapper.countAnonymousStructure_47C167E7217746A55100F50A57F637C0(queryParam).intValue(), AnonymousStructure_90BB04F104917B26166C550B4A1B0632.class); 
        return result;
    } 

}

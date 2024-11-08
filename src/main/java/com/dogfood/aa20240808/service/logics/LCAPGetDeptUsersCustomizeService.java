package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import java.util.ArrayList; 
import java.util.List; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_9C0F38CE0B1F4B46D348B17C9BB990AA; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_C477D5E68C0A882A877BCEEA4EA2DE1E; 
import com.dogfood.aa20240808.config.Constants; 
import com.dogfood.aa20240808.repository.LCAPGetDeptUsersCustomizeServiceMapper; 

/**
 * 获取当前部门下成员
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
public class LCAPGetDeptUsersCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LCAPGetDeptUsersCustomizeServiceMapper lCAPGetDeptUsersCustomizeServiceMapper;

    public AnonymousStructure_C477D5E68C0A882A877BCEEA4EA2DE1E lCAPGetDeptUsers(String deptId, Long page, Long size) {
        List<AnonymousStructure_9C0F38CE0B1F4B46D348B17C9BB990AA> searchList = new ArrayList<>();
        AnonymousStructure_C477D5E68C0A882A877BCEEA4EA2DE1E result = new AnonymousStructure_C477D5E68C0A882A877BCEEA4EA2DE1E();
        if (CommonFunctionUtil.hasValue(deptId)) {
            searchList = lCAPGetDeptUsersCustomizeServiceMapper.getAnonymousStructure_9C0F38CE0B1F4B46D348B17C9BB990AA(deptId); 
            if ((CommonFunctionUtil.length(searchList).compareTo(0L) > 0)) {
                result = CommonFunctionUtil.paginateList(searchList, page, size, AnonymousStructure_C477D5E68C0A882A877BCEEA4EA2DE1E.class); 
            } else {
            } 

        } else {
        } 

        return result;
    } 

}

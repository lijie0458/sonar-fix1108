package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import java.util.ArrayList; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_73BD2DE8D3A2FEEB58892DAF14663D90; 
import java.util.List; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_B5031BE337A8E75F153A8EDD97EDE05B; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import com.dogfood.aa20240808.util.LogicCallUtils; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.repository.PERGetUserListByRoleIdCustomizeServiceMapper; 
import com.dogfood.aa20240808.config.Constants; 

/**
 * PER-通过角色id获取角色下的成员列表
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
public class PERGetUserListByRoleIdCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private PERGetUserListByRoleIdCustomizeServiceMapper pERGetUserListByRoleIdCustomizeServiceMapper;

    public List<AnonymousStructure_B5031BE337A8E75F153A8EDD97EDE05B> pERGetUserListByRoleId(Long roleId) {
        List<AnonymousStructure_B5031BE337A8E75F153A8EDD97EDE05B> result = new ArrayList<>();
        if ((CommonFunctionUtil.notEquals(roleId, null))) {
            result = CommonFunctionUtil.createListPage(pERGetUserListByRoleIdCustomizeServiceMapper.getAnonymousStructure_B5031BE337A8E75F153A8EDD97EDE05B(roleId), pERGetUserListByRoleIdCustomizeServiceMapper.countAnonymousStructure_B5031BE337A8E75F153A8EDD97EDE05B(roleId).intValue(), AnonymousStructure_73BD2DE8D3A2FEEB58892DAF14663D90.class).list; 
        } else {
            LCAP_LOGGER.info(CommonFunctionUtil.toString("传入参数roleId为null"));
            LCAP_LOGGER.info("abort!"); 
            LogicCallUtils.abort(); 
        } 

        return result;
    } 

}

package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import java.util.ArrayList; 
import java.util.List; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import com.dogfood.aa20240808.repository.LoadPurchaseOrderDetailsByRequestOrderNumberCustomizeServiceMapper; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_898D34F6DFA2CDBCB4C25D9754ABC005; 
import org.slf4j.Logger; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_CDC259E4B568E15F37645E1088B0DC34; 
import com.dogfood.aa20240808.domain.structure.PurchaseOrderDetailStructureStructure; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.config.Constants; 

/**
 * 根据主订单好查询下面明细
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
public class LoadPurchaseOrderDetailsByRequestOrderNumberCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LoadPurchaseOrderDetailsByRequestOrderNumberCustomizeServiceMapper loadPurchaseOrderDetailsByRequestOrderNumberCustomizeServiceMapper;

    public List<PurchaseOrderDetailStructureStructure> loadPurchaseOrderDetailsByRequestOrderNumber(String request_number) {
        List<PurchaseOrderDetailStructureStructure> List = new ArrayList<>();
        PurchaseOrderDetailStructureStructure PurchaseDetails = new PurchaseOrderDetailStructureStructure();
        List<PurchaseOrderDetailStructureStructure> result = new ArrayList<>();
        List<AnonymousStructure_898D34F6DFA2CDBCB4C25D9754ABC005> currentLcpEachName_1 = CommonFunctionUtil.createListPage(loadPurchaseOrderDetailsByRequestOrderNumberCustomizeServiceMapper.getAnonymousStructure_898D34F6DFA2CDBCB4C25D9754ABC005(request_number), loadPurchaseOrderDetailsByRequestOrderNumberCustomizeServiceMapper.countAnonymousStructure_898D34F6DFA2CDBCB4C25D9754ABC005(request_number).intValue(), AnonymousStructure_CDC259E4B568E15F37645E1088B0DC34.class).list;
        for (Long index = 0L; index < currentLcpEachName_1.size(); index++) {
            AnonymousStructure_898D34F6DFA2CDBCB4C25D9754ABC005 item = currentLcpEachName_1.get(index.intValue());
            PurchaseDetails.material = item.materialDetails; 
            PurchaseDetails.purchaseOrderDetail = item.purchaseOrderDetail; 
            PurchaseDetails.unitName = item.measuringUnit.unitName; 
            CommonFunctionUtil.add(List, CommonFunctionUtil.clone(PurchaseDetails));
        } 

        result = CommonFunctionUtil.clone(List); 
        return result;
    } 

}

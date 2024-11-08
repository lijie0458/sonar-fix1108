package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import java.util.HashMap; 
import com.dogfood.aa20240808.service.entities.InventoryEntityService; 
import java.util.ArrayList; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_3D26FC4B14C7240A937FF824EB2C8626; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_D064F9FB058DBE7CA5880F9FE42F0344; 
import com.dogfood.aa20240808.domain.entities.InventoryEntity; 
import java.util.List; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_873CF28DE5093DD1906C2E345F84CE28; 
import com.dogfood.aa20240808.domain.structure.SaleOrderDetailStrutureStructure; 
import com.dogfood.aa20240808.domain.entities.Storage_locationEntity; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import java.util.Map; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.dogfood.aa20240808.repository.LoadsaleOrdersDetailsBySaleOrderNumCustomizeServiceMapper; 
import com.dogfood.aa20240808.config.Constants; 
import java.math.BigDecimal; 
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_C6F46AF0646F4C6D829DCCDE2CD5BDDD; 

/**
 * 点击下订单查询下面明细
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
public class LoadsaleOrdersDetailsBySaleOrderNumCustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    private LoadsaleOrdersDetailsBySaleOrderNumCustomizeServiceMapper loadsaleOrdersDetailsBySaleOrderNumCustomizeServiceMapper;

    @Autowired
    private LoadquantityShippedByDetailsIdCustomizeService loadquantityShippedByDetailsIdCustomizeService;

    @Autowired
    private LoadSaleOderByInventoryIdCustomizeService loadSaleOderByInventoryIdCustomizeService;

    @Autowired
    private GetStoragelocationByStorageCustomizeService getStoragelocationByStorageCustomizeService;

    @Autowired
    private LoadWarehouseByCodeCustomizeService loadWarehouseByCodeCustomizeService;

    @Autowired
    private InventoryEntityService inventoryEntityService;

    @Autowired
    private LoadBathNumberByCodeAnddistinctionCustomizeService loadBathNumberByCodeAnddistinctionCustomizeService;

    @Autowired
    private LoadDeliveryReleaseDetailsSelect_15CustomizeService loadDeliveryReleaseDetailsSelect_15CustomizeService;

    @Autowired
    private LoadCountInventoryByCodeAndWahouseCustomizeService loadCountInventoryByCodeAndWahouseCustomizeService;

    public List<SaleOrderDetailStrutureStructure> loadsaleOrdersDetailsBySaleOrderNum(String request_number, Boolean isUpdate) {
        List<SaleOrderDetailStrutureStructure> List = new ArrayList<>();
        SaleOrderDetailStrutureStructure saleOrderDetails = new SaleOrderDetailStrutureStructure();
        List<AnonymousStructure_873CF28DE5093DD1906C2E345F84CE28> variable1 = new ArrayList<>();
        Map<String, BigDecimal> Map = new HashMap<>();
        List<InventoryEntity> variable2 = new ArrayList<>();
        Map<Long, BigDecimal> Map2 = new HashMap<>();
        List<AnonymousStructure_3D26FC4B14C7240A937FF824EB2C8626> ListDetailsId = new ArrayList<>();
        Storage_locationEntity variable3 = new Storage_locationEntity();
        List<SaleOrderDetailStrutureStructure> result = new ArrayList<>();
        variable1 = CommonFunctionUtil.createListPage(loadsaleOrdersDetailsBySaleOrderNumCustomizeServiceMapper.getAnonymousStructure_873CF28DE5093DD1906C2E345F84CE28(request_number), loadsaleOrdersDetailsBySaleOrderNumCustomizeServiceMapper.countAnonymousStructure_873CF28DE5093DD1906C2E345F84CE28(request_number).intValue(), AnonymousStructure_C6F46AF0646F4C6D829DCCDE2CD5BDDD.class).list; 
        // 根据已经发货数量进行查询，如果已发货数量大于订单数量，则不能在下订单
        ListDetailsId = loadquantityShippedByDetailsIdCustomizeService.loadquantityShippedByDetailsId(); 
        if ((CommonFunctionUtil.length(ListDetailsId).compareTo(0L) > 0)) {
            Map2 = CommonFunctionUtil.listToMap(ListDetailsId, (item) -> item.salesOrderDetailsId, (item) -> item.sum); 
        } else {
        } 

        Map = CommonFunctionUtil.listToMap(loadSaleOderByInventoryIdCustomizeService.loadSaleOderByInventoryId(CommonFunctionUtil.listTransform(variable1, (item) -> item.salesOrderDetails.materialCode)), (item) -> item.materialCode, (item) -> item.total); 
        for (Long index = 0L; index < variable1.size(); index++) {
            AnonymousStructure_873CF28DE5093DD1906C2E345F84CE28 item = variable1.get(index.intValue());
            if ((CommonFunctionUtil.equals(isUpdate, null))) {
                if ((CommonFunctionUtil.equals(CommonFunctionUtil.mapGet(Map2, item.salesOrderDetails.id), null))) {
                    saleOrderDetails.unshippedQuantity = BigDecimal.valueOf(0L); 
                } else {
                    saleOrderDetails.unshippedQuantity = CommonFunctionUtil.mapGet(Map2, item.salesOrderDetails.id); 
                } 

                if ((saleOrderDetails.unshippedQuantity.compareTo(item.salesOrderDetails.quantity) == -1)) {
                    saleOrderDetails.saleOrderDetails = item.salesOrderDetails; 
                    if (CommonFunctionUtil.hasValue(saleOrderDetails.saleOrderDetails.warehouse)) {
                        variable3 = CommonFunctionUtil.listHead(getStoragelocationByStorageCustomizeService.getStoragelocationByStorage(saleOrderDetails.saleOrderDetails.warehouse)); 
                        saleOrderDetails.location = variable3.location_name; 
                    } else {
                    } 

                    saleOrderDetails.name = item.materialDetails; 
                    saleOrderDetails.sp = item.materialDetails.specification; 
                    saleOrderDetails.unitname = item.measuringUnit.unitName; 
                    saleOrderDetails.warehouseList = loadWarehouseByCodeCustomizeService.loadWarehouseByCode(item.salesOrderDetails.materialCode); 
                    saleOrderDetails.inventory = CommonFunctionUtil.mapGet(Map, item.salesOrderDetails.materialCode); 
                    CommonFunctionUtil.add(List, CommonFunctionUtil.clone(saleOrderDetails));
                } else {
                } 

            } else {
                if (isUpdate) {
                    saleOrderDetails.saleOrderDetails = item.salesOrderDetails; 
                    if (CommonFunctionUtil.hasValue(saleOrderDetails.saleOrderDetails.warehouse)) {
                        variable3 = CommonFunctionUtil.listHead(getStoragelocationByStorageCustomizeService.getStoragelocationByStorage(saleOrderDetails.saleOrderDetails.warehouse)); 
                        saleOrderDetails.location = variable3.location_name; 
                    } else {
                    } 

                    saleOrderDetails.name = item.materialDetails; 
                    saleOrderDetails.sp = item.materialDetails.specification; 
                    saleOrderDetails.unitname = item.measuringUnit.unitName; 
                    saleOrderDetails.warehouseList = loadWarehouseByCodeCustomizeService.loadWarehouseByCode(item.salesOrderDetails.materialCode); 
                    if (saleOrderDetails.name.is_batch_enabled) {
                        saleOrderDetails.inventory = inventoryEntityService.get(item.salesOrderDetails.batchNumber).stockQuantity; 
                        List<AnonymousStructure_D064F9FB058DBE7CA5880F9FE42F0344> currentLcpEachName_1 = loadBathNumberByCodeAnddistinctionCustomizeService.loadBathNumberByCodeAnddistinction(item.salesOrderDetails.materialCode, item.salesOrderDetails.warehouse, loadDeliveryReleaseDetailsSelect_15CustomizeService.loadDeliveryReleaseDetailsSelect_15(item.salesOrderDetails.warehouse).id).list;
                        for (Long index1 = 0L; index1 < currentLcpEachName_1.size(); index1++) {
                            AnonymousStructure_D064F9FB058DBE7CA5880F9FE42F0344 item1 = currentLcpEachName_1.get(index1.intValue());
                            CommonFunctionUtil.add(variable2, CommonFunctionUtil.clone(item1.inventory));
                        } 

                        saleOrderDetails.inventoryList = CommonFunctionUtil.clone(variable2); 
                    } else {
                        saleOrderDetails.inventory = loadCountInventoryByCodeAndWahouseCustomizeService.loadCountInventoryByCodeAndWahouse(saleOrderDetails.saleOrderDetails.warehouse, saleOrderDetails.saleOrderDetails.materialCode).total; 
                    } 

                    CommonFunctionUtil.add(List, CommonFunctionUtil.clone(saleOrderDetails));
                } else {
                    saleOrderDetails.saleOrderDetails = item.salesOrderDetails; 
                    if (CommonFunctionUtil.hasValue(saleOrderDetails.saleOrderDetails.warehouse)) {
                        variable3 = CommonFunctionUtil.listHead(getStoragelocationByStorageCustomizeService.getStoragelocationByStorage(saleOrderDetails.saleOrderDetails.warehouse)); 
                        saleOrderDetails.location = variable3.location_name; 
                    } else {
                    } 

                    saleOrderDetails.name = item.materialDetails; 
                    saleOrderDetails.sp = item.materialDetails.specification; 
                    saleOrderDetails.unitname = item.measuringUnit.unitName; 
                    saleOrderDetails.warehouseList = loadWarehouseByCodeCustomizeService.loadWarehouseByCode(item.salesOrderDetails.materialCode); 
                    saleOrderDetails.inventory = CommonFunctionUtil.mapGet(Map, item.salesOrderDetails.materialCode); 
                    CommonFunctionUtil.add(List, CommonFunctionUtil.clone(saleOrderDetails));
                } 

            } 

        } 

        result = CommonFunctionUtil.clone(List); 
        return result;
    } 

}
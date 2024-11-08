package com.dogfood.aa20240808.service.logics; 

import org.springframework.stereotype.Service; 
import org.springframework.beans.factory.annotation.Qualifier; 
import com.netease.cloud.extension.model.RabbitMQueueDeclare; 
import com.dogfood.aa20240808.util.CommonFunctionUtil; 
import org.slf4j.Logger; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.slf4j.LoggerFactory; 
import com.netease.cloud.extension.model.SendModel; 
import com.dogfood.aa20240808.config.Constants; 
import com.netease.cloud.extension.RabbitMQ; 

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
public class Logic1CustomizeService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger(Constants.LCAP_CUSTOMIZE_LOGGER);

    @Autowired
    @Qualifier("connector_RabbitMQRabb1")
    private RabbitMQ connector_RabbitMQRabb1;

    public Object logic1() {
        Object result = new Object();
        result = (connector_RabbitMQRabb1.send(CommonFunctionUtil.newWithInitiation(new SendModel(), _bean1 -> {
    _bean1.msg = "helloworld2"; 
    _bean1.declare = CommonFunctionUtil.newWithInitiation(new RabbitMQueueDeclare(), _bean2 -> {
    _bean2.queueName = "queue1"; 
} ); 
} ))); 
        return result;
    } 

}

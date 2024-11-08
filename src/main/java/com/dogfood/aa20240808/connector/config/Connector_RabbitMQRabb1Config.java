package com.dogfood.aa20240808.connector.config; 

import org.springframework.context.annotation.Configuration; 
import org.springframework.core.env.Environment; 
import org.springframework.context.annotation.Bean; 
import org.springframework.beans.factory.annotation.Autowired; 
import com.netease.cloud.extension.RabbitMQ; 

@Configuration
public class Connector_RabbitMQRabb1Config {

    @Autowired
    private Environment env;

    @Bean(name = "connector_RabbitMQRabb1")
    public RabbitMQ connector_RabbitMQRabb1() {
        RabbitMQ RabbitMQ = new RabbitMQ().initRabbitMQ(this.env.getProperty("connector.rabb1.host"), this.env.getProperty("connector.rabb1.port", Integer.class), this.env.getProperty("connector.rabb1.userName"), this.env.getProperty("connector.rabb1.password"));
        return RabbitMQ;
    } 

}

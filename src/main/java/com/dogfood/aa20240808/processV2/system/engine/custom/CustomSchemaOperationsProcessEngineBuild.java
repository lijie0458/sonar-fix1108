package com.dogfood.aa20240808.processV2.system.engine.custom;

import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CustomSchemaOperationsProcessEngineBuild implements Command<Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSchemaOperationsProcessEngineBuild.class);

    @Override
    public Void execute(CommandContext commandContext) {
        return null;
    }
}

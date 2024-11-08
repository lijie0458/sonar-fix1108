package com.dogfood.aa20240808.processV2.system.aspect;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProcessRecord {
    /**
     * operation action
     * @return
     */
    ProcessRecordAction action();
}

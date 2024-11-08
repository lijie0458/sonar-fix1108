package com.dogfood.aa20240808.service.dto.filters.logic.unary;


/**
 * @Author: sys
 */
public class IsNullQueryFilter extends UnaryExpressionFilter {


    public IsNullQueryFilter() {
        this.operator = "isNull";
    }

    @Override
    public String sql(String dbType) {
        return String.format(" (%s is null) ", left.sql(dbType));
    }
}

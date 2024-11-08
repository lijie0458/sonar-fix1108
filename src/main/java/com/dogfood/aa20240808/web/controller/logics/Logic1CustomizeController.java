package com.dogfood.aa20240808.web.controller.logics; 

import org.springframework.web.bind.annotation.RestController; 
import com.dogfood.aa20240808.service.logics.Logic1CustomizeService; 
import com.dogfood.aa20240808.web.validation.Validation; 
import com.dogfood.aa20240808.web.ApiReturn; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.web.bind.annotation.PostMapping; 
import com.dogfood.aa20240808.web.validation.ValidationRuleGroup; 

@RestController
public class Logic1CustomizeController {

    @Autowired
    private Logic1CustomizeService logic1CustomizeService;

    @Validation(value = { @ValidationRuleGroup(value = "62c96b0151bb48d19934c2d922d57f48",rules = { })})
    @PostMapping("/api/lcplogics/logic1")
    public ApiReturn<Object> logic1() throws Exception {
        return ApiReturn.of(logic1CustomizeService.logic1());
    } 

}

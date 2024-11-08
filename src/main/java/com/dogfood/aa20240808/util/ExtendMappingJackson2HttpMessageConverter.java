package com.dogfood.aa20240808.util;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

public class ExtendMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    public ExtendMappingJackson2HttpMessageConverter() {
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.valueOf("text/json"));
        setSupportedMediaTypes(mediaTypes);
    }
}
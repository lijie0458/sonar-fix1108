package com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import java.util.ArrayList; 
import java.util.List; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class NewsStructure {

    public List<com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.ArticlesStructure> articles = new ArrayList<>();
    public List<com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.ArticlesStructure> getArticles() {
        return articles;
    } 

    public void setArticles(List<com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.ArticlesStructure> articles) {
        this.articles = articles; 
    } 


}

package com.example.product_store.elasticSearch.model;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class CategoryES {
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text)
    private String categoryName;
}

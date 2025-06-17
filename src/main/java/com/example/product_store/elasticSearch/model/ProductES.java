package com.example.product_store.elasticSearch.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "products")
@Data
public class ProductES {

    @Id
    private String id;

    private String title;
    private String description;
    private Integer stock;
    private Double price;

    @Field(type = FieldType.Keyword)
    private String createdBy;

    @Field(type = FieldType.Nested)
    private List<CategoryES> categories;

}

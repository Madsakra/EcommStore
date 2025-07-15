package com.example.product_store.store.product.specification;

import com.example.product_store.store.category.model.Category;
import com.example.product_store.store.product.model.Product;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

// CLASS THAT BUILDS FILTER FOR JPA TO PROCESS
public class ProductSpecification {

    public static Specification<Product> hasCategoryIds(List<String> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Category> join = root.join("categories");
            return join.get("id").in(categoryIds);
        };
    }


    // > THAN PRICE
    public static Specification<Product> hasPriceGreaterThan(BigDecimal price) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price)
        );
    }

    // < THAN PRICE
    public static Specification<Product> hasPriceLessThan(BigDecimal price) {
        return (((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), price)
        ));
    }

    // FOR SEARCHING TITLE: ADD ON SPECIFICATION
    public static Specification<Product> titleContains(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    // FOR SEARCHING DESCRIPTION: ADD ON SPECIFICATION\
    public static Specification<Product> descriptionContaining(String description){
        return (root, query, cb) -> cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");

    }

}

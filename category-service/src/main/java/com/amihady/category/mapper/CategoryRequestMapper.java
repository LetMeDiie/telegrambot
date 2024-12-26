package com.amihady.category.mapper;


import com.amihady.category.entity.Category;
import com.amihady.category.request.AddNewCategoryRequest;
import org.springframework.stereotype.Component;

@Component
public class CategoryRequestMapper {
    public Category mapToCategory(AddNewCategoryRequest request){
        Category category = new Category();
        category.setCategoryName(request.categoryName());
        return category;
    }
}

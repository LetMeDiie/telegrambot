package com.amihady.category.service;

import com.amihady.category.dto.CategoryTreeDto;
import com.amihady.category.request.AddNewCategoryRequest;
import com.amihady.category.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryTreeDto> getCategoryTree();  // Получение дерева категорий

    CategoryResponse addRootCategory(AddNewCategoryRequest request);  // Добавление корневой категории
    CategoryResponse addChildCategory(String rootCategoryName , AddNewCategoryRequest request);  // Добавление дочерней категории
    CategoryResponse removeCategory(String categoryName);  // Удаление категории
}

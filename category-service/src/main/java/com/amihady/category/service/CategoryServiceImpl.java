package com.amihady.category.service;

import com.amihady.category.dto.CategoryTreeDto;
import com.amihady.category.entity.Category;
import com.amihady.category.mapper.CategoryRequestMapper;
import com.amihady.category.mapper.CategoryTreeDtoMapper;
import com.amihady.category.repository.CategoryRepository;
import com.amihady.category.request.AddNewCategoryRequest;
import com.amihady.category.response.CategoryResponse;
import com.amihady.exception.CategoryAlreadyExistsException;
import com.amihady.exception.CategoryNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level= AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository repository;
    CategoryRequestMapper categoryRequestMapper;

    CategoryTreeDtoMapper categoryTreeDtoMapper;

    @Cacheable(value = "categoryTreeCache")
    @Override
    public List<CategoryTreeDto> getCategoryTree() {
        List<Category> roots = repository.findByParentIsNull();
        List<CategoryTreeDto> categoryTreeDtoList=
                categoryTreeDtoMapper.mapToCategoryTree(roots);
        return categoryTreeDtoList;
    }

    @CacheEvict(value = "categoryTreeCache", allEntries = true)
    @Override
    public CategoryResponse addRootCategory(AddNewCategoryRequest request) {
        validateCategoryDoesNotExist(request.categoryName());

        Category rootCategory=categoryRequestMapper.mapToCategory(request);
        repository.save(rootCategory);

        return new CategoryResponse("Root category '" + request.categoryName() + "' has been saved.");

    }

    @CacheEvict(value = "categoryTreeCache", allEntries = true)
    @Override
    public CategoryResponse addChildCategory(
                            String parentCategoryName ,
                            AddNewCategoryRequest request) {
        Category parentCategory = findCategoryByName(parentCategoryName);

        validateCategoryDoesNotExist(request.categoryName());
        Category childCategory = categoryRequestMapper.mapToCategory(request);

        parentCategory.addChild(childCategory);
        childCategory.setParent(parentCategory);

        repository.save(parentCategory);

        return new CategoryResponse(
                "Child category '" + request.categoryName()
                        + "' has been added to parent '" +parentCategoryName + "'."
        );
    }

    @CacheEvict(value = "categoryTreeCache", allEntries = true)
    @Override
    public CategoryResponse removeCategory(String categoryName) {
         Category category = findCategoryByName(categoryName);
         repository.delete(category);
        return new CategoryResponse(
                "Category '" + category.getCategoryName() + "' has been successfully deleted."
        );
    }

    private Category findCategoryByName(String categoryName) {
        return repository.findByCategoryName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category with name " + categoryName + " not found"));
    }

    private void validateCategoryDoesNotExist(String categoryName) {
        if (repository.findByCategoryName(categoryName).isPresent()) {
            throw new CategoryAlreadyExistsException("Category with name '" + categoryName + "' already exists.");
        }
    }
}
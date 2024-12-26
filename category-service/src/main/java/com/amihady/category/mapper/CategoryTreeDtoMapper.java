package com.amihady.category.mapper;

import com.amihady.category.dto.CategoryTreeDto;
import com.amihady.category.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryTreeDtoMapper {

    public List<CategoryTreeDto> mapToCategoryTree(List<Category> categories) {
        return categories.stream()
                .map(this::mapToCategoryTreeDto)
                .collect(Collectors.toList());
    }

    private CategoryTreeDto mapToCategoryTreeDto(Category category) {
        List<CategoryTreeDto> children = category.getChildren().isEmpty()
                ? List.of()
                : category.getChildren().stream()
                .map(this::mapToCategoryTreeDto)
                .collect(Collectors.toList());

        return new CategoryTreeDto(category.getCategoryName(), children);
    }
}

package com.amihady.category.dto;

import java.util.List;

public record CategoryTreeDto(
        String categoryName,
        List<CategoryTreeDto> children
) {
}

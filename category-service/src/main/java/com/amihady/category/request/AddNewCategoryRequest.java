package com.amihady.category.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddNewCategoryRequest(
        @NotNull(message = "Category name cannot be null")
        @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
        String categoryName
) {
}

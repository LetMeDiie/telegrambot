package com.amihady.category.controller;

import com.amihady.category.dto.CategoryTreeDto;
import com.amihady.category.request.AddNewCategoryRequest;
import com.amihady.category.response.CategoryResponse;
import com.amihady.category.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping( value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE )
@RestController
@FieldDefaults(level= AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
public class CategoriesRestController {
    CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<List<CategoryTreeDto>> getCategoryTree() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }

    @PostMapping()
    public ResponseEntity<CategoryResponse> addRootCategory(
                                @RequestBody AddNewCategoryRequest request) {
        CategoryResponse categoryResponse =
                categoryService.addRootCategory(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryResponse);

    }
}

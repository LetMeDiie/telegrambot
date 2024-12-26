package com.amihady.category.controller;


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

@RestController
@RequiredArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping(value ="/categories/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoryRestController {
    CategoryService categoryService;


    @DeleteMapping
    public ResponseEntity<CategoryResponse> deleteCategory(
                @PathVariable("name") String name){
        return ResponseEntity.ok(categoryService.removeCategory(name));

    }

    @PostMapping()
    public ResponseEntity<CategoryResponse> addChildCategory(
            @PathVariable("name") String name,
            @RequestBody AddNewCategoryRequest request){
        CategoryResponse response =
                categoryService.addChildCategory(name,request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }
}

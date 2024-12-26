package com.amihady.category;


import com.amihady.category.controller.CategoryRestController;
import com.amihady.category.request.AddNewCategoryRequest;
import com.amihady.category.response.CategoryResponse;
import com.amihady.category.service.CategoryService;
import com.amihady.exception.CategoryAlreadyExistsException;
import com.amihady.exception.CategoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryRestController.class)
public class CategoryRestControllerTest {

    private static final String BASE_URL = "/categories/{name}";

    @Autowired
    MockMvc mockMvc;


    @MockBean
    private CategoryService categoryService;

    @Test
    public void testAddChildCategory_Success() throws Exception{
        String parentCategory = "ParentCategory";
        AddNewCategoryRequest request = new AddNewCategoryRequest("New child category");
        CategoryResponse categoryResponse = new CategoryResponse("Child category added");

        when(categoryService.addChildCategory(parentCategory,request))
                .thenReturn(categoryResponse);
        mockMvc.perform(post(BASE_URL,parentCategory)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoryName\":\"New child category\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Child category added"));
    }

    @Test
    public void testAddChildCategory_ParentNotFound() throws Exception {
        String parentCategory = "NonExistingParent";
        AddNewCategoryRequest request = new AddNewCategoryRequest("New child category");

        when(categoryService.addChildCategory(parentCategory, request))
                .thenThrow(new CategoryNotFoundException("Parent category with name " + parentCategory + " not found"));

        mockMvc.perform(post(BASE_URL, parentCategory)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"New child category\"}"))
                .andExpect(status().isNotFound())  // Ожидаем статус 404
                .andExpect(jsonPath("$.title").value("Category Not Found"))
                .andExpect(jsonPath("$.detail").value("Parent category with name NonExistingParent not found"));
    }

    @Test
    public void testAddChildCategory_CategoryAlreadyExists() throws Exception {
        String parentCategory = "ParentCategory";
        AddNewCategoryRequest request = new AddNewCategoryRequest("Existing child category");

        when(categoryService.addChildCategory(parentCategory, request))
                .thenThrow(new CategoryAlreadyExistsException("Category with name " + request.categoryName() + " already exists"));

        mockMvc.perform(post(BASE_URL, parentCategory)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"Existing child category\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Category Already Exists"))
                .andExpect(jsonPath("$.detail").value("Category with name Existing child category already exists"));
    }

    @Test
    public void testDeleteCategory_Success() throws Exception{
        String categoryName = "ExistingCategory";
        CategoryResponse response = new CategoryResponse("Category successfully deleted");

        when(categoryService.removeCategory(categoryName))
                .thenReturn(response);

        mockMvc.perform(delete(BASE_URL,categoryName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Category successfully deleted"));
    }

    @Test
    public void testDeleteCategory_NotFound() throws Exception {
        String categoryName = "NonExistingCategory";

        when(categoryService.removeCategory(categoryName)).thenThrow(new CategoryNotFoundException("Category not found"));

        mockMvc.perform(delete(BASE_URL,categoryName))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Category Not Found"))
                .andExpect(jsonPath("$.detail").value( "Category not found"));

    }


}

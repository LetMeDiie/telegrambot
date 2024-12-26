package com.amihady.category;


import com.amihady.category.controller.CategoriesRestController;
import com.amihady.category.dto.CategoryTreeDto;
import com.amihady.category.request.AddNewCategoryRequest;
import com.amihady.category.response.CategoryResponse;
import com.amihady.category.service.CategoryService;
import com.amihady.exception.CategoryAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriesRestController.class)
public class CategoriesRestControllerTest {

    private static final String BASE_URL = "/categories";
    @Autowired
    MockMvc mockMvc;

    @MockBean
    CategoryService categoryService;

    @Test
    public void testGetCategoryTree() throws Exception{
        CategoryTreeDto categoryTreeDto1 = new CategoryTreeDto("root1",new ArrayList<>());
        CategoryTreeDto categoryTreeDto2 = new CategoryTreeDto("root2",new ArrayList<>());
        List<CategoryTreeDto> categoryTreeDtoList = Arrays.asList(categoryTreeDto1, categoryTreeDto2);

        when(categoryService.getCategoryTree()).thenReturn(categoryTreeDtoList);

        mockMvc.perform(
                 get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryName").value("root1"))
                .andExpect(jsonPath("$[1].categoryName").value("root2"));
    }


    @Test
    public void testAddRootCategory_Success() throws Exception {
        AddNewCategoryRequest request = new AddNewCategoryRequest("New Category");
        CategoryResponse categoryResponse = new CategoryResponse("Added new category");

        when(categoryService.addRootCategory(request)).thenReturn(categoryResponse);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"New Category\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Added new category"));
    }

    @Test
    public void testAddRootCategory_CategoryAlreadyExists() throws Exception {
        AddNewCategoryRequest request = new AddNewCategoryRequest("Existing Category");

        when(categoryService.addRootCategory(request))
                .thenThrow(new CategoryAlreadyExistsException("Category with name " + request.categoryName() + " already exists"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"Existing Category\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Category Already Exists"))
                .andExpect(jsonPath("$.detail").value("Category with name Existing Category already exists"));

    }


}

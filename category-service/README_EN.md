# Category Service Design

## Functionality

- **Adding and deleting categories.**
- **Generating and providing category tree data:** Converts the category hierarchy into a structured format (tree).
- **Ensuring unique category names in the hierarchy:** All category names will be unique, regardless of the hierarchy.
- **Database integration.**
- **Error handling:** Handles scenarios where a category is not found or name uniqueness is violated.
- **Caching:** Caching will be used to speed up processing and minimize database queries (e.g., for the category tree).

## Use Cases

### 1. Adding a Root Category
**Goal:** The system adds a new category at the top level of the hierarchy.

**Main flow:**
1. The actor sends a request to add a category, providing the following data:
    - Category Name: The name of the new category.
2. The system checks:
    - Ensures that no category with the same name exists among all categories.
    - If it exists, an error message is returned.
3. If the name is unique:
    - The system saves the category to the database.
    - The cache is updated.
4. The system returns confirmation of successful addition with the following information:
    - Category Name.
    - Status: "successfully added."


### 2. Adding a Child Category
**Goal:** The system adds a new category as a child to an existing category.

**Main flow:**
1. The actor sends a request to add a child category, providing the following data:
   - Category Name: The name of the new category.
   - Parent Category Name: The identifier (name) of the existing category to which the new category will be attached.
2. The system performs checks:
   - Ensures that no category with the same name exists among all categories.
   - Ensures that the parent category exists.
3. If the checks pass successfully:
   - The system saves the new category, linking it to the specified parent category in the database.
   - The cache is updated.
4. The system returns confirmation of successful addition with the following information:
   - Category Name.
   - Parent Category Name.
   - Status: "successfully added."

### 3. Deleting a Category
**Goal:** The system deletes an existing category from the category tree.

**Main flow:**
1. The actor sends a request to delete a category, providing the following data:
   - Category Name: The name of the category to be deleted.
2. The system performs checks:
   - Ensures that the category with the specified name exists.
   - If it does not exist, an error is returned.
3. If the checks pass successfully:
   - The system deletes the category and all its child elements from the database.
   - The cache is updated.
4. The system returns confirmation of successful deletion with the following information:
   - Category Name.
   - Status: "successfully deleted."

**Additional:** When deleting a category, all its child elements are automatically removed to avoid "orphaned" records in the database. This is achieved using cascade deletion and orphan removal (cascade = ALL, orphanRemoval = true).

### 4. Retrieving Categories as a Tree
**Goal:** The system provides a hierarchy of categories as a tree, allowing the user to view the structure of all categories and their child elements.

**Main flow:**
1. The actor sends a request to retrieve the category tree (no additional parameters required).
2. The system processes the request:
   - Retrieves all categories that do not have a parent category.
   - Uses an algorithm to build the category tree, taking the hierarchy into account.
3. The system returns the data structure in JSON format, with child categories nested under their respective parent elements.

**Additional:** The category model must contain information about its parent (the parent category's ID) to correctly construct the hierarchy.

## Notes

- The **"one-to-many"** principle was used to model the relationship between parent and child categories.
- **Caching** is used in the system to speed up operations with the category tree.

# How to Use the Service

## 1. Request to Retrieve the Category Tree
**Method:** GET  
**URL:** `http://localhost:8080/api/categories`

### Description:
This request returns the category tree, meaning all categories with their child elements.

### Example Request:
GET `http://localhost:8080/api/categories`

## 2. Request to Add a Root Category
**Method:** POST  
**URL:** `http://localhost:8080/api/categories`

This request allows you to add a new category at the top level of the hierarchy. The root category does not have a parent category.

#### Request Parameters:
- **categoryName**: The name of the new root category (mandatory field).

#### Example Request:
POST `http://localhost:8080/api/categories \
-H "Content-Type: application/json" \
-d '{"categoryName": "New Root Category"}'`

## 3. Request to Delete a Category
**Method:** DELETE  
**URL:** `http://localhost:8080/api/categories/{categoryName}`

This request deletes an existing category from the category tree.

#### Request Parameters:
- **categoryName**: The name of the category to be deleted (mandatory field).

#### Example Request:
DELETE `http://localhost:8080/api/categories/OldCategory`

**Method:** DELETE  
**URL:** `http://localhost:8080/api/categories/{categoryName}`

This request deletes the category by its name along with all its child elements from the database.

#### Request Parameters:
- **categoryName**: The name of the category to be deleted (mandatory field).

#### Example Request:
DELETE `http://localhost:8080/api/categories/Category_1`

### 4. Request to Add a Child Category
**Method:** POST  
**URL:** `http://localhost:8080/api/categories/{parentCategoryName}`

This request adds a new category as a child of an existing category.

#### Request Parameters:
- **categoryName**: The name of the new child category (mandatory field).
- **parentCategoryName**: The name of the parent category to which the new category will be linked (passed in the URL).

#### Example Request:
POST `http://localhost:8080/api/categories/Category_1 \
-H "Content-Type: application/json" \
-d '{"categoryName": "New Child Category"}'`

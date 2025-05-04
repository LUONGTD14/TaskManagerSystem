package com.example.taskmanagersystem.service;

import com.example.taskmanagersystem.dto.request.CategoryRequest;
import com.example.taskmanagersystem.dto.response.CategoryResponse;
import com.example.taskmanagersystem.mapper.CategoryMapper;
import com.example.taskmanagersystem.model.Category;
import com.google.firebase.database.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Service
@RequiredArgsConstructor
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    private DatabaseReference categoryRef;

    @PostConstruct
    public void init() {
        this.categoryRef = FirebaseDatabase.getInstance().getReference("categories");
    }

    public CategoryResponse addCategory(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        category.setId(UUID.randomUUID().toString());
        categoryRef.child(category.getId()).setValueAsync(category);
        return categoryMapper.toDto(category);
    }

    public List<CategoryResponse> getAllCategories() {
        List<CategoryResponse> list = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Category Category = child.getValue(Category.class);
                    if (Category != null) {
                        list.add(categoryMapper.toDto(Category));
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return list;
    }
}


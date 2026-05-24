package com.revnu.backend.shared.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.revnu.backend.features.categories.model.Category;
import com.revnu.backend.features.categories.repository.CategoryRepository;

@Component
@Order(2)
public class CategorySeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public CategorySeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        migrateType("SALES", "SALE");

        seedIfAbsent("Meals", "SALE");
        seedIfAbsent("Drinks", "SALE");
        seedIfAbsent("Desserts", "SALE");
        seedIfAbsent("Catering", "SALE");
        seedIfAbsent("Take-out", "SALE");
        seedIfAbsent("Others", "SALE");

        seedIfAbsent("Ingredients / Supplies", "EXPENSE");
        seedIfAbsent("Utilities", "EXPENSE");
        seedIfAbsent("Equipment", "EXPENSE");
        seedIfAbsent("Maintenance", "EXPENSE");
        seedIfAbsent("Others", "EXPENSE");
    }

    private void migrateType(String oldType, String newType) {
        categoryRepository.findByRestaurantIsNull().stream()
                .filter(c -> oldType.equals(c.getType()))
                .forEach(c -> {
                    c.setType(newType);
                    categoryRepository.save(c);
                });
    }

    private void seedIfAbsent(String name, String type) {
        if (!categoryRepository.existsByNameAndTypeAndRestaurantIsNull(name, type)) {
            categoryRepository.save(Category.builder()
                    .name(name)
                    .type(type)
                    .restaurant(null)
                    .isDefault(true)
                    .build());
        }
    }
}

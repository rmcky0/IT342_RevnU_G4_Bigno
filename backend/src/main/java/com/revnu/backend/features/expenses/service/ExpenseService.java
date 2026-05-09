package com.revnu.backend.features.expenses.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.expenses.dto.ExpenseRequest;
import com.revnu.backend.features.expenses.dto.ExpenseResponse;
import com.revnu.backend.features.expenses.model.Expense;
import com.revnu.backend.features.expenses.model.ExpenseStatus;
import com.revnu.backend.features.expenses.repository.ExpenseRepository;
import com.revnu.backend.features.files.model.FileRecord;
import com.revnu.backend.features.files.repository.FileRecordRepository;
import com.revnu.backend.features.files.service.SupabaseStorageService;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.tags.model.Tag;
import com.revnu.backend.features.tags.repository.TagRepository;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final FileRecordRepository fileRecordRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final TagRepository tagRepository;
    private final SupabaseStorageService supabaseStorageService;

    public ExpenseService(ExpenseRepository expenseRepository, FileRecordRepository fileRecordRepository,
            UserRepository userRepository, RestaurantRepository restaurantRepository,
            TagRepository tagRepository, SupabaseStorageService supabaseStorageService) {
        this.expenseRepository = expenseRepository;
        this.fileRecordRepository = fileRecordRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.tagRepository = tagRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    @Transactional
    public ExpenseResponse recordExpense(String ownerEmail, ExpenseRequest request) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        Set<Tag> tags = resolveTags(request.tagNames(), restaurant);

        Expense expense = Expense.builder()
                .amount(request.amount())
                .description(request.description())
                .tags(tags)
                .restaurant(restaurant)
                .status(ExpenseStatus.OPEN)
                .build();

        return mapToResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse updateExpense(String ownerEmail, UUID expenseId, ExpenseRequest request) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        Expense expense = getValidatedExpense(expenseId, restaurant);

        if (expense.getStatus() == ExpenseStatus.CLOSED) {
            throw new IllegalStateException("Cannot edit a finalized expense record.");
        }

        expense.setAmount(request.amount());
        expense.setDescription(request.description());
        expense.setTags(resolveTags(request.tagNames(), restaurant));

        return mapToResponse(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getAllExpenses(String ownerEmail, int page, int size) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return expenseRepository.findByRestaurant(restaurant, pageable)
                .map(this::mapToResponse);
    }

    public List<ExpenseResponse> getExpensesByDate(String ownerEmail, LocalDate date) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        return expenseRepository.findByRestaurantAndCreatedAtBetween(
                restaurant, date.atStartOfDay(), date.atTime(23, 59, 59))
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteExpense(String ownerEmail, UUID expenseId) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        Expense expense = getValidatedExpense(expenseId, restaurant);

        if (expense.getStatus() == ExpenseStatus.CLOSED) {
            throw new IllegalStateException("Cannot delete a finalized expense record.");
        }

        expenseRepository.delete(expense);
    }

    @Transactional
    public ExpenseResponse uploadReceipt(String ownerEmail, UUID expenseId, MultipartFile file) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));

        Expense expense = getValidatedExpense(expenseId, restaurant);

        if (expense.getStatus() == ExpenseStatus.CLOSED) {
            throw new IllegalStateException("Cannot attach receipt to a finalized expense record.");
        }

        String publicUrl = supabaseStorageService.uploadFile(file, "receipts");

        FileRecord fileRecord = new FileRecord();
        fileRecord.setFilename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "receipt");
        fileRecord.setFilepath(publicUrl);
        fileRecord.setFiletype(file.getContentType() != null ? file.getContentType() : "application/octet-stream");

        expense.setFile(fileRecordRepository.save(fileRecord));
        return mapToResponse(expenseRepository.save(expense));
    }

    // --- Helpers ---
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private Restaurant getRestaurant(User owner) {
        return restaurantRepository.findByOwner(owner)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant profile not found."));
    }

    private Expense getValidatedExpense(UUID expenseId, Restaurant restaurant) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found."));
        if (!expense.getRestaurant().getId().equals(restaurant.getId())) {
            throw new SecurityException("You do not have permission to modify this record.");
        }
        return expense;
    }

    private Set<Tag> resolveTags(List<String> tagNames, Restaurant restaurant) {
        return tagNames.stream()
                .map(name -> tagRepository.findByNameAndRestaurantIdAndType(
                name.toUpperCase().trim(), restaurant.getId(), "EXPENSE")
                .orElseGet(() -> {
                    // FIX: Applied Builder Pattern
                    Tag newTag = Tag.builder()
                            .name(name.toUpperCase().trim())
                            .restaurant(restaurant)
                            .type("EXPENSE")
                            .build();
                    return tagRepository.save(newTag);
                }))
                .collect(Collectors.toSet());
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(), expense.getAmount(),
                expense.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                expense.getDescription(),
                expense.getFile() != null ? expense.getFile().getId() : null,
                expense.getStatus(), expense.getCreatedAt()
        );
    }
}

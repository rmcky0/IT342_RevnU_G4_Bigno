package com.revnu.backend.features.expenses.service;

import java.time.LocalDate;
import java.util.Locale;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.categories.model.Category;
import com.revnu.backend.features.categories.repository.CategoryRepository;
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

@Service
public class ExpenseService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "png", "pdf");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
    );

    private final ExpenseRepository expenseRepository;
    private final FileRecordRepository fileRecordRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final SupabaseStorageService supabaseStorageService;

    public ExpenseService(ExpenseRepository expenseRepository, FileRecordRepository fileRecordRepository,
            UserRepository userRepository, RestaurantRepository restaurantRepository,
            CategoryRepository categoryRepository, SupabaseStorageService supabaseStorageService) {
        this.expenseRepository = expenseRepository;
        this.fileRecordRepository = fileRecordRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.categoryRepository = categoryRepository;
        this.supabaseStorageService = supabaseStorageService;
    }

    @Transactional
    public ExpenseResponse recordExpense(String ownerEmail, ExpenseRequest request) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        Category category = resolveCategory(request.categoryId(), restaurant);

        Expense expense = Expense.builder()
                .amount(request.amount())
                .notes(request.notes())
                .category(category)
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
        expense.setNotes(request.notes());
        expense.setCategory(resolveCategory(request.categoryId(), restaurant));

        return mapToResponse(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getAllExpenses(String ownerEmail, int page, int size) {
        Restaurant restaurant = getRestaurant(getUser(ownerEmail));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return expenseRepository.findByRestaurantAndStatus(restaurant, ExpenseStatus.OPEN, pageable).map(this::mapToResponse);
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

        if (expense.getFile() != null) {
            FileRecord file = expense.getFile();
            expense.setFile(null);
            expenseRepository.save(expense);
            supabaseStorageService.deleteFile(file.getFilepath());
            fileRecordRepository.delete(file);
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

        if (expense.getFile() != null) {
            FileRecord oldFile = expense.getFile();
            expense.setFile(null);
            expenseRepository.save(expense);
            supabaseStorageService.deleteFile(oldFile.getFilepath());
            fileRecordRepository.delete(oldFile);
        }

        validateFileType(file);
        String publicUrl = supabaseStorageService.uploadFile(file, "receipts");

        FileRecord fileRecord = new FileRecord();
        fileRecord.setFilename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "receipt");
        fileRecord.setFilepath(publicUrl);
        fileRecord.setFiletype(file.getContentType() != null ? file.getContentType() : "application/octet-stream");

        expense.setFile(fileRecordRepository.save(fileRecord));
        return mapToResponse(expenseRepository.save(expense));
    }

    private Category resolveCategory(UUID categoryId, Restaurant restaurant) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Category not found."));

        boolean isOwned = category.getRestaurant() != null
                && category.getRestaurant().getId().equals(restaurant.getId());
        boolean isDefault = category.isDefault();

        if (!isDefault && !isOwned) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Category does not belong to this restaurant.");
        }
        if (!"EXPENSE".equalsIgnoreCase(category.getType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Category type must be EXPENSE for expense records.");
        }
        return category;
    }

    private void validateFileType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required.");
        }

        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid file type. Only .jpg, .png, and .pdf are allowed.");
        }

        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid file type. Only .jpg, .png, and .pdf are allowed.");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isBlank() || !filename.contains(".")) {
            return null;
        }
        String ext = filename.substring(filename.lastIndexOf('.') + 1);
        return ext.toLowerCase(Locale.ROOT);
    }

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

    private ExpenseResponse mapToResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getCategory().getId(),
                expense.getCategory().getName(),
                expense.getNotes(),
                expense.getFile() != null ? expense.getFile().getId() : null,
                expense.getStatus(),
                expense.getCreatedAt()
        );
    }
}

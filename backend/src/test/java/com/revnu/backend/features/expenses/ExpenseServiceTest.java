package com.revnu.backend.features.expenses;

import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.categories.model.Category;
import com.revnu.backend.features.categories.repository.CategoryRepository;
import com.revnu.backend.features.expenses.dto.ExpenseRequest;
import com.revnu.backend.features.expenses.model.Expense;
import com.revnu.backend.features.expenses.model.ExpenseStatus;
import com.revnu.backend.features.expenses.repository.ExpenseRepository;
import com.revnu.backend.features.expenses.service.ExpenseService;
import com.revnu.backend.features.files.model.FileRecord;
import com.revnu.backend.features.files.repository.FileRecordRepository;
import com.revnu.backend.features.files.service.SupabaseStorageService;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseService Unit Tests")
class ExpenseServiceTest {

    @Mock private ExpenseRepository expenseRepository;
    @Mock private FileRecordRepository fileRecordRepository;
    @Mock private UserRepository userRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private SupabaseStorageService supabaseStorageService;

    @InjectMocks
    private ExpenseService expenseService;

    private User owner;
    private Restaurant restaurant;
    private Restaurant otherRestaurant;
    private Category expenseCategory;
    private Category saleCategory;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(UUID.randomUUID()).email("owner@test.com").build();
        restaurant = Restaurant.builder().id(UUID.randomUUID()).name("My Restaurant").owner(owner).build();
        otherRestaurant = Restaurant.builder().id(UUID.randomUUID()).name("Other Restaurant").build();

        expenseCategory = Category.builder()
                .id(UUID.randomUUID()).name("Utilities").type("EXPENSE")
                .restaurant(null).isDefault(true).build();

        saleCategory = Category.builder()
                .id(UUID.randomUUID()).name("Meals").type("SALE")
                .restaurant(null).isDefault(true).build();

        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(restaurantRepository.findByOwner(owner)).thenReturn(Optional.of(restaurant));
    }

    private Expense buildExpense(ExpenseStatus status, FileRecord file) {
        Expense expense = new Expense();
        expense.setId(UUID.randomUUID());
        expense.setAmount(new BigDecimal("100.00"));
        expense.setCategory(expenseCategory);
        expense.setRestaurant(restaurant);
        expense.setStatus(status);
        expense.setFile(file);
        return expense;
    }

    // ── Business Rule 4: Finalized expense cannot be updated ────────────────────
    @Test
    @DisplayName("Rule 4 - updateExpense() throws for CLOSED expense")
    void updateExpense_closedExpense_throwsIllegalState() {
        UUID expenseId = UUID.randomUUID();
        Expense closed = buildExpense(ExpenseStatus.CLOSED, null);
        closed.setId(expenseId);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(closed));

        ExpenseRequest req = new ExpenseRequest(new BigDecimal("200.00"), expenseCategory.getId(), null);

        assertThatThrownBy(() -> expenseService.updateExpense("owner@test.com", expenseId, req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("finalized");
    }

    // ── Business Rule 4: Finalized expense cannot be deleted ────────────────────
    @Test
    @DisplayName("Rule 4 - deleteExpense() throws for CLOSED expense")
    void deleteExpense_closedExpense_throwsIllegalState() {
        UUID expenseId = UUID.randomUUID();
        Expense closed = buildExpense(ExpenseStatus.CLOSED, null);
        closed.setId(expenseId);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(closed));

        assertThatThrownBy(() -> expenseService.deleteExpense("owner@test.com", expenseId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("finalized");
    }

    // ── Business Rule 9: Deleting expense also deletes attached file ─────────────
    @Test
    @DisplayName("Rule 9 - deleteExpense() deletes attached receipt file from storage")
    void deleteExpense_withReceipt_deletesFileFromStorage() {
        UUID expenseId = UUID.randomUUID();
        FileRecord fileRecord = new FileRecord();
        fileRecord.setId(UUID.randomUUID());
        fileRecord.setFilepath("receipts/some-file.jpg");
        Expense openExpense = buildExpense(ExpenseStatus.OPEN, fileRecord);
        openExpense.setId(expenseId);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(openExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(openExpense);

        expenseService.deleteExpense("owner@test.com", expenseId);

        verify(supabaseStorageService).deleteFile("receipts/some-file.jpg");
        verify(fileRecordRepository).delete(fileRecord);
        verify(expenseRepository).delete(openExpense);
    }

    // ── Business Rule 9: Only jpg, png, pdf accepted for receipts ────────────────
    @Test
    @DisplayName("Rule 9 - uploadReceipt() rejects invalid file types (e.g. .exe)")
    void uploadReceipt_invalidExtension_throwsBadRequest() {
        UUID expenseId = UUID.randomUUID();
        Expense openExpense = buildExpense(ExpenseStatus.OPEN, null);
        openExpense.setId(expenseId);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(openExpense));

        MockMultipartFile maliciousFile = new MockMultipartFile(
                "file", "malware.exe", "application/octet-stream", new byte[]{1, 2, 3});

        assertThatThrownBy(() -> expenseService.uploadReceipt("owner@test.com", expenseId, maliciousFile))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    // ── Business Rule 13: Finalized expense receipt cannot be modified ────────────
    @Test
    @DisplayName("Rule 13 - uploadReceipt() throws for CLOSED (finalized) expense")
    void uploadReceipt_finalizedExpense_throwsIllegalState() {
        UUID expenseId = UUID.randomUUID();
        Expense closedExpense = buildExpense(ExpenseStatus.CLOSED, null);
        closedExpense.setId(expenseId);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(closedExpense));

        MockMultipartFile file = new MockMultipartFile(
                "file", "receipt.jpg", "image/jpeg", new byte[]{1, 2, 3});

        assertThatThrownBy(() -> expenseService.uploadReceipt("owner@test.com", expenseId, file))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("finalized");
    }

    // ── Business Rule 13: New receipt overwrites existing one (1:1 mapping) ───────
    @Test
    @DisplayName("Rule 13 - uploadReceipt() deletes old file before uploading new one")
    void uploadReceipt_existingFile_overwritesPreviousFile() {
        UUID expenseId = UUID.randomUUID();
        FileRecord oldFile = new FileRecord();
        oldFile.setId(UUID.randomUUID());
        oldFile.setFilepath("receipts/old.jpg");
        Expense openExpense = buildExpense(ExpenseStatus.OPEN, oldFile);
        openExpense.setId(expenseId);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(openExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(openExpense);
        when(supabaseStorageService.uploadFile(any(), anyString())).thenReturn("receipts/new.jpg");
        FileRecord newFileRecord = new FileRecord();
        newFileRecord.setId(UUID.randomUUID());
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(newFileRecord);

        MockMultipartFile newFile = new MockMultipartFile(
                "file", "new-receipt.jpg", "image/jpeg", new byte[]{1, 2, 3});

        expenseService.uploadReceipt("owner@test.com", expenseId, newFile);

        verify(supabaseStorageService).deleteFile("receipts/old.jpg");
        verify(fileRecordRepository).delete(oldFile);
        verify(supabaseStorageService).uploadFile(any(), eq("receipts"));
    }

    // ── Business Rule 12: Category type segregation — SALE category rejected ─────
    @Test
    @DisplayName("Rule 12 - recordExpense() rejects SALE category type")
    void recordExpense_withSaleCategory_throwsBadRequest() {
        when(categoryRepository.findById(saleCategory.getId())).thenReturn(Optional.of(saleCategory));
        ExpenseRequest req = new ExpenseRequest(new BigDecimal("100.00"), saleCategory.getId(), null);

        assertThatThrownBy(() -> expenseService.recordExpense("owner@test.com", req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    // ── Business Rule 2: Tenant Isolation ────────────────────────────────────────
    @Test
    @DisplayName("Rule 2 - deleteExpense() blocks access to another restaurant's record")
    void deleteExpense_foreignRestaurant_throwsSecurityException() {
        UUID expenseId = UUID.randomUUID();
        Expense foreignExpense = buildExpense(ExpenseStatus.OPEN, null);
        foreignExpense.setId(expenseId);
        foreignExpense.setRestaurant(otherRestaurant);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(foreignExpense));

        assertThatThrownBy(() -> expenseService.deleteExpense("owner@test.com", expenseId))
                .isInstanceOf(SecurityException.class);
    }

    // ── Business Rule 9: Valid file types are accepted ───────────────────────────
    @Test
    @DisplayName("Rule 9 - uploadReceipt() accepts .jpg, .png, and .pdf files")
    void uploadReceipt_validTypes_noException() {
        UUID expenseId = UUID.randomUUID();
        Expense openExpense = buildExpense(ExpenseStatus.OPEN, null);
        openExpense.setId(expenseId);
        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(openExpense));
        when(supabaseStorageService.uploadFile(any(), anyString())).thenReturn("receipts/receipt.pdf");
        FileRecord saved = new FileRecord();
        saved.setId(UUID.randomUUID());
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(saved);
        when(expenseRepository.save(any(Expense.class))).thenReturn(openExpense);

        MockMultipartFile pdfFile = new MockMultipartFile(
                "file", "receipt.pdf", "application/pdf", new byte[]{1, 2, 3});

        expenseService.uploadReceipt("owner@test.com", expenseId, pdfFile);

        verify(supabaseStorageService).uploadFile(any(), eq("receipts"));
    }
}

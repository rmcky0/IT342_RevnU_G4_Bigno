package com.revnu.backend.features.sales;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.revnu.backend.features.auth.model.RoleType;
import com.revnu.backend.features.auth.model.User;
import com.revnu.backend.features.auth.repository.UserRepository;
import com.revnu.backend.features.restaurants.model.Restaurant;
import com.revnu.backend.features.restaurants.repository.RestaurantRepository;
import com.revnu.backend.features.sales.dto.SaleRequest;
import com.revnu.backend.features.sales.dto.SaleResponse;
import com.revnu.backend.features.sales.model.Sale;
import com.revnu.backend.features.sales.model.SaleStatus;
import com.revnu.backend.features.sales.repository.SaleRepository;
import com.revnu.backend.features.sales.service.SaleService;
import com.revnu.backend.features.tags.repository.TagRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaleService Tests")
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private SaleService saleService;

    private User testUser;
    private Restaurant testRestaurant;
    private UUID testRestaurantId;
    private UUID testSaleId;

    @BeforeEach
    void setUp() {
        testRestaurantId = UUID.randomUUID();
        testSaleId = UUID.randomUUID();

        testUser = new User();
        testUser.setEmail("tenant@revnu.com");
        testUser.setFullname("Test Tenant");
        testUser.setRole(RoleType.TENANT);

        testRestaurant = new Restaurant();
        testRestaurant.setId(testRestaurantId);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setOwner(testUser);
    }

    private Sale buildSale(BigDecimal amount, SaleStatus status) {
        Sale sale = new Sale();
        sale.setId(testSaleId);
        sale.setAmount(amount);
        sale.setDescription("Test sale");
        sale.setTags(Set.of());
        sale.setRestaurant(testRestaurant);
        sale.setStatus(status);
        return sale;
    }

    private void stubUserAndRestaurant() {
        when(userRepository.findByEmail("tenant@revnu.com"))
                .thenReturn(Optional.of(testUser));
        when(restaurantRepository.findByOwner(testUser))
                .thenReturn(Optional.of(testRestaurant));
    }

    @Test
    @DisplayName("Record sale — saves and returns SaleResponse")
    void recordSale_validRequest_savesAndReturnsSaleResponse() {
        stubUserAndRestaurant();
        SaleRequest request = new SaleRequest(
                new BigDecimal("150.00"), List.of(), "Lunch sale");

        Sale saved = buildSale(new BigDecimal("150.00"), SaleStatus.OPEN);
        when(saleRepository.save(any(Sale.class))).thenReturn(saved);

        SaleResponse result = saleService.recordSale("tenant@revnu.com", request);

        assertNotNull(result, "Result should not be null");
        assertEquals(0, new BigDecimal("150.00").compareTo(result.amount()),
                "Amount should be 150.00");
        assertEquals(SaleStatus.OPEN, result.status(), "New sale should be OPEN");

        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    @DisplayName("Record sale — restaurant not found throws exception")
    void recordSale_noRestaurant_throwsRuntimeException() {
        when(userRepository.findByEmail("tenant@revnu.com"))
                .thenReturn(Optional.of(testUser));
        when(restaurantRepository.findByOwner(testUser))
                .thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, ()
                -> saleService.recordSale("tenant@revnu.com",
                        new SaleRequest(new BigDecimal("100.00"), List.of(), "Test")),
                "Should throw when restaurant not found"
        );

        verify(saleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Record sale — user not found throws exception")
    void recordSale_unknownUser_throwsRuntimeException() {
        when(userRepository.findByEmail("ghost@revnu.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, ()
                -> saleService.recordSale("ghost@revnu.com",
                        new SaleRequest(new BigDecimal("100.00"), List.of(), "Test"))
        );
    }

    @Test
    @DisplayName("Get all sales — returns paginated response")
    void getAllSales_validOwner_returnsPage() {
        stubUserAndRestaurant();

        List<Sale> saleList = List.of(
                buildSale(new BigDecimal("100.00"), SaleStatus.OPEN),
                buildSale(new BigDecimal("200.00"), SaleStatus.OPEN)
        );
        Page<Sale> salePage = new PageImpl<>(saleList);
        when(saleRepository.findByRestaurant(eq(testRestaurant), any(Pageable.class)))
                .thenReturn(salePage);

        Page<SaleResponse> result = saleService.getAllSales("tenant@revnu.com", 0, 9);

        assertNotNull(result, "Page should not be null");
        assertEquals(2, result.getContent().size(), "Should return 2 sales");
    }

    @Test
    @DisplayName("Update sale — changes amount and returns updated response")
    void updateSale_openSale_updatesAndReturns() {
        stubUserAndRestaurant();

        Sale existingSale = buildSale(new BigDecimal("100.00"), SaleStatus.OPEN);
        when(saleRepository.findById(testSaleId))
                .thenReturn(Optional.of(existingSale));

        Sale updatedSale = buildSale(new BigDecimal("250.00"), SaleStatus.OPEN);
        when(saleRepository.save(any(Sale.class))).thenReturn(updatedSale);

        SaleResponse result = saleService.updateSale("tenant@revnu.com", testSaleId,
                new SaleRequest(new BigDecimal("250.00"), List.of(), "Updated"));

        assertNotNull(result);
        assertEquals(0, new BigDecimal("250.00").compareTo(result.amount()),
                "Amount should be updated to 250.00");
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    @DisplayName("Update CLOSED sale — throws exception (finalized records locked)")
    void updateSale_closedSale_throwsIllegalStateException() {
        stubUserAndRestaurant();

        Sale closedSale = buildSale(new BigDecimal("100.00"), SaleStatus.CLOSED);
        when(saleRepository.findById(testSaleId))
                .thenReturn(Optional.of(closedSale));

        assertThrows(IllegalStateException.class, ()
                -> saleService.updateSale("tenant@revnu.com", testSaleId,
                        new SaleRequest(new BigDecimal("250.00"), List.of(), "Try update")),
                "Should not allow editing finalized sale"
        );

        verify(saleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete open sale — removes record")
    void deleteSale_openSale_deletesSuccessfully() {
        stubUserAndRestaurant();

        Sale openSale = buildSale(new BigDecimal("100.00"), SaleStatus.OPEN);
        when(saleRepository.findById(testSaleId))
                .thenReturn(Optional.of(openSale));

        saleService.deleteSale("tenant@revnu.com", testSaleId);

        verify(saleRepository, times(1)).delete(openSale);
    }

    @Test
    @DisplayName("Delete CLOSED sale — throws exception (finalized records locked)")
    void deleteSale_closedSale_throwsIllegalStateException() {
        stubUserAndRestaurant();

        Sale closedSale = buildSale(new BigDecimal("100.00"), SaleStatus.CLOSED);
        when(saleRepository.findById(testSaleId))
                .thenReturn(Optional.of(closedSale));

        assertThrows(IllegalStateException.class, ()
                -> saleService.deleteSale("tenant@revnu.com", testSaleId),
                "Should not delete a finalized sale"
        );

        verify(saleRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Delete sale — wrong restaurant owner throws SecurityException")
    void deleteSale_wrongOwner_throwsSecurityException() {
        stubUserAndRestaurant();

        Restaurant otherRestaurant = new Restaurant();
        otherRestaurant.setId(UUID.randomUUID());

        Sale saleFromOtherRestaurant = new Sale();
        saleFromOtherRestaurant.setId(testSaleId);
        saleFromOtherRestaurant.setAmount(new BigDecimal("100.00"));
        saleFromOtherRestaurant.setRestaurant(otherRestaurant);
        saleFromOtherRestaurant.setStatus(SaleStatus.OPEN);

        when(saleRepository.findById(testSaleId))
                .thenReturn(Optional.of(saleFromOtherRestaurant));

        assertThrows(SecurityException.class, ()
                -> saleService.deleteSale("tenant@revnu.com", testSaleId),
                "Should throw SecurityException for unauthorized access"
        );

        verify(saleRepository, never()).delete(any());
    }
}

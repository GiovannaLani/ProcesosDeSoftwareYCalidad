package com.spq.vinted.performance;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.JUnitPerfReportingConfig;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestActiveConfig;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;
import com.spq.vinted.model.Category;
import com.spq.vinted.model.Clothes;
import com.spq.vinted.model.ClothesSize;
import com.spq.vinted.model.ClothesType;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.UserRepository;
import com.spq.vinted.service.ChatRoomService;
import com.spq.vinted.service.ItemService;
import com.spq.vinted.service.MessageService;
import com.spq.vinted.service.OfferService;
import com.spq.vinted.service.PurchaseService;

import jakarta.transaction.Transactional;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@ExtendWith(JUnitPerfInterceptor.class)
@Transactional
public class PerformanceTest {

    @Autowired
    private ItemService itemService;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserRepository userRepository;

    private static Long testItemId;
    private static Long testUserId;

    @JUnitPerfTestActiveConfig
    private static final JUnitPerfReportingConfig PERF_CONFIG = JUnitPerfReportingConfig.builder()
            .reportGenerator(new HtmlReportGenerator("target/reports/item-service-performance.html"))
            .build();

    @BeforeAll
    static void setupData(
            @Autowired ItemRepository itemRepo,
            @Autowired UserRepository userRepo) {
        
        // Crear usuario de prueba con TODOS los campos obligatorios
        User testUser = new User();
        testUser.setName("Test User");
        testUser.setSurname("Performance"); // Campo requerido
        testUser.setUsername("testuser");
        testUser.setEmail("test@performance.com");
        testUser.setPassword("securepassword");
        
        testUser = userRepo.save(testUser);
        testUserId = testUser.getId();
        
        // Crear ítem de prueba con todos los campos requeridos
        Clothes testItem = new Clothes();
        testItem.setTitle("Camiseta Performance Test");
        testItem.setDescription("Descripción para tests");
        testItem.setPrice(19.99f);
        testItem.setCategory(Category.MAN);
        testItem.setSeller(testUser);
        testItem.setSize(ClothesSize.M);
        testItem.setClothesType(ClothesType.TSHIRT);
        testItem.setImages(new ArrayList<>());

        
        testItem = itemRepo.save(testItem);
        testItemId = testItem.getId();
    }

    @Test
    @JUnitPerfTest(threads = 100, durationMs = 15_000, warmUpMs = 3_000)
    @JUnitPerfTestRequirement(executionsPerSec = 50, percentiles = "95:300ms")
    public void addItemToCart_Performance() {
        itemService.addItemToCart(testUserId, testItemId);
    }

    @Test
    @JUnitPerfTest(threads = 50, durationMs = 10_000)
    @JUnitPerfTestRequirement(executionsPerSec = 50, percentiles = "95:300ms")
    public void removeItemFromCart_Performance() {
        // Primero añadir el item al carrito
        itemService.addItemToCart(testUserId, testItemId);
        // Luego intentar removerlo
        itemService.removeItemFromCart(testUserId, testItemId);
    }

    @Test
    @JUnitPerfTest(threads = 200, durationMs = 20_000)
    @JUnitPerfTestRequirement(executionsPerSec = 50, percentiles = "95:300ms")
    public void getCartItems_Performance() {
        itemService.getCartItems(testUserId);
    }

    @Test
    @JUnitPerfTest(threads = 50, durationMs = 10_000)
    @JUnitPerfTestRequirement(executionsPerSec = 50, percentiles = "95:300ms")
    public void searchItems_Performance() {
        // Test con diferentes queries
        String[] queries = {"camiseta", "móvil", "libro", "sofá"};
        itemService.searchItems(testUserId, queries[ThreadLocalRandom.current().nextInt(queries.length)]);
    }

    @Test
    @JUnitPerfTest(threads = 30, durationMs = 8_000)
    @JUnitPerfTestRequirement(executionsPerSec = 50, percentiles = "95:300ms")
    public void getClothesByCategory_Performance() {
        Category[] categories = Category.values();
        itemService.getClothesByCategory(categories[ThreadLocalRandom.current().nextInt(categories.length)]);
    }

    @Test
    @JUnitPerfTest(threads = 20, durationMs = 12_000)
    @JUnitPerfTestRequirement(executionsPerSec = 50, percentiles = "95:300ms")
    @Transactional
    public void deleteItem_Performance() {
        // Requiere pre-insertar items para eliminar
        itemService.deleteItem(testUserId, testItemId);
    }

    @JUnitPerfTest(threads = 40, durationMs = 10_000)
    @JUnitPerfTestRequirement(executionsPerSec = 50, percentiles = "95:300ms")
    public void uploadItemImages_Performance() throws IOException {
        // Mock de MultipartFile
        MultipartFile mockFile = new MockMultipartFile(
            "test.jpg", "test.jpg", "image/jpeg", "test".getBytes());
        itemService.uploadItemImages(testItemId, List.of(mockFile));
    }
}

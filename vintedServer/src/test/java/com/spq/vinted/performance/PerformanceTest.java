package com.spq.vinted.performance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.beans.factory.annotation.Autowired;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.JUnitPerfReportingConfig;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestActiveConfig;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;
import com.spq.vinted.model.Home;
import com.spq.vinted.model.HomeType;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.UserRepository;
import com.spq.vinted.service.ItemService;
import com.spq.vinted.service.UserService;

import jakarta.transaction.Transactional;

@SpringBootTest
@ExtendWith(JUnitPerfInterceptor.class)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PerformanceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Item item;

    @JUnitPerfTestActiveConfig
    private static final JUnitPerfReportingConfig PERF_CONFIG = JUnitPerfReportingConfig.builder()
    .reportGenerator(new HtmlReportGenerator("target/reports/item-service-performance.html"))
    .build();

    @BeforeAll
    public void setUp() throws InterruptedException {
        userRepository.deleteAll();
        itemRepository.deleteAll();

        userService.createUser("user@user.com", "password", "userName", "name", "surname");
        user = userRepository.findByEmail("user@user.com").orElseThrow();

        item = new Home("Test Item", "Test Description", 10.0f, user, HomeType.DECORATION);
        itemService.saveItem(item);
    }
    
    @Test
    @JUnitPerfTest(threads = 10, durationMs = 10000, warmUpMs = 2000)
    @JUnitPerfTestRequirement(executionsPerSec = 20, percentiles = "95:300ms")
    public void testLogin() {
        userService.logIn("user@user.com", "password");
        
    }
 
    @Test
    @JUnitPerfTest(threads = 10, durationMs = 10000, warmUpMs = 2000)
    @JUnitPerfTestRequirement(executionsPerSec = 20, percentiles = "95:300ms")
    public void getItemsById() {
        itemService.getItemById(item.getId());
    }
 
    @Test
    @JUnitPerfTest(threads = 10, durationMs = 10000, warmUpMs = 2000)
    @JUnitPerfTestRequirement(executionsPerSec = 20, percentiles = "95:300ms")
    public void getSaveItem() {
        Item newItem = new Home("Another Test Item", "Another Description", 15.0f, user, HomeType.DECORATION);
        itemService.saveItem(newItem);
    }

}

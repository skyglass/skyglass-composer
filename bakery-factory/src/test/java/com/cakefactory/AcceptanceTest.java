package com.cakefactory;

import com.cakefactory.client.BrowserClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class AcceptanceTest {

    @Autowired
    protected MockMvc mockMvc;

    protected BrowserClient client;

    @BeforeEach
    void setUp() {
        client = new BrowserClient(mockMvc);
    }
}

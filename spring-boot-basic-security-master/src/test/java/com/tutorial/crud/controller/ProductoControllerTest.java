package com.tutorial.crud.controller;



import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.tutorial.crud.service.ProductoService;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {
    @MockBean
    private ProductoService productoService;

    @Autowired
    private MockMvc mockMvc;


    @InjectMocks
    private ProductoController productoController;


    @Test
    @WithMockUser (roles = "ADMIN") // Simula un usuario con rol ADMIN
    void deberiadevolverproductos() throws Exception{
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/lista"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("lista"));
    }
    
}



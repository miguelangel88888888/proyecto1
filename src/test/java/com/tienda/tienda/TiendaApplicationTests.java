package com.tienda.tienda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.tienda.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class TiendaApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository repository;

    @BeforeEach
    void limpiarProductos() {
        repository.deleteAll();
    }

    @Test
    void permiteCrearConsultarActualizarYEliminarProducto() throws Exception {
        String producto = """
                {
                  "nombre": "Camiseta",
                  "descripcion": "Algodon",
                  "precio": 35000,
                  "stock": 20
                }
                """;

        MvcResult resultadoCreacion = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(producto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Camiseta")))
                .andReturn();

        Long id = objectMapper.readTree(resultadoCreacion.getResponse().getContentAsString())
                .get("id").asLong();

        mockMvc.perform(get("/api/productos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is("Algodon")));

        mockMvc.perform(put("/api/productos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Camiseta nueva",
                                  "descripcion": "Algodon organico",
                                  "precio": 40000,
                                  "stock": 10
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Camiseta nueva")))
                .andExpect(jsonPath("$.stock", is(10)));

        mockMvc.perform(delete("/api/productos/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/productos/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void rechazaProductoConNombreVacioPrecioNegativoOStockNegativo() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": " ",
                                  "descripcion": "Producto invalido",
                                  "precio": -1,
                                  "stock": -1
                                }
                                """))
                .andExpect(status().isBadRequest());

        org.junit.jupiter.api.Assertions.assertTrue(repository.findAll().isEmpty());
    }
}

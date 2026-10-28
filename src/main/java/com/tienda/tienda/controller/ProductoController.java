package com.tienda.tienda.controller;

import com.tienda.tienda.model.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    // Lista en memoria: simula la base de datos
    private final List<Producto> productos = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(2);

    public ProductoController() {
        productos.add(new Producto(1L, "Camiseta", 35000, 20));
        productos.add(new Producto(2L, "Gorra", 25000, 15));
    }

    // GET /api/productos -> listar todos
    @GetMapping
    public List<Producto> listar() {
        return productos;
    }

    // GET /api/productos/{id} -> buscar uno
    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/productos -> crear
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        producto.setId(contador.incrementAndGet());
        productos.add(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    // PUT /api/productos/{id} -> actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto datos) {
        for (Producto p : productos) {
            if (p.getId().equals(id)) {
                p.setNombre(datos.getNombre());
                p.setPrecio(datos.getPrecio());
                p.setStock(datos.getStock());
                return ResponseEntity.ok(p);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /api/productos/{id} -> eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = productos.removeIf(p -> p.getId().equals(id));
        return eliminado ? ResponseEntity.noContent().build()
                         : ResponseEntity.notFound().build();
    }
}

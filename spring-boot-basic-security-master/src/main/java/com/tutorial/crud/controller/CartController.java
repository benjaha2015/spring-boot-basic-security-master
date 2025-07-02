package com.tutorial.crud.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tutorial.crud.entity.DetalleOrden;
import com.tutorial.crud.entity.Orden;
import com.tutorial.crud.entity.Producto;
import com.tutorial.crud.service.IDetalleOrdenService;
import com.tutorial.crud.service.IOrdenService;
import com.tutorial.crud.service.ProductoService;
import com.tutorial.crud.service.UsuarioService;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private IOrdenService ordenService;

    @Autowired
    private IDetalleOrdenService detalleOrdenService;

    // ELIMINAMOS LAS VARIABLES DE INSTANCIA. El controlador ya no guarda el estado.

    @PostMapping("")
    public String addToCart(@RequestParam Long id, @RequestParam Integer cantidad, HttpSession session) {
        logger.info("-> POST /cart: Añadiendo producto {}", id);

        // 1. OBTENER EL CARRITO DE LA SESIÓN
        List<DetalleOrden> detalles = getDetallesFromSession(session);
        Orden orden = getOrdenFromSession(session);

        Optional<Producto> optionalProducto = productoService.getOne(id);
        if (optionalProducto.isEmpty()) {
            logger.error("Producto no encontrado para el ID: {}", id);
            return "redirect:/";
        }

        Producto producto = optionalProducto.get();
        boolean yaExiste = detalles.stream().anyMatch(p -> p.getProducto().getId().equals(producto.getId()));

        if (yaExiste) {
            // Si ya existe, actualizamos la cantidad
            detalles.forEach(d -> {
                if (d.getProducto().getId().equals(producto.getId())) {
                    d.setCantidad(d.getCantidad() + cantidad);
                    d.setTotal(d.getPrecio() * d.getCantidad());
                }
            });
        } else {
            // Si es nuevo, lo creamos y añadimos
            DetalleOrden detalleOrden = new DetalleOrden();
            detalleOrden.setCantidad(cantidad);
            detalleOrden.setPrecio(producto.getPrecio());
            detalleOrden.setNombre(producto.getNombre());
            detalleOrden.setTotal(producto.getPrecio() * cantidad);
            detalleOrden.setProducto(producto);
            detalles.add(detalleOrden);
        }

        // 2. GUARDAR EL CARRITO ACTUALIZADO EN LA SESIÓN
        orden.setTotal(calcularTotal(detalles));
        session.setAttribute("detalles", detalles);
        session.setAttribute("orden", orden);

        return "redirect:/cart";
    }

    @GetMapping("")
    public String showCart(HttpSession session, Model model) {
        logger.info("-> GET /cart: Mostrando página del carrito.");
        
        // Obtenemos todo de la sesión y lo pasamos al modelo
        model.addAttribute("cart", getDetallesFromSession(session));
        model.addAttribute("orden", getOrdenFromSession(session));
        
        return "producto/carrito";
    }

    @GetMapping("/delete/{id}")
    public String deleteProductoCart(@PathVariable Long id, HttpSession session) {
        logger.info("-> GET /cart/delete/{}", id);
        
        List<DetalleOrden> detalles = getDetallesFromSession(session);
        Orden orden = getOrdenFromSession(session);
        
        // Filtramos para quitar el producto
        List<DetalleOrden> detallesActualizados = detalles.stream()
                .filter(d -> !d.getProducto().getId().equals(id))
                .collect(Collectors.toList());

        // Guardamos los cambios en la sesión
        orden.setTotal(calcularTotal(detallesActualizados));
        session.setAttribute("detalles", detallesActualizados);
        session.setAttribute("orden", orden);

        return "redirect:/cart";
    }
    
    // --- MÉTODOS AUXILIARES PARA MANEJAR LA SESIÓN ---

    private List<DetalleOrden> getDetallesFromSession(HttpSession session) {
        List<DetalleOrden> detalles = (List<DetalleOrden>) session.getAttribute("detalles");
        if (detalles == null) {
            detalles = new ArrayList<>();
        }
        return detalles;
    }

    private Orden getOrdenFromSession(HttpSession session) {
        Orden orden = (Orden) session.getAttribute("orden");
        if (orden == null) {
            orden = new Orden();
        }
        return orden;
    }

    private double calcularTotal(List<DetalleOrden> detalles) {
        return detalles.stream().mapToDouble(DetalleOrden::getTotal).sum();
    }
    
    // El método saveOrder() no se toca, pero asegúrate de que use la sesión
    // si necesitas limpiarla al final.


@GetMapping("/convertir-divisa")
@ResponseBody
public Map<String, Object> convertirDivisa(
        @RequestParam(defaultValue = "CLP") String moneda,
        HttpSession session) {
    List<DetalleOrden> detalles = getDetallesFromSession(session);
    Orden orden = getOrdenFromSession(session);

    double tasa = 1.0;
    String simbolo = "$";
    switch (moneda) {
        case "USD":
            tasa = 925.0; // Ejemplo: 1 USD = 925 CLP
            simbolo = "US$ ";
            break;
        case "EUR":
            tasa = 1000.0; // Ejemplo: 1 EUR = 1000 CLP
            simbolo = "€ ";
            break;
        default:
            tasa = 1.0;
            simbolo = "$";
    }

    List<Map<String, Object>> detallesConvertidos = new ArrayList<>();
    for (DetalleOrden d : detalles) {
        Map<String, Object> det = new HashMap<>();
        double precio = d.getPrecio();
        double total = d.getTotal();
        if (!"CLP".equals(moneda)) {
            precio = precio / tasa;
            total = total / tasa;
        }
        det.put("nombre", d.getNombre());
        det.put("precio", String.format("%s%.2f", simbolo, precio));
        det.put("cantidad", d.getCantidad());
        det.put("total", String.format("%s%.2f", simbolo, total));
        detallesConvertidos.add(det);
    }

    double totalOrden = orden.getTotal();
    if (!"CLP".equals(moneda)) {
        totalOrden = totalOrden / tasa;
    }

    Map<String, Object> result = new HashMap<>();
    result.put("detalles", detallesConvertidos);
    result.put("total", String.format("%.2f", totalOrden));
    result.put("simbolo", simbolo);
    return result;
}


}
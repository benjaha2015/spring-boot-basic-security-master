package com.tutorial.crud.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tutorial.crud.entity.Producto;
import com.tutorial.crud.service.ProductoService;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    ProductoService productoService;

    @GetMapping("lista")
    public ModelAndView list(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/producto/lista");
        List<Producto> productos = productoService.list();
        mv.addObject("productos", productos);
        return mv;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("nuevo")
    public String nuevo(){
        return "producto/nuevo";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/guardar")
        public ModelAndView crear(@RequestParam String nombre, @RequestParam float precio, @RequestParam String codigoProducto, @RequestParam String marca, @RequestParam String categoria, @RequestParam Integer stock){
        ModelAndView mv = new ModelAndView();
        if(StringUtils.isBlank(nombre)){
            mv.setViewName("producto/nuevo");
            mv.addObject("error", "el nombre no puede estar vacío");
            return mv;
        }
        if(precio <1 ){
            mv.setViewName("producto/nuevo");
            mv.addObject("error", "el precio debe ser mayor que cero");
            return mv;
        }
        if(productoService.existsByNombre(nombre)){
            mv.setViewName("producto/nuevo");
            mv.addObject("error", "ese nombre ya existe");
            return mv;
        }
        
        Producto producto = new Producto(nombre, precio, marca, codigoProducto, categoria, stock);
        productoService.save(producto);
        mv.setViewName("redirect:/producto/lista");
        return mv;
    }

    @GetMapping("/detalle/{id}")
    public ModelAndView detalle(@PathVariable("id") Long id){
        if(!productoService.existsById(id))
            return new ModelAndView("redirect:/producto/lista");
        Producto producto = productoService.getOne(id).get();
        ModelAndView mv = new ModelAndView("/producto/detalle");
        mv.addObject("producto", producto);
        return mv;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public ModelAndView editar(@PathVariable("id") Long id){
        if(!productoService.existsById(id))
            return new ModelAndView("redirect:/producto/lista");
        Producto producto = productoService.getOne(id).get();
        ModelAndView mv = new ModelAndView("/producto/editar");
        mv.addObject("producto", producto);
        return mv;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/actualizar")
    public ModelAndView actualizar(@RequestParam Long id, @RequestParam String nombre, @RequestParam float precio, @RequestParam String codigoProducto, @RequestParam String marca, @RequestParam String categoria, @RequestParam Integer stock){
        if(!productoService.existsById(id))
            return new ModelAndView("redirect:/producto/lista");
        ModelAndView mv = new ModelAndView();
        Producto producto = productoService.getOne(id).get();
        if(StringUtils.isBlank(nombre)){
            mv.setViewName("producto/editar");
            mv.addObject("producto", producto);
            mv.addObject("error", "el nombre no puede estar vacío");
            return mv;
        }
        if(precio <1 ){
            mv.setViewName("producto/editar");
            mv.addObject("error", "el precio debe ser mayor que cero");
            mv.addObject("producto", producto);
            return mv;
        }
        if(productoService.existsByNombre(nombre) && productoService.getByNombre(nombre).get().getId() == id){
            mv.setViewName("producto/editar");
            mv.addObject("error", "ese nombre ya existe");
            mv.addObject("producto", producto);
            return mv;
        }

        producto.setNombre(nombre);
        producto.setPrecio(precio);
         producto.setCodigoProducto(codigoProducto);
        producto.setMarca(marca);
        producto.setCategoria(categoria);
        producto.setStock(stock);
        productoService.save(producto);
        return new ModelAndView("redirect:/producto/lista");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/borrar/{id}")
    public ModelAndView borrar(@PathVariable("id")Long id){
        if(productoService.existsById(id)){
            productoService.delete(id);
            return new ModelAndView("redirect:/producto/lista");
        }
        return null;
    }


}

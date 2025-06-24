package com.tutorial.crud.service;
import java.util.List;
import java.util.Optional;

import com.tutorial.crud.entity.Orden;
import com.tutorial.crud.entity.Usuario;

public interface IOrdenService {
	List<Orden> findAll();
	Optional<Orden> findById(Integer id);
	Orden save (Orden orden);
	String generarNumeroOrden();
	List<Orden> findByUsuario (Usuario usuario);
}

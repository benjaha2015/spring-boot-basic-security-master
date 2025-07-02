# Ferremas - Tienda Online con Spring Boot

Este proyecto es una tienda online desarrollada en Spring Boot, que incluye:
- Carrito de compras
- Conversión visual de divisas
- Pago seguro con Webpay Plus

---

## Tabla de Contenidos
- [Requisitos](#requisitos)
- [Configuración](#configuración)
- [Estructura de Carpetas](#estructura-de-carpetas)
- [Principales Endpoints y Funcionalidades](#principales-endpoints-y-funcionalidades)
  - [Carrito de Compras](#carrito-de-compras)
  - [Conversión de Divisas](#conversión-de-divisas)
  - [Pago con Webpay Plus](#pago-con-webpay-plus)
- [Notas](#notas)
- [Licencia](#licencia)

---

## Requisitos
- Java 8+
- Maven
- MySQL/MariaDB

---

## Configuración
1. Configura tu base de datos en `src/main/resources/application.properties`.
2. Configura las credenciales de Webpay Plus de integración:
    ```properties
    transbank.commerceCode=597055555532
    transbank.apiKey=020c13fa6a994bfaaf3c0b1c6a2a1a2a
    transbank.environment=INTEGRATION
    ```
3. Ejecuta la aplicación con:
    ```
    mvn spring-boot:run
    ```

---

## Estructura de Carpetas
- `src/main/java/com/tutorial/crud/controller/` - Controladores de la aplicación (Carrito, Webpay, etc.)
- `src/main/resources/templates/producto/` - Vistas Thymeleaf (carrito, pago, confirmación, etc.)
- `src/main/resources/application.properties` - Configuración de la aplicación

---

## Principales Endpoints y Funcionalidades

### Carrito de Compras

- **GET `/cart`**  
  Muestra el carrito de compras actual del usuario.
- **POST `/cart`**  
  Agrega un producto al carrito.
- **GET `/cart/delete/{id}`**  
  Elimina un producto del carrito.

#### Conversión de Divisas

- **GET `/cart/convertir-divisa?moneda=CLP|USD|EUR`**  
  Devuelve los precios del carrito y el total convertidos a la moneda seleccionada.  
  - **Parámetro:**  
    - `moneda`: `"CLP"`, `"USD"` o `"EUR"`
  - **Respuesta:**  
    ```json
    {
      "detalles": [
        {
          "nombre": "Producto",
          "precio": "US$ 10.00",
          "cantidad": 1,
          "total": "US$ 10.00"
        }
      ],
      "total": "US$ 10.00",
      "simbolo": "US$ "
    }
    ```
  - **Nota:** El cambio de divisa es solo visual. El pago siempre se realiza en pesos chilenos (CLP).

### Pago con Webpay Plus

- **POST `/webpay/create`**  
  Inicia una transacción Webpay Plus.  
  - **Parámetros:**  
    - `buyOrder`: Orden de compra única
    - `sessionId`: Sesión única
    - `amount`: Monto en CLP
  - **Respuesta:**  
    Redirige automáticamente a Webpay para completar el pago.

- **POST `/webpay/return`**  
  Endpoint de retorno de Webpay.  
  - **Parámetro:**  
    - `token`: Token de la transacción entregado por Webpay
  - **Funcionalidad:**  
    - Valida la transacción y muestra una página de confirmación de compra (`compra.html`).
    - Si ocurre un error, muestra una página de error.

---

## Notas
- El cambio de divisa en el carrito es solo visual. El monto enviado a Webpay siempre será en CLP.
- El sistema utiliza Thymeleaf para renderizar las vistas y JavaScript para actualizar los precios en tiempo real.
- Las credenciales de Webpay Plus en modo integración son públicas y solo para pruebas.

---

## Licencia
MIT

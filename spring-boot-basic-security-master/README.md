# Documentación de Endpoints y Funcionalidad

## Autenticación y Usuarios

- **GET `/login`**  
  Muestra el formulario de inicio de sesión.

- **GET `/usuario/registro`**  
  Muestra el formulario de registro de usuario.

- **POST `/usuario/registrar`**  
  Registra un nuevo usuario.  
  **Parámetros:**  
  - `nombreUsuario` (String)  
  - `password` (String)  
  **Respuestas:**  
  - Redirige a login si el registro es exitoso.
  - Muestra errores si el usuario ya existe o los campos están vacíos.

## Productos

- **GET `/producto/lista`**  
  Lista todos los productos.

- **GET `/producto/detalle/{id}`**  
  Muestra el detalle de un producto específico.

- **GET `/producto/nuevo`**  
  (Solo ADMIN) Muestra el formulario para crear un nuevo producto.

- **POST `/producto/guardar`**  
  (Solo ADMIN) Crea un nuevo producto.  
  **Parámetros:**  
  - `nombre`, `precio`, `codigoProducto`, `marca`, `categoria`, `stock`

- **GET `/producto/editar/{id}`**  
  (Solo ADMIN) Muestra el formulario para editar un producto.

- **POST `/producto/actualizar`**  
  (Solo ADMIN) Actualiza un producto existente.

- **GET `/producto/borrar/{id}`**  
  (Solo ADMIN) Elimina un producto.

## Carrito de Compras

- **POST `/cart`**  
  Agrega un producto al carrito.  
  **Parámetros:**  
  - `id` (Long, id del producto)  
  - `cantidad` (Integer)

- **GET `/delete/cart/{id}`**  
  Elimina un producto del carrito.

- **GET `/getCart`**  
  Muestra el carrito actual del usuario.

## Webpay (Pagos)

- **GET `/webpay`**  
  Muestra el formulario de pago.

- **POST `/webpay`**  
  Inicia una transacción de pago con Webpay.  
  **Parámetros:**  
  - `amount` (double, monto a pagar)

- **GET `/return`**  
  Endpoint de retorno de Webpay para manejar la respuesta de la transacción.

---

### Notas de Seguridad

- Los endpoints de administración de productos requieren el rol `ADMIN`.
- El registro y login están abiertos a todos los usuarios.
- El sistema utiliza Spring Security para la autenticación y autorización.

---

## Requisitos Funcionales

1. El sistema debe permitir a los usuarios registrarse y autenticarse mediante un formulario de login.
2. Los usuarios pueden consultar la lista de productos y ver el detalle de cada uno.
3. Los usuarios autenticados pueden agregar productos a un carrito de compras y gestionarlo (agregar/eliminar productos, ver el carrito).
4. Los usuarios pueden realizar pagos a través de Webpay.
5. Solo los usuarios con rol ADMIN pueden crear, editar y eliminar productos.
6. El sistema debe validar los datos de entrada en los formularios (por ejemplo, que el precio sea mayor a cero, que los campos obligatorios no estén vacíos, etc.).

## Requisitos No Funcionales

1. El sistema debe proteger los endpoints sensibles mediante autenticación y autorización usando Spring Security.
2. La aplicación debe ser accesible desde navegadores modernos y tener una interfaz amigable.
3. El sistema debe manejar errores y mostrar mensajes claros al usuario en caso de fallos (por ejemplo, credenciales incorrectas, usuario ya existente, etc.).
4. El sistema debe ser capaz de integrarse con el servicio de pagos Webpay de manera segura.
5. El código debe ser mantenible y seguir buenas prácticas de desarrollo (separación de capas, uso de servicios, controladores, etc.).
6. La información sensible, como contraseñas, debe almacenarse de forma segura (encriptada).
7. El sistema debe ser escalable para soportar múltiples usuarios concurrentes.

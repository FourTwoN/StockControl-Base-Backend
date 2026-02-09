# StockControl-Backend

## Ejecutar backend (Quarkus Dev)

`demeter-app` es el módulo de arranque. Desde ahí podés correr todos los módulos o elegir un subconjunto.

### 1) Correr todos los módulos

```bash
./gradlew :demeter-app:quarkusDev
```

### 2) Elegir qué módulos correr

Usá la propiedad Gradle `demeter.modules` con una lista separada por comas:

```bash
./gradlew :demeter-app:quarkusDev -Pdemeter.modules=productos,inventario,ventas
```

Valores válidos:

- `common`
- `productos`
- `inventario`
- `ventas`
- `costos`
- `usuarios`
- `ubicaciones`
- `empaquetado`
- `precios`
- `analytics`
- `fotos`
- `chatbot`

Notas:

- Si no pasás `demeter.modules` (o usás `all`), se corren todos.
- `common` se agrega automáticamente aunque no lo pongas.

### 3) Verificar qué módulos quedaron activos

```bash
./gradlew :demeter-app:printDemeterModules
./gradlew :demeter-app:printDemeterModules -Pdemeter.modules=productos,inventario
```

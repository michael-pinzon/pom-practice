# SauceDemo POM Practice

## Introducción

Este proyecto implementa la solución del ejercicio de automatización sobre
[SauceDemo](https://www.saucedemo.com/), usando Java 21, Maven, Selenium WebDriver,
Chrome y TestNG.

Los escenarios están diseñados con el patrón Page Object Model (POM). Cada prueba
inicia una sesión independiente, ejecuta acciones de usuario a través de objetos de
página y valida el resultado únicamente desde la clase de prueba.

## Tecnologías

| Tecnología | Versión / configuración |
| --- | --- |
| Java | 21 |
| Maven | 3.9+ |
| Selenium Java | 4.48.0 |
| TestNG | 7.12.0 |
| Navegador | Google Chrome |
| Ejecución | Headless por defecto |

Selenium Manager resuelve automáticamente el controlador compatible con Chrome, por
lo que no es necesario descargar ni configurar un `chromedriver` manualmente.

## Estructura

```text
src/test/java/com/globant/pompractice/
├── config/
│   ├── DriverFactory.java       # Creación y configuración de ChromeDriver
│   └── TestConfig.java          # Propiedades configurables de la ejecución
├── data/
│   └── TestData.java             # Credenciales y datos del checkout
├── pages/
│   ├── BasePage.java             # Page Factory, esperas y acciones comunes
│   ├── LoginPage.java
│   ├── InventoryPage.java
│   ├── CartPage.java
│   ├── CheckoutInformationPage.java
│   ├── CheckoutOverviewPage.java
│   └── CheckoutCompletePage.java
└── tests/
    ├── BaseTest.java              # Ciclo de vida del WebDriver
    ├── PurchaseTest.java
    ├── CartRemovalTest.java
    └── LogoutTest.java
```

## Escenarios automatizados

### Compra de un producto aleatorio

`PurchaseTest` inicia sesión con el usuario estándar, selecciona aleatoriamente un
producto del inventario, abre el carrito, completa nombre, apellido y código postal,
finaliza la compra y verifica el mensaje `Thank you for your order!`.

### Eliminación de productos del carrito

`CartRemovalTest` añade tres productos diferentes, verifica que el carrito contiene
tres elementos, elimina todos mediante `CartPage` y valida que el carrito quede vacío.

### Cierre de sesión

`LogoutTest` inicia sesión, abre el menú lateral, cierra la sesión y comprueba que el
usuario regresa a la página de login.

## Decisiones de diseño

- Los localizadores son privados y se inicializan con `@FindBy` mediante Page Factory.
- Las acciones que cambian de pantalla devuelven el Page Object correspondiente para
  permitir un flujo legible y encadenable.
- `BasePage` concentra el `WebDriverWait` explícito y las operaciones reutilizables.
- Las aserciones están únicamente en las clases de prueba; los Page Objects exponen
  estados y datos observables.
- `BaseTest` usa `@BeforeMethod` y `@AfterMethod` para crear y cerrar un navegador por
  prueba, evitando dependencia entre escenarios.
- La selección de tres productos usa una lista barajada de índices, asegurando que
  sean distintos sin depender de nombres fijos.

## Ejecución

Desde la raíz del proyecto:

```bash
mvn clean test
```

Para ejecutar un escenario específico:

```bash
mvn clean test '-Dtest=PurchaseTest'
mvn clean test '-Dtest=CartRemovalTest'
mvn clean test '-Dtest=LogoutTest'
```

Para ver el navegador durante la ejecución:

```bash
mvn clean test '-Dheadless=false'
```

También se pueden sobrescribir la URL y el navegador con `-DbaseUrl` y `-Dbrowser`.
La implementación actual valida `chrome` como navegador soportado.

## Credenciales de demostración

El ejercicio utiliza las credenciales públicas de SauceDemo:

```text
Usuario: standard_user
Contraseña: secret_sauce
```

Los datos del formulario de checkout están centralizados en `TestData.java` para que
puedan cambiarse sin modificar los Page Objects ni las pruebas.

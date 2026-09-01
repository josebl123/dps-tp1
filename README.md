# dps-tp1 — Currency Converter

Trabajo Práctico #1 de Desarrollo de Software Profesional (ITBA).

Conversor de monedas sobre la API de [freecurrencyapi.com](https://freecurrencyapi.com): permite
listar las monedas soportadas, consultar cotizaciones y convertir un monto a una o varias monedas,
tanto con la cotización actual como con la de una fecha pasada.

## Requisitos

- **JDK 25.** El build lo exige explícitamente (`maven-enforcer-plugin`), así que con una versión
  anterior falla antes de compilar.
- **Maven 3.8+**

```bash
java -version
```

> **Nota para WSL:** si tenés instalado el JDK 21 (`/usr/lib/jvm/java-21-openjdk-amd64`), el build
> falla con `Build requires Java 25`. Instalá el 25 y exportá `JAVA_HOME`:
>
> ```bash
> sudo apt install openjdk-25-jdk && export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
> ```

## Configuración

La API key **no está en el código**: se lee de un archivo `.env` en la raíz del proyecto, que está
en el `.gitignore` y no se versiona.

1. Copiá el archivo de ejemplo:

```bash
cp .env.example .env
```

2. Conseguí una key gratuita en [freecurrencyapi.com](https://app.freecurrencyapi.com/register) y
   completala:

```
FREECURRENCYAPI_KEY=tu-key-aca
```

Si el archivo falta o la variable está vacía, el programa corta al arrancar con un mensaje claro,
sin llegar a hacer ninguna llamada a la API. Como alternativa, la key también se toma de una
variable de entorno con el mismo nombre, útil para CI:

```bash
export FREECURRENCYAPI_KEY="tu-key-aca"
```

### Dónde se resuelve la configuración

El archivo lo lee `Main`, que es el composition root, y le pasa la key al cliente como un `String`
por constructor. `FreeCurrencyApiClient` no sabe que existe un `.env`: recibe un valor y lo usa.
Por eso los tests corren en memoria pura, sin necesidad de que el archivo exista.

## Build y tests

```bash
mvn verify
```

Compila, corre los tests y valida la cobertura. El build **falla si la cobertura baja del 100%** en
líneas y ramas (`jacoco:check`), con `Main` como única clase excluida por ser el composition root.

Solo los tests:

```bash
mvn test
```

El reporte de cobertura queda en `target/site/jacoco/index.html`.

## Ejecución

Desde IntelliJ: correr `Main` directamente (el `.env` se lee del directorio del proyecto, que es el
working directory por defecto).

Desde la línea de comandos, armando el classpath con Maven:

```bash
mvn -q dependency:build-classpath -Dmdep.outputFile=target/cp.txt
java -cp "target/classes:$(cat target/cp.txt)" Main
```

En Windows el separador de classpath es `;` en vez de `:`.

## Estructura

La separación entre negocio y detalles es el eje del diseño: **`service/` y `model/` no importan
nada de `client/`, `http/` ni `parser/`**. Las únicas flechas entre capas van del detalle al
dominio.

```
edu.itba.class1.exchange
├── model/                        Value objects del dominio
│   ├── MoneyAmount               Monto + moneda; redondea según ISO 4217
│   ├── CurrencyRate              Cotización con timestamp; rechaza tasas <= 0
│   └── Conversion                Monto convertido + cotización usada
├── service/                      Reglas de negocio y puertos
│   ├── CurrencyConverter                 Conversiones con cotización actual
│   ├── HistoricalCurrencyConverter       Conversiones con cotización histórica
│   ├── CurrencyRateProvider              Puerto: cotizaciones actuales
│   ├── HistoricalCurrencyRateProvider    Puerto: cotizaciones por fecha
│   ├── CurrencyCatalog                   Puerto: monedas disponibles
│   └── error/RateNotAvailableException   Excepción de dominio
├── client/currencyapi/freecurrencyapi/
│   ├── FreeCurrencyApiClient     Implementa los tres puertos contra la API
│   ├── FreeCurrencyResponseMapper  Anti-corruption layer: DTO → dominio
│   └── response/                 DTOs de la API (no cruzan al dominio)
├── client/error/                 Excepciones de integración, por status HTTP
├── http/                         Cliente HTTP, status y mapeo a excepción
├── parser/                       Abstracción de parseo JSON
└── main/Main                     Composition root
```

El diagrama de dependencias está en [`dependencies.puml`](dependencies.puml).

### Decisiones de diseño

**El dominio define lo que necesita.** `CurrencyRateProvider`, `HistoricalCurrencyRateProvider` y
`CurrencyCatalog` viven en `service/` y las implementa `FreeCurrencyApiClient`, que vive en el
detalle. La dependencia queda invertida: el detalle depende del dominio, no al revés.

**Las interfaces están segregadas por caso de uso** (ISP). `CurrencyConverter` depende solo de
`CurrencyRateProvider` y no arrastra las operaciones históricas que no usa.

**Los DTOs no cruzan la frontera.** `FreeCurrencyResponseMapper` es el único punto donde el modelo
de FreeCurrencyAPI se traduce a `CurrencyRate` y `Currency`. Nadie en `service/` ni en `model/`
conoce `ExchangeRateResponse`.

**El comportamiento vive con los datos** (tell don't ask). `MoneyAmount.convertWithRate(rate)` hace
la conversión; el converter no extrae el monto para multiplicarlo afuera.

## Funcionalidades

| # | Requerimiento | Implementación |
|---|---|---|
| 1 | Listar las monedas soportadas | `CurrencyCatalog.getAvailableCurrencies()` |
| 2 | Timestamp de la cotización | `CurrencyRate.timestamp()` |
| 3 | Solo la cotización, sin convertir | `CurrencyRateProvider.getCurrencyRate()` |
| 4 | Manejo y notificación de errores | `ResponseStatusChecker` + jerarquía de excepciones |
| 5 | Un monto a varias monedas | `CurrencyConverter.convertMultiple()` |
| 6 | Cotización histórica por un monto | `HistoricalCurrencyConverter.convertMultiple()` |
| 7 | Cotización usada en cada conversión | `Conversion.rateUsed()` |

`Main` ejecuta los siete puntos en orden al correr el programa.

Sobre el punto 2: la API no informa fecha en el endpoint `latest`, así que la cotización actual se
sella con el instante en que se obtuvo. Las históricas llevan la fecha solicitada.

## Manejo de errores

Los errores de la API se traducen a excepciones según el status HTTP, en `ResponseStatusChecker`:

| Status | Excepción |
|---|---|
| 401, 403 | `AuthenticationFailedException` |
| 404 | `CurrencyProviderResourceNotFoundException` |
| 422 | `InvalidProviderRequestException` |
| 429 | `CurrencyProviderRateLimitException` |
| otros | `CurrencyProviderException` |

Aparte, `HttpTransportException` cubre las fallas de conexión (timeout, DNS, host inalcanzable),
`JsonParseException` las respuestas ilegibles y `RateNotAvailableException` el caso de que la API
responda OK pero sin la cotización pedida.

`Main` las captura y las notifica sin cortar con un stack trace:

```
No se pudo completar la operacion: Currency provider authentication failed (status 401): {...}
```

# 🚀 Generador de Preguntas Java

API REST que genera preguntas de opción múltiple sobre análisis de código Java SECUENCIAL, utilizando un modelo GPT (simulado o real) y arquitectura hexagonal.

## 📋 Características

- 🎯 **Generación inteligente**: Crea preguntas sobre código Java secuencial evitando temáticas ya utilizadas
- 🗄️ **Base de datos H2**: Almacena temáticas previas para evitar repeticiones
- 🏗️ **Arquitectura hexagonal**: Código limpio y mantenible
- 🤖 **Integración GPT**: Soporte real y simulado para ChatGPT
- ✅ **Validación**: Verifica respuestas del usuario
- 📖 **Documentación**: API REST bien documentada

## 🛠️ Tecnologías

- **Java 17+**
- **Spring Boot 3.2.2**
- **Spring Web** - API REST
- **Spring Data JPA** - Persistencia
- **Base de datos H2** - Almacenamiento en memoria
- **WebClient** - Cliente HTTP para GPT
- **Lombok** - Reducción de boilerplate
- **Jackson** - Serialización JSON
- **JUnit 5 + Mockito** - Testing

## 🏗️ Arquitectura

### Estructura del proyecto

```
src/main/java/org/jcr/generadorpreguntasjava/
├── domain/
│   ├── model/           # Entidades de dominio (Pregunta, Opcion, Tematica)
│   └── service/         # Interfaces de servicios de dominio
├── application/
│   ├── usecase/         # Casos de uso (interfaces)
│   └── service/         # Implementación de servicios
├── port/
│   ├── in/web/         # Controladores y DTOs
│   └── out/            # Interfaces de puertos de salida
├── infrastructure/
│   ├── adapter/output/  # Implementaciones de adaptadores
│   ├── persistence/     # Entidades JPA y repositorios
│   └── config/         # Configuración
└── shared/             # Excepciones y respuestas comunes
```

### Flujo de generación de preguntas

1. **Consulta temáticas previas** de la base H2
2. **Construye prompt dinámico** incluyendo temáticas a evitar
3. **Envía prompt al modelo GPT** (real o simulado)
4. **Parsea respuesta JSON** y crea objeto Pregunta
5. **Guarda nuevas temáticas** en la base de datos
6. **Retorna pregunta** al cliente

## 🚀 Inicio rápido

### Prerrequisitos

- Java 17+
- Maven 3.8+
- (Opcional) Clave API de OpenAI

### Instalación

1. **Clonar el proyecto**
```bash
git clone <tu-repositorio>
cd GeneradorPreguntasJava
```

2. **Compilar**
```bash
mvn clean install
```

3. **Ejecutar**
```bash
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

### Configuración

#### Modo simulado (por defecto)
```properties
openai.enabled=false
```

#### Modo real con OpenAI
```properties
openai.enabled=true
openai.api-key=tu-clave-api-aqui
openai.model=gpt-3.5-turbo
```

O usando variable de entorno:
```bash
export OPENAI_API_KEY=tu-clave-api
```

## 📚 API Endpoints

### 🎯 Generar pregunta

**POST** `/api/v1/preguntas`

```bash
curl -X POST http://localhost:8080/api/v1/preguntas \
  -H "Content-Type: application/json" \
  -d "{}"
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Pregunta generada exitosamente",
  "data": {
    "enunciado": "¿Qué imprime el siguiente código Java?",
    "codigoJava": "public class Ejemplo {\n    public static void main(String[] args) {\n        int[] numeros = {1, 2, 3, 4, 5};\n        int suma = 0;\n        for (int numero : numeros) {\n            suma += numero;\n        }\n        System.out.println(suma);\n    }\n}",
    "opciones": [
      {"letra": "A", "texto": "10", "correcta": false},
      {"letra": "B", "texto": "15", "correcta": true},
      {"letra": "C", "texto": "20", "correcta": false},
      {"letra": "D", "texto": "Error de compilación", "correcta": false}
    ],
    "explicacion": "El código suma todos los elementos del array [1,2,3,4,5]. La suma es 1+2+3+4+5 = 15.",
    "tematicas": ["arrays", "bucles", "for-each"]
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

### ✅ Validar respuesta

**POST** `/api/v1/respuesta`

```bash
curl -X POST http://localhost:8080/api/v1/respuesta \
  -H "Content-Type: application/json" \
  -d '{
    "letraSeleccionada": "B",
    "pregunta": {
      "enunciado": "¿Qué imprime el siguiente código Java?",
      "codigoJava": "...",
      "opciones": [...],
      "explicacion": "...",
      "tematicas": [...]
    }
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "¡Respuesta correcta!",
  "data": {
    "correcta": true,
    "explicacion": "El código suma todos los elementos del array [1,2,3,4,5]. La suma es 1+2+3+4+5 = 15.",
    "letraCorrecta": "B"
  },
  "timestamp": "2024-01-15T10:31:00"
}
```

## 🗄️ Base de datos

### Consola H2

Accede a la consola web de H2 en: `http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Usuario**: `sa`
- **Contraseña**: (vacía)

### Tabla temáticas

```sql
SELECT * FROM tematicas ORDER BY fecha_creacion DESC;
```

## 🧪 Testing

### Ejecutar pruebas

```bash
mvn test
```

### Cobertura de pruebas

Las pruebas incluyen:
- ✅ Generación exitosa de preguntas
- ✅ Validación de respuestas correctas/incorrectas
- ✅ Manejo de errores de ChatGPT
- ✅ Parseo de JSON inválido
- ✅ Guardado de temáticas

## 🤖 Prompt Template

El sistema utiliza un prompt complejo y realista que incluye:

- **Instrucciones específicas** para código Java secuencial
- **Restricciones claras** (sin concurrencia, threads, etc.)
- **Formato JSON estricto** para la respuesta
- **Temáticas a evitar** dinámicamente consultadas de la BD
- **Validaciones de estructura** y contenido

Ejemplo de prompt generado:
```
Eres un experto en Java que genera preguntas de análisis de código secuencial.

**INSTRUCCIONES ESTRICTAS:**
1. Genera SOLO preguntas sobre código Java SECUENCIAL (sin programación concurrente, threads, async, etc.)
2. El código debe usar únicamente estructuras de control básicas: if/else, for, while, do-while, switch
...

**EVITAR ESTAS TEMÁTICAS YA UTILIZADAS:**
["arrays", "bucles", "strings", "condicionales"]

**FORMATO DE RESPUESTA (JSON estricto):**
{
  "enunciado": "¿Qué imprime el siguiente código Java?",
  ...
}
```

## ⚙️ Configuración avanzada

### Variables de entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `OPENAI_API_KEY` | Clave API de OpenAI | `tu-api-key-aqui` |
| `SERVER_PORT` | Puerto del servidor | `8080` |
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring | - |

### Profiles de Spring

#### Desarrollo (simulado)
```properties
spring.profiles.active=dev
openai.enabled=false
```

#### Producción (OpenAI real)
```properties
spring.profiles.active=prod
openai.enabled=true
openai.api-key=${OPENAI_API_KEY}
```

## 🔧 Personalización

### Agregar nuevos tipos de pregunta

1. Modificar `PreguntaPromptTemplate.java`
2. Actualizar el prompt con nuevas instrucciones
3. Ajustar parseo en `GenerarPreguntaServiceImpl.java`

### Cambiar modelo de IA

```properties
openai.model=gpt-4
openai.base-url=https://api.openai.com/v1
```

### Personalizar base de datos

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/preguntas
spring.datasource.username=usuario
spring.datasource.password=contraseña
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

## 🐛 Solución de problemas

### Error de conexión a OpenAI
- ✅ Verificar clave API válida
- ✅ Comprobar conectividad a internet
- ✅ Usar modo simulado: `openai.enabled=false`

### Error de base de datos
- ✅ Verificar configuración H2
- ✅ Comprobar permisos de escritura
- ✅ Revisar logs de Hibernate

### Error de parsing JSON
- ✅ Verificar respuesta del modelo
- ✅ Comprobar formato de prompt
- ✅ Revisar logs de Jackson

## 🤝 Contribución

1. Fork el proyecto
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## 📞 Soporte

¿Tienes preguntas? Crea un [issue](../../issues) en GitHub.

---

⭐ **¡No olvides dar una estrella si te fue útil!** ⭐

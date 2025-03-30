# Diseño de FlyAway - Base de Datos

## Arquitectura General

La aplicación sigue el patrón de arquitectura MVVM (Model-View-ViewModel) junto con Clean Architecture para mantener un código limpio y mantenible. La base de datos es un componente fundamental que implementa la capa de persistencia de datos.

### Capas de la Aplicación

1. **Presentación (UI)**
   - Activities/Fragments
   - Composables
   - ViewModels
   - Estados UI

2. **Dominio**
   - Casos de Uso
   - Modelos de Dominio
   - Interfaces de Repositorio

3. **Datos**
   - Implementaciones de Repositorio
   - Base de Datos Room
   - Modelos de Datos

## Diseño de la Base de Datos

### 1. Estructura General

La base de datos está implementada utilizando Room, que es una biblioteca de persistencia proporcionada por Android Jetpack. Room actúa como una capa de abstracción sobre SQLite, ofreciendo:

- Verificación de tipos en tiempo de compilación
- Soporte para corrutinas y Flow
- Integración con LiveData
- Anotaciones para simplificar el código

### 2. Configuración de la Base de Datos

La base de datos está definida en la clase `FlyAwayDatabase`:

```kotlin
@Database(
    entities = [TripEntity::class, DayEntity::class, ActivityEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlyAwayDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun dayDao(): DayDao
    abstract fun activityDao(): ActivityDao
}
```

### 3. Modelo de Datos

La base de datos está estructurada en tres tablas principales:

#### Tabla: trips
```kotlin
@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val destination: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: LocalDate
)
```

#### Tabla: days
```kotlin
@Entity(tableName = "days")
data class DayEntity(
    @PrimaryKey
    val id: String,
    val tripId: String,
    val date: LocalDate,
    val dayNumber: Int
)
```

#### Tabla: activities
```kotlin
@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val dayId: String,
    val name: String,
    val description: String,
    val startTime: LocalTime,
    val endTime: LocalTime?,
    val location: String
)
```

### 4. Acceso a Datos (DAOs)

Cada entidad tiene su propio DAO que define las operaciones de base de datos:

#### TripDao
```kotlin
@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    fun getAllTrips(): Flow<List<TripEntity>>
    
    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTripById(tripId: String): Flow<TripEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)
    
    @Delete
    suspend fun deleteTrip(trip: TripEntity)
}
```

### 5. Conversión de Tipos

Room no puede almacenar directamente tipos complejos como `LocalDate` y `LocalTime`. Se implementan conversores personalizados:

```kotlin
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun fromString(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun toString(time: LocalTime?): String? {
        return time?.toString()
    }
}
```

### 6. Repositorio

El repositorio actúa como una capa de abstracción entre la base de datos y el resto de la aplicación:

```kotlin
@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val dayDao: DayDao,
    private val activityDao: ActivityDao
) : TripRepository {
    // Implementación de los métodos del repositorio
}
```

### 7. Flujo de Datos

#### Lectura de Datos
1. El ViewModel solicita datos al repositorio
2. El repositorio utiliza los DAOs para obtener los datos
3. Los datos se convierten de entidades a modelos de dominio
4. Los datos se emiten como Flow para actualizar la UI

#### Escritura de Datos
1. El ViewModel envía datos al repositorio
2. El repositorio convierte los modelos de dominio a entidades
3. Los DAOs realizan las operaciones de inserción/actualización/eliminación
4. Los cambios se reflejan automáticamente en los Flows observables

### 8. Relaciones y Restricciones

- Las relaciones entre tablas se manejan mediante claves foráneas
- Se implementa eliminación en cascada para mantener la integridad referencial
- Las operaciones que afectan a múltiples tablas se realizan dentro de transacciones

### 9. Manejo de Errores

- Se implementa manejo de errores en el repositorio
- Se utilizan transacciones para operaciones complejas
- Se valida la integridad de los datos antes de la inserción
- Se registran los errores para facilitar la depuración

### 10. Migración de Base de Datos

Para futuras actualizaciones del esquema:

1. Incrementar el número de versión en la anotación @Database
2. Crear una clase de migración que extienda Migration
3. Implementar la lógica de migración en el método migrate()
4. Registrar la migración en el DatabaseModule

### 11. Validación de Datos

- Los IDs son generados usando UUID para garantizar unicidad
- Las fechas de fin deben ser posteriores a las fechas de inicio
- Los nombres de viajes no pueden estar vacíos
- Las actividades deben tener un nombre y una ubicación
- Las horas de fin son opcionales pero deben ser posteriores a las horas de inicio si se especifican

## Tecnologías Utilizadas

1. **Base de Datos**
   - Room Database
   - SQLite (subyacente)

2. **Inyección de Dependencias**
   - Dagger Hilt

3. **Concurrencia**
   - Coroutines
   - Flow

4. **UI/UX**
   - Jetpack Compose
   - Material Design 3
   - Navigation Component

## Patrones de Diseño

1. Repository Pattern
2. Factory Pattern
3. Observer Pattern (Flow)
4. Dependency Injection
5. Builder Pattern

## Estrategia de Testing

1. **Unit Tests**
   - ViewModels
   - Use Cases
   - Repositories

2. **Integration Tests**
   - Database
   - API

3. **UI Tests**
   - Compose UI Testing
   - End-to-End Tests 
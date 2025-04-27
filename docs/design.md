# Diseño de la Aplicación FlyAway

## Arquitectura

La aplicación sigue una arquitectura MVVM (Model-View-ViewModel) con las siguientes capas:

- **View**: Pantallas de la aplicación implementadas con Jetpack Compose
- **ViewModel**: Lógica de presentación y manejo de estado
- **Repository**: Capa de abstracción para el acceso a datos
- **Data**: Implementación de la persistencia y servicios externos

## Esquema de Base de Datos

### Tabla: users
```sql
CREATE TABLE users (
    id TEXT PRIMARY KEY,
    email TEXT NOT NULL,
    username TEXT NOT NULL UNIQUE,
    birthdate TEXT NOT NULL,
    address TEXT,
    country TEXT,
    phone_number TEXT,
    accept_emails BOOLEAN NOT NULL,
    created_at TEXT NOT NULL
);
```

### Tabla: trips
```sql
CREATE TABLE trips (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    name TEXT NOT NULL,
    destination TEXT NOT NULL,
    start_date TEXT NOT NULL,
    end_date TEXT NOT NULL,
    created_at TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Tabla: days
```sql
CREATE TABLE days (
    id TEXT PRIMARY KEY,
    trip_id TEXT NOT NULL,
    date TEXT NOT NULL,
    day_number INTEGER NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
);
```

### Tabla: activities
```sql
CREATE TABLE activities (
    id TEXT PRIMARY KEY,
    day_id TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    start_time TEXT,
    end_time TEXT,
    location TEXT,
    FOREIGN KEY (day_id) REFERENCES days(id) ON DELETE CASCADE
);
```

### Tabla: app_access_logs
```sql
CREATE TABLE app_access_logs (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    action TEXT NOT NULL,
    timestamp TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

## Uso de la Base de Datos

### Repositorios

1. **UserRepository**
   - `saveUser(user: User)`: Guarda un nuevo usuario
   - `getUserById(id: String)`: Obtiene un usuario por su ID
   - `getUserByEmail(email: String)`: Obtiene un usuario por su email
   - `updateUser(user: User)`: Actualiza los datos de un usuario
   - `deleteUser(id: String)`: Elimina un usuario

2. **TripRepository**
   - `saveTrip(trip: Trip)`: Guarda un nuevo viaje
   - `getTripsByUserId(userId: String)`: Obtiene los viajes de un usuario
   - `getTripById(tripId: String)`: Obtiene un viaje por su ID
   - `updateTrip(trip: Trip)`: Actualiza un viaje
   - `deleteTrip(tripId: String)`: Elimina un viaje

3. **DayRepository**
   - `saveDay(day: Day)`: Guarda un nuevo día
   - `getDaysByTripId(tripId: String)`: Obtiene los días de un viaje
   - `updateDay(day: Day)`: Actualiza un día
   - `deleteDay(dayId: String)`: Elimina un día

4. **ActivityRepository**
   - `saveActivity(activity: Activity)`: Guarda una nueva actividad
   - `getActivitiesByDayId(dayId: String)`: Obtiene las actividades de un día
   - `updateActivity(activity: Activity)`: Actualiza una actividad
   - `deleteActivity(activityId: String)`: Elimina una actividad

5. **AppAccessLogRepository**
   - `logAccess(userId: String, action: String)`: Registra un acceso a la aplicación
   - `getAccessLogsByUserId(userId: String)`: Obtiene los logs de acceso de un usuario

## Autenticación

La aplicación utiliza Firebase Authentication para la autenticación de usuarios:

- **Login**: Autenticación con email y contraseña
- **Registro**: Creación de cuenta con email y contraseña
- **Recuperación de contraseña**: Envío de email para restablecer contraseña
- **Logout**: Cierre de sesión

## Mejoras de UX

1. **Validaciones**
   - Nombres de usuario únicos
   - Fechas válidas para viajes e itinerarios
   - Horarios válidos para actividades
   - Campos obligatorios

2. **Pickers**
   - DatePicker para fechas de viaje
   - TimePicker para horarios de actividades
   - Formato de fecha: dd/MM/yyyy

3. **Feedback al usuario**
   - Mensajes de error claros
   - Indicadores de carga
   - Confirmaciones de acciones importantes

## Versión
Versión actual: 0.4.0 
package com.sebastiangill.recordatorios.model

// ============================================================
//  PRIORIDAD
//  sealed class: solo existen estas 3 prioridades.
//  No se puede crear una prioridad inválida.
// ============================================================
sealed class Prioridad(val etiqueta: String, val nivel: Int) {
    data object Alta : Prioridad("Alta", 3)
    data object Media : Prioridad("Media", 2)
    data object Baja : Prioridad("Baja", 1)
}

// ============================================================
//  ESTADO DEL RECORDATORIO
// ============================================================
sealed class EstadoRecordatorio(val etiqueta: String) {
    data object Pendiente : EstadoRecordatorio("Pendiente")
    data object EnProgreso : EstadoRecordatorio("En progreso")
    data object Completado : EstadoRecordatorio("Completado")
}

// ============================================================
//  CATEGORÍA
//  data class: dos categorías con el mismo nombre son iguales,
//  por eso un Set puede evitar repetidas.
// ============================================================
data class Categoria(
    val nombre: String
) {
    init {
        require(nombre.isNotBlank()) { "La categoría no puede estar vacía" }
    }
}

// ============================================================
//  RECORDATORIO
//  data class inmutable: todas sus propiedades son "val".
//  Para modificar uno se crea una copia con copy().
// ============================================================
data class Recordatorio(
    val id: Int,
    val titulo: String,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: EstadoRecordatorio = EstadoRecordatorio.Pendiente,
    val descripcion: String? = null,   // opcional
    val fechaLimite: String? = null,   // opcional (ej: "20/10/2026")
    val ubicacion: String? = null      // opcional (se usará en próximos módulos)
) {
    // Reglas de validación: si no se cumplen, el objeto no se crea
    init {
        require(id > 0) { "El id debe ser mayor a 0" }
        require(titulo.isNotBlank()) { "El título no puede estar vacío" }
        require(titulo.length <= 60) { "El título no puede superar los 60 caracteres" }
    }
}

// ============================================================
//  ESTADOS DE LA PANTALLA
//  Representa en qué situación se encuentra la app.
// ============================================================
sealed class EstadoPantalla {
    data object Cargando : EstadoPantalla()
    data class Exito(val recordatorios: List<Recordatorio>) : EstadoPantalla()
    data object Vacio : EstadoPantalla()
    data class Error(val mensaje: String) : EstadoPantalla()
}
package com.sebastiangill.recordatorios.data

import androidx.compose.ui.graphics.Color
import com.sebastiangill.recordatorios.model.Categoria
import com.sebastiangill.recordatorios.model.EstadoRecordatorio
import com.sebastiangill.recordatorios.model.Prioridad
import com.sebastiangill.recordatorios.model.Recordatorio

// ============================================================
//  DATOS SIMULADOS
//  object: existe una sola instancia, accesible desde toda la app.
// ============================================================
object DatosSimulados {

    // ---------- CATEGORÍAS ----------
    // Lista con categorías repetidas a propósito
    private val categoriasCargadas: List<Categoria> = listOf(
        Categoria("Trabajo"),
        Categoria("Estudio"),
        Categoria("Salud"),
        Categoria("Hogar"),
        Categoria("Estudio"),   // repetida
        Categoria("Trabajo")    // repetida
    )

    // SET: al convertir la lista en Set se eliminan las repetidas
    // Resultado: Trabajo, Estudio, Salud, Hogar
    val categorias: Set<Categoria> = categoriasCargadas.toSet()

    // MAP: asocia cada categoría con un color
    val coloresPorCategoria: Map<Categoria, Color> = mapOf(
        Categoria("Trabajo") to Color(0xFF1E88E5),  // azul
        Categoria("Estudio") to Color(0xFF8E24AA),  // violeta
        Categoria("Salud") to Color(0xFF43A047),    // verde
        Categoria("Hogar") to Color(0xFFFB8C00)     // naranja
    )

    // ---------- RECORDATORIOS ----------
    // LIST: recordatorios simulados (inmutable)
    val recordatorios: List<Recordatorio> = listOf(
        Recordatorio(
            id = 1,
            titulo = "Entregar Actividad 1 de Programación Mobile",
            categoria = Categoria("Estudio"),
            prioridad = Prioridad.Alta,
            estado = EstadoRecordatorio.EnProgreso,
            fechaLimite = "20/10/2026"
        ),
        Recordatorio(
            id = 2,
            titulo = "Preparar informe semanal",
            categoria = Categoria("Trabajo"),
            prioridad = Prioridad.Alta,
            descripcion = "Resumen de producción de la semana"
        ),
        Recordatorio(
            id = 3,
            titulo = "Turno con el médico clínico",
            categoria = Categoria("Salud"),
            prioridad = Prioridad.Media,
            fechaLimite = "23/10/2026",
            ubicacion = "Centro médico"
        ),
        Recordatorio(
            id = 4,
            titulo = "Pagar la factura de luz",
            categoria = Categoria("Hogar"),
            prioridad = Prioridad.Alta,
            fechaLimite = "18/10/2026"
        ),
        Recordatorio(
            id = 5,
            titulo = "Repasar comandos de Bash",
            categoria = Categoria("Estudio"),
            prioridad = Prioridad.Baja,
            estado = EstadoRecordatorio.Completado
        ),
        Recordatorio(
            id = 6,
            titulo = "Reunión de equipo",
            categoria = Categoria("Trabajo"),
            prioridad = Prioridad.Media,
            estado = EstadoRecordatorio.Completado
        ),
        Recordatorio(
            id = 7,
            titulo = "Comprar verduras",
            categoria = Categoria("Hogar"),
            prioridad = Prioridad.Baja,
            estado = EstadoRecordatorio.EnProgreso
        ),
        Recordatorio(
            id = 8,
            titulo = "Caminar 30 minutos",
            categoria = Categoria("Salud"),
            prioridad = Prioridad.Baja
        )
    )

    // ---------- FILTROS ----------
    fun filtrarPorEstado(estado: EstadoRecordatorio): List<Recordatorio> =
        recordatorios.filter { it.estado == estado }

    fun filtrarPorPrioridad(prioridad: Prioridad): List<Recordatorio> =
        recordatorios.filter { it.prioridad == prioridad }

    // Busca el color en el Map; si no existe, usa gris
    fun colorDe(categoria: Categoria): Color =
        coloresPorCategoria[categoria] ?: Color.Gray

    // MAP: cantidad de recordatorios por estado
    val cantidadPorEstado: Map<String, Int> =
        recordatorios.groupingBy { it.estado.etiqueta }.eachCount()

    // ---------- PRUEBA DE VALIDACIÓN ----------
    // Intenta crear un recordatorio con título vacío: debe fallar
    val pruebaValidacion: String = runCatching {
        Recordatorio(
            id = 99,
            titulo = "   ",
            categoria = Categoria("Trabajo"),
            prioridad = Prioridad.Baja
        )
    }.fold(
        onSuccess = { "Error: se creó un recordatorio inválido" },
        onFailure = { "Validación OK → ${it.message}" }
    )
}
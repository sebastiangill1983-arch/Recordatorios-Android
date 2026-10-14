package com.sebastiangill.recordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sebastiangill.recordatorios.data.DatosSimulados
import com.sebastiangill.recordatorios.model.Categoria
import com.sebastiangill.recordatorios.model.EstadoPantalla
import com.sebastiangill.recordatorios.model.EstadoRecordatorio
import com.sebastiangill.recordatorios.model.Prioridad
import com.sebastiangill.recordatorios.model.Recordatorio
import com.sebastiangill.recordatorios.ui.theme.RecordatoriosTheme

// ============================================================
//  ACTIVITY PRINCIPAL
// ============================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecordatoriosTheme {
                PantallaRecordatorios()
            }
        }
    }
}

// ============================================================
//  PANTALLA PRINCIPAL
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRecordatorios() {
    // Filtro elegido por el usuario (null = mostrar todos)
    var filtro by remember { mutableStateOf<EstadoRecordatorio?>(null) }

    // Aplica el filtro usando la función de DatosSimulados
    val lista = filtro?.let { DatosSimulados.filtrarPorEstado(it) }
        ?: DatosSimulados.recordatorios

    // Estado de la pantalla (sealed class)
    val estadoPantalla: EstadoPantalla =
        if (lista.isEmpty()) EstadoPantalla.Vacio else EstadoPantalla.Exito(lista)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Recordatorios", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            TarjetaResumen()
            Spacer(Modifier.height(12.dp))
            FiltrosEstado(seleccionado = filtro, onSeleccionar = { filtro = it })
            Spacer(Modifier.height(8.dp))

            // Según el estado, se muestra una cosa u otra
            when (estadoPantalla) {
                is EstadoPantalla.Exito -> ListaRecordatorios(estadoPantalla.recordatorios)
                EstadoPantalla.Vacio -> Text(
                    "No hay recordatorios con este filtro",
                    modifier = Modifier.padding(16.dp)
                )
                EstadoPantalla.Cargando -> CircularProgressIndicator()
                is EstadoPantalla.Error -> Text(
                    estadoPantalla.mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// ============================================================
//  TARJETA DE RESUMEN (usa Map y Set)
// ============================================================
@Composable
fun TarjetaResumen() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                "Resumen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))

            // MAP: cantidad de recordatorios por estado
            DatosSimulados.cantidadPorEstado.forEach { (estado, cantidad) ->
                Text("• $estado: $cantidad", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(8.dp))

            // SET: categorías sin repetir
            Text(
                "Categorías (${DatosSimulados.categorias.size}):",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DatosSimulados.categorias.forEach { EtiquetaCategoria(it) }
            }
            Spacer(Modifier.height(8.dp))

            // Resultado de la prueba de validación
            Text(
                DatosSimulados.pruebaValidacion,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// ============================================================
//  FILTROS POR ESTADO
// ============================================================
@Composable
fun FiltrosEstado(
    seleccionado: EstadoRecordatorio?,
    onSeleccionar: (EstadoRecordatorio?) -> Unit
) {
    val opciones: List<EstadoRecordatorio?> = listOf(
        null,
        EstadoRecordatorio.Pendiente,
        EstadoRecordatorio.EnProgreso,
        EstadoRecordatorio.Completado
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(opciones) { opcion ->
            FilterChip(
                selected = seleccionado == opcion,
                onClick = { onSeleccionar(opcion) },
                label = { Text(opcion?.etiqueta ?: "Todos") }
            )
        }
    }
}

// ============================================================
//  LISTA DE RECORDATORIOS
// ============================================================
@Composable
fun ListaRecordatorios(recordatorios: List<Recordatorio>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(recordatorios, key = { it.id }) { recordatorio ->
            TarjetaRecordatorio(recordatorio)
        }
    }
}

// ============================================================
//  TARJETA DE UN RECORDATORIO
// ============================================================
@Composable
fun TarjetaRecordatorio(recordatorio: Recordatorio) {
    val colorCategoria = DatosSimulados.colorDe(recordatorio.categoria)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            // Barra lateral con el color de la categoría (sale del Map)
            Box(
                Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(colorCategoria)
            )
            Column(Modifier.padding(12.dp)) {
                // Título
                Text(
                    recordatorio.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                // Descripción (opcional: solo se muestra si existe)
                recordatorio.descripcion?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(8.dp))

                // Categoría y prioridad
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EtiquetaCategoria(recordatorio.categoria)
                    EtiquetaPrioridad(recordatorio.prioridad)
                }
                Spacer(Modifier.height(6.dp))

                // Estado
                TextoEstado(recordatorio.estado)

                // Datos opcionales
                recordatorio.fechaLimite?.let {
                    Text("📅 Vence: $it", style = MaterialTheme.typography.bodySmall)
                }
                recordatorio.ubicacion?.let {
                    Text("📍 $it", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// ============================================================
//  ETIQUETAS (chips de color)
// ============================================================
@Composable
fun Etiqueta(texto: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
fun EtiquetaCategoria(categoria: Categoria) {
    Etiqueta(categoria.nombre, DatosSimulados.colorDe(categoria))
}

@Composable
fun EtiquetaPrioridad(prioridad: Prioridad) {
    // when sobre una sealed class: Kotlin obliga a cubrir todos los casos
    val color = when (prioridad) {
        Prioridad.Alta -> Color(0xFFE53935)   // rojo
        Prioridad.Media -> Color(0xFFF9A825)  // ámbar
        Prioridad.Baja -> Color(0xFF757575)   // gris
    }
    Etiqueta("Prioridad ${prioridad.etiqueta}", color)
}

@Composable
fun TextoEstado(estado: EstadoRecordatorio) {
    val (icono, color) = when (estado) {
        EstadoRecordatorio.Pendiente -> "⏳" to MaterialTheme.colorScheme.onSurfaceVariant
        EstadoRecordatorio.EnProgreso -> "🔄" to Color(0xFF1E88E5)
        EstadoRecordatorio.Completado -> "✅" to Color(0xFF43A047)
    }
    Text(
        "$icono ${estado.etiqueta}",
        style = MaterialTheme.typography.labelLarge,
        color = color
    )
}

// ============================================================
//  VISTA PREVIA (para ver el diseño sin ejecutar)
// ============================================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VistaPrevia() {
    RecordatoriosTheme {
        PantallaRecordatorios()
    }
}
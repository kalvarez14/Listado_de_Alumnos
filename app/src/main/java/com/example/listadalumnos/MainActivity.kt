package com.example.listadalumnos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadalumnos.databinding.ActivityMainBinding
import com.example.listadalumnos.databinding.DialogAlumnoBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelperAlumno
    private lateinit var alumnoAdapter: AlumnoAdapter
    private var listaAlumnos = ArrayList<Alumno>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de insets para diseño Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainContainer) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.mainContainer.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Inicializar Helper de Base de Datos SQLite
        dbHelper = DBHelperAlumno(this)

        // Configurar RecyclerView y Adaptador
        configurarRecyclerView()

        // Cargar datos iniciales desde la base de datos
        cargarDatosDesdeBD()

        // Listener para agregar un nuevo alumno a la BD
        binding.fabAgregar.setOnClickListener {
            mostrarDialogoAgregarAlumno()
        }
    }

    /**
     * Configura el RecyclerView con un LinearLayoutManager y el AlumnoAdapter
     */
    private fun configurarRecyclerView() {
        alumnoAdapter = AlumnoAdapter(listaAlumnos) { alumnoAEditar ->
            mostrarDialogoActualizarAlumno(alumnoAEditar)
        }

        binding.rvAlumnos.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = alumnoAdapter
        }
    }

    /**
     * Obtiene los alumnos almacenados en la tabla 'Alumnos' de SQLite y actualiza la vista
     */
    private fun cargarDatosDesdeBD() {
        listaAlumnos = dbHelper.getAllAlumnos()
        alumnoAdapter.actualizarLista(listaAlumnos)

        // Actualizar vistas informativas según la cantidad de elementos
        if (listaAlumnos.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvAlumnos.visibility = View.GONE
            binding.tvTotalAlumnos.text = getString(R.string.no_alumnos)
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvAlumnos.visibility = View.VISIBLE
            binding.tvTotalAlumnos.text = getString(R.string.total_alumnos_formato, listaAlumnos.size)
        }
    }

    /**
     * Muestra el diálogo modal con el formulario prellenado para ACTUALIZAR un alumno en SQLite.
     * Evita la eliminación directa preservando la integridad de la información.
     */
    private fun mostrarDialogoActualizarAlumno(alumno: Alumno) {
        val dialogBinding = DialogAlumnoBinding.inflate(LayoutInflater.from(this))

        // Prellenar campos con los datos actuales del alumno seleccionado
        dialogBinding.tvTituloDialogo.text = getString(R.string.editar_alumno)
        dialogBinding.etNombre.setText(alumno.nombre)
        dialogBinding.etCuenta.setText(alumno.cuenta)
        dialogBinding.etCorreo.setText(alumno.correo)
        dialogBinding.etImagen.setText(alumno.imagen)

        MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.guardar) { _, _ ->
                val nuevoNombre = dialogBinding.etNombre.text.toString().trim()
                val nuevaCuenta = dialogBinding.etCuenta.text.toString().trim()
                val nuevoCorreo = dialogBinding.etCorreo.text.toString().trim()
                val nuevaImagen = dialogBinding.etImagen.text.toString().trim()

                if (nuevoNombre.isEmpty() || nuevaCuenta.isEmpty() || nuevoCorreo.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Por favor complete los campos obligatorios",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                // Crear objeto actualizado preservando el mismo ID de la base de datos
                val alumnoActualizado = Alumno(
                    id = alumno.id,
                    nombre = nuevoNombre,
                    cuenta = nuevaCuenta,
                    correo = nuevoCorreo,
                    imagen = nuevaImagen
                )

                // Ejecutar actualización en la base de datos SQLite
                val renglonesAfectados = dbHelper.updateAlumno(alumnoActualizado)

                if (renglonesAfectados > 0) {
                    Toast.makeText(this, "¡Alumno actualizado con éxito!", Toast.LENGTH_SHORT).show()
                    cargarDatosDesdeBD() // Recargar lista para refrescar la interfaz
                } else {
                    Toast.makeText(this, "Error al actualizar en la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }

    /**
     * Muestra el diálogo modal para agregar un nuevo alumno en la base de datos SQLite
     */
    private fun mostrarDialogoAgregarAlumno() {
        val dialogBinding = DialogAlumnoBinding.inflate(LayoutInflater.from(this))
        dialogBinding.tvTituloDialogo.text = getString(R.string.agregar_alumno)

        MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.guardar) { _, _ ->
                val nombre = dialogBinding.etNombre.text.toString().trim()
                val cuenta = dialogBinding.etCuenta.text.toString().trim()
                val correo = dialogBinding.etCorreo.text.toString().trim()
                val imagen = dialogBinding.etImagen.text.toString().trim()

                if (nombre.isEmpty() || cuenta.isEmpty() || correo.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Por favor complete todos los campos obligatorios",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val nuevoAlumno = Alumno(
                    nombre = nombre,
                    cuenta = cuenta,
                    correo = correo,
                    imagen = if (imagen.isNotEmpty()) imagen else "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300"
                )

                val idGenerado = dbHelper.insertAlumno(nuevoAlumno)

                if (idGenerado > -1) {
                    Toast.makeText(this, "¡Alumno registrado con éxito!", Toast.LENGTH_SHORT).show()
                    cargarDatosDesdeBD()
                } else {
                    Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }
}
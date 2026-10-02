package com.example.listadalumnos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.listadalumnos.databinding.ItemAlumnoBinding

class AlumnoAdapter(
    private var listaAlumnos: ArrayList<Alumno>,
    private val onEditClick: (Alumno) -> Unit
) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    class AlumnoViewHolder(val binding: ItemAlumnoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val binding = ItemAlumnoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlumnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = listaAlumnos[position]

        with(holder.binding) {
            tvNombre.text = alumno.nombre
            tvCuenta.text = root.context.getString(R.string.cuenta_formato, alumno.cuenta)
            tvCorreo.text = alumno.correo

            // Carga de imagen utilizando Glide con imagen de apoyo en caso de error
            Glide.with(root.context)
                .load(alumno.imagen)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .centerCrop()
                .into(imgAlumno)

            // Listener para actualizar la información del alumno al pulsar el botón o la tarjeta
            btnEditar.setOnClickListener {
                onEditClick(alumno)
            }

            root.setOnClickListener {
                onEditClick(alumno)
            }
        }
    }

    override fun getItemCount(): Int = listaAlumnos.size

    /**
     * Actualiza la lista de alumnos en el adaptador y notifica los cambios
     */
    fun actualizarLista(nuevaLista: ArrayList<Alumno>) {
        listaAlumnos = nuevaLista
        notifyDataSetChanged()
    }
}
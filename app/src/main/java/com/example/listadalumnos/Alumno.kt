package com.example.listadalumnos

/**
 * Clase modelo Alumno con sus atributos principales:
 * @param id Identificador único en la base de datos SQLite
 * @param nombre Nombre completo del estudiante
 * @param cuenta Número de cuenta / matrícula
 * @param imagen URL o ruta de la imagen del estudiante
 * @param correo Correo electrónico institucional / personal
 */
data class Alumno(
    var id: Int = 0,
    var nombre: String = "",
    var cuenta: String = "",
    var imagen: String = "",
    var correo: String = ""
)
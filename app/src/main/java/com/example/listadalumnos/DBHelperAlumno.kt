package com.example.listadalumnos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelperAlumno(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Alumnos.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ALUMNOS = "Alumnos"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CUENTA = "cuenta"
        const val COLUMN_IMAGEN = "imagen"
        const val COLUMN_CORREO = "correo"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE " + TABLE_ALUMNOS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOMBRE + " TEXT, "
                + COLUMN_CUENTA + " TEXT, "
                + COLUMN_IMAGEN + " TEXT, "
                + COLUMN_CORREO + " TEXT" + ")")
        db?.execSQL(createTableQuery)

        // Insertar datos de prueba iniciales para que la aplicación funcione de inmediato
        insertarDatosSemilla(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ALUMNOS")
        onCreate(db)
    }

    private fun insertarDatosSemilla(db: SQLiteDatabase?) {
        val alumnosIniciales = listOf(
            Alumno(
                nombre = "Carlos Eduardo Mendoza",
                cuenta = "319284756",
                imagen = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                correo = "carlos.mendoza@alumnos.unam.mx"
            ),
            Alumno(
                nombre = "Mariana Hernández Gómez",
                cuenta = "318492011",
                imagen = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&auto=format&fit=crop&q=80",
                correo = "mariana.hg@alumnos.unam.mx"
            ),
            Alumno(
                nombre = "Alejandro Ruiz Torres",
                cuenta = "320193847",
                imagen = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80",
                correo = "a.ruiz.torres@alumnos.unam.mx"
            ),
            Alumno(
                nombre = "Sofia Isabel Ramírez",
                cuenta = "317583920",
                imagen = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=300&auto=format&fit=crop&q=80",
                correo = "sofia.ramirez@alumnos.unam.mx"
            ),
            Alumno(
                nombre = "Fernando Castro Morales",
                cuenta = "321049281",
                imagen = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop&q=80",
                correo = "f.castro@alumnos.unam.mx"
            )
        )

        for (alumno in alumnosIniciales) {
            val values = ContentValues().apply {
                put(COLUMN_NOMBRE, alumno.nombre)
                put(COLUMN_CUENTA, alumno.cuenta)
                put(COLUMN_IMAGEN, alumno.imagen)
                put(COLUMN_CORREO, alumno.correo)
            }
            db?.insert(TABLE_ALUMNOS, null, values)
        }
    }

    /**
     * Obtiene la lista completa de alumnos desde la base de datos
     */
    fun getAllAlumnos(): ArrayList<Alumno> {
        val list = ArrayList<Alumno>()
        val selectQuery = "SELECT * FROM $TABLE_ALUMNOS ORDER BY $COLUMN_ID DESC"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            return list
        }

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val nombreIndex = cursor.getColumnIndex(COLUMN_NOMBRE)
            val cuentaIndex = cursor.getColumnIndex(COLUMN_CUENTA)
            val imagenIndex = cursor.getColumnIndex(COLUMN_IMAGEN)
            val correoIndex = cursor.getColumnIndex(COLUMN_CORREO)

            do {
                val id = if (idIndex != -1) cursor.getInt(idIndex) else 0
                val nombre = if (nombreIndex != -1) cursor.getString(nombreIndex) ?: "" else ""
                val cuenta = if (cuentaIndex != -1) cursor.getString(cuentaIndex) ?: "" else ""
                val imagen = if (imagenIndex != -1) cursor.getString(imagenIndex) ?: "" else ""
                val correo = if (correoIndex != -1) cursor.getString(correoIndex) ?: "" else ""

                val alumno = Alumno(id, nombre, cuenta, imagen, correo)
                list.add(alumno)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }

    /**
     * Inserta un nuevo alumno en la base de datos
     */
    fun insertAlumno(alumno: Alumno): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_IMAGEN, alumno.imagen)
            put(COLUMN_CORREO, alumno.correo)
        }
        val result = db.insert(TABLE_ALUMNOS, null, values)
        db.close()
        return result
    }

    /**
     * Actualiza la información de un alumno existente en la base de datos
     */
    fun updateAlumno(alumno: Alumno): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_IMAGEN, alumno.imagen)
            put(COLUMN_CORREO, alumno.correo)
        }

        val rowsAffected = db.update(
            TABLE_ALUMNOS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(alumno.id.toString())
        )
        db.close()
        return rowsAffected
    }
}
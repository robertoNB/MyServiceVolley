package com.example.myserviciovolly

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.security.AccessControlContext

class adminbd (context:Context):SQLiteOpenHelper(context,DATABASE,null,1)
{
    companion object{
        val DATABASE="Escuela"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "Create Table alumno(" +
                    "nocontrol text primary key, " +
                    "nombre text, " +
                    "carrera text, " +
                    "telefono text)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun Ejecuta (sentencia: String) : Int
    {
        try {
            val db=this.writableDatabase
            db.execSQL(sentencia)
            db.close()
            return 1
        }
        catch (ex:Exception)
        {
            Log.d("roberto",ex.toString())
            return 0
        }
    }

    fun consulta(select: String):Cursor?
    {
        try
        {
            val  db=this.readableDatabase
            return db.rawQuery(select,null)
        }
        catch (ex:Exception)
        {
            return null
        }
    }
}
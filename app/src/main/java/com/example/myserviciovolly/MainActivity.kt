package com.example.myserviciovolly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.myserviciovolly.ApiVolley.VolleySingleton
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    var WsInsertarAlumnos:String="http://192.168.0.10/servicios/InsertarAlumno.php"
    var wsConsultaById:String="http://192.168.0.10/servicios/MostrarAlumno.php"
    var wsMostrar:String="http://192.168.0.10/servicios/MostrarAlumnos.php"
    var wsEliminar:String="http://192.168.0.10/servicios/BorrarAlumno.php"
    var wsActualizar:String="http://192.168.0.10/servicios/ActualizarAlumno.php"
    var wsRespalda:String="http://192.168.0.10/servicios/RespaldaAlumnos.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    //----------------------------------------------------------------------------------------------------------------------
    //hacemos el evento
    fun respaldaAlumnos (v:View){
        //declarams una instancia
        var admin=adminbd(this)
        var alumnoJson:JSONObject
        var jsonArray:JSONArray = JSONArray()
        var jsonParam = JSONObject()//JSON Final
        jsonParam.put("usr","RANB")
        jsonParam.put("pwd","hola")
        val cur = admin.consulta("select nocontrol,carrera,nombre,telefono from alumno")
        //recorrer el cursor
        if(cur!!.moveToFirst()){
        do{
            //genera instancia
            alumnoJson =JSONObject()

            //logica para meter al json
            alumnoJson.put("nocontrol", cur!!.getString(0))
            alumnoJson.put("carrera", cur!!.getString(1))
            alumnoJson.put("nombre", cur!!.getString(2))
            alumnoJson.put("telefono", cur!!.getString(3))
            jsonArray.put(alumnoJson)

        }
        while(cur!!.moveToNext())
        cur.close()
        }//fin del if
        jsonParam.put("alumno",jsonArray)
        sendRequest(wsRespalda,jsonParam)

    }

    //--------------------------------------------------------------------------------------------------------------------------
    fun ConsultaXid(v:View){
        if(etNoControl.text.isEmpty()){
            Toast.makeText(this, "Falta el numero de control", Toast.LENGTH_SHORT).show()
            etNoControl.requestFocus()
        }else{
            var jsonEntrada = JSONObject()
            val no = etNoControl.text.toString()
            jsonEntrada.put("nocontrol",no)
            getAlumno(jsonEntrada)
        }
    }

    fun Consulta(v:View){
            getAlumnos()
    }

    fun insertarAlumno(v:View){
        if(etNoControl.text.isEmpty()||etTelefono.text.isEmpty()||etNombre.text.isEmpty()||etCarrera.text.isEmpty())
        {
            Toast.makeText(this, "faltan de llenar datos", Toast.LENGTH_SHORT).show();
            etNoControl.requestFocus()
        }else {
            val no=etNoControl.text.toString()
            val nom=etNombre.text.toString()
            val car=etCarrera.text.toString()
            val tel=etTelefono.text.toString()

            //prepara el json de entrada a nuestro web service
            var jsonEntrada = JSONObject()
            jsonEntrada.put("nocontrol",no)
            jsonEntrada.put("nombre",nom)
            jsonEntrada.put("carrera",car)
            jsonEntrada.put("telefono",tel)
            sendRequest(WsInsertarAlumnos,jsonEntrada)
            limpiarCajas()
        }
    }
    fun ActualizaAlumno(v:View){
        if (etNoControl.text.isEmpty()|| etCarrera.text.isEmpty()||etNombre.text.isEmpty()||etTelefono.text.isEmpty()){
            Toast.makeText(this, "Falta ingresar todos los datos", Toast.LENGTH_SHORT).show();
            etNoControl.requestFocus()
        }else
        {
            val nom=etNombre.text.toString()
            val car=etCarrera.text.toString()
            val tel=etTelefono.text.toString()
            val no=etNoControl.text.toString()

            var jsonEntrada = JSONObject()
            jsonEntrada.put("nocontrol",no)
            jsonEntrada.put("nombre",nom)
            jsonEntrada.put("carrera",car)
            jsonEntrada.put("telefono",tel)
            sendRequest(wsActualizar,jsonEntrada)
            limpiarCajas()
        }
    }
    fun BorrarAlumno(v:View){
        val no=etNoControl.text.toString()

        var jsonEntrada = JSONObject()
        jsonEntrada.put("nocontrol",no)
        sendRequest(wsEliminar,jsonEntrada)
        limpiarCajas()

    }

    //enviamo el url prepara el envia de la informacion y recive los parametros
    fun sendRequest(WsURL:String,jsonEntrada: JSONObject){
        //entrada l web service JsonEntrada
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, WsURL, jsonEntrada,
            Response.Listener { response ->
                val succ = response["success"]
                val msg= response["message"]
                Toast.makeText(this, "Success:${succ} message: ${msg}", Toast.LENGTH_SHORT).show();
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "${error.message}", Toast.LENGTH_LONG).show();
                Log.d("ERROR","${error.message}")
                Toast.makeText(this, "API: Error de capa 8 en el web service ):", Toast.LENGTH_LONG).show();
            }
        )
        //instamcia de volleySin y manda el JsonRequest
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun getAlumnos(){

        val WsURL = wsMostrar
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, WsURL, null,
            Response.Listener {response->
                val succ = response["success"]
                val msg= response["message"]
                val alumnoJSON  = response.getJSONArray("alumno")

                for (i in 0 until alumnoJSON.length()) {
                    val no = alumnoJSON.getJSONObject(i).getString("nocontrol")
                    val nom = alumnoJSON.getJSONObject(i).getString("nombre")
                    val car = alumnoJSON.getJSONObject(i).getString("carrera")
                    val tel = alumnoJSON.getJSONObject(i).getString("telefono")

                    val admin = adminbd(this)
                    val sentencia = "insert into alumno (nocontrol,carrera,nombre,telefono)values" +
                            "('${no}','${car}','${nom}','${tel}')"
                    admin.Ejecuta(sentencia)
                }
                Toast.makeText(this, "Alumnos Guardados SQLITE", Toast.LENGTH_SHORT).show();
            },
            Response.ErrorListener { error->
                Toast.makeText(this, "${error.message}", Toast.LENGTH_LONG).show();
                Log.d("ERROR","${error.message}")
                Toast.makeText(this, "API: Error de capa 8 en el web service ):", Toast.LENGTH_LONG).show();
            }
                )
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun getAlumno(jsonEntrada: JSONObject){
        val wsURL = wsConsultaById
        val jsonOR= JsonObjectRequest(
            Request.Method.POST,wsURL,jsonEntrada,
            Response.Listener {response->
                val succ=response["success"]
                val msg=response["message"]
                val alumnoJson = response.getJSONArray("alumno")
                if(alumnoJson.length()>=1){
                    val no = alumnoJson.getJSONObject(0).getString("nocontrol")
                    val carr = alumnoJson.getJSONObject(0).getString("carrera")
                    val nom = alumnoJson.getJSONObject(0).getString("nombre")
                    val tel = alumnoJson.getJSONObject(0).getString("telefono")
                    etNoControl.setText(no)
                    etCarrera.setText(carr)
                    etNombre.setText(nom)
                    etTelefono.setText(tel)
                    etNoControl.requestFocus()
                }
            },
            Response.ErrorListener { error->
                Toast.makeText(this, "${error.message }", Toast.LENGTH_SHORT).show()
                Log.d("ERROR","${error.message}")
                Toast.makeText(this, "API: Error de capa 8 en WS: ", Toast.LENGTH_SHORT).show()
            })
        VolleySingleton.getInstance(this).addToRequestQueue(jsonOR)
    }

    fun limpiarCajas(){
        etNoControl.setText("")
        etNombre.setText("")
        etCarrera.setText("")
        etTelefono.setText("")
    }
}

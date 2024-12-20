package com.example.projet_maison
//page de la maison choisie
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeActivity : AppCompatActivity() {
    private var isShuttersAvailable: Boolean = true
    private lateinit var token: String;
    private lateinit var houseId: String;
    private var lights = ArrayList<Light>()
    private var shutters = ArrayList<Shutter>()
    private var lightsId=ArrayList<String>()
    private var shuttersId=ArrayList<String>()
    private lateinit var shutterAdapter: ShutterAdapter
    private lateinit var lightAdapter: LightAdapter
    private lateinit var listViewShutters: ListView
    private lateinit var listViewLights: ListView
    private var selectedDeviceType: String = ""
    private var selectedDeviceId: String = ""
    private var isGroupCommand=false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        shutterAdapter = ShutterAdapter(this, shutters)
        lightAdapter = LightAdapter(this, lights)
        token = intent.getStringExtra("token").toString()
        houseId = intent.getStringExtra("houseId").toString()
        listViewShutters = findViewById(R.id.listViewShutters)
        listViewLights = findViewById(R.id.listViewLights)
        findViewById<Button>(R.id.btnToggle).text="Lumières"
        displayHouseId()
        initDevicesListView()
        initDevices()
        switch()
    }

    private fun displayHouseId() {
        findViewById<TextView>(R.id.txtHouseId).text =
            "Bienvenue sur la page de contrôle de la maison $houseId"
    }

    private fun initDevices() {
        Api().get<JsonObject>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices",
            ::initDevicesSuccess,
            token
        )
    }

    private fun initDevicesSuccess(responseCode: Int, devicesListRaw: JsonObject?) {
        val gson = Gson()
        lights.clear()
        shutters.clear()
        if (responseCode == 200 && devicesListRaw != null) {
            val deviceList = devicesListRaw["devices"] as JsonArray
            for (device in deviceList) {
                val deviceObject = device as JsonObject
                val availableCommandsArray = deviceObject["availableCommands"] as JsonArray
                if (availableCommandsArray.size() == 2) {
                    val light = gson.fromJson(device, Light::class.java)
                    lights.add(light)
                    lightsId.add(light.id)
                }
                if (availableCommandsArray.size() == 3) {
                    val shutter = gson.fromJson(device, Shutter::class.java)
                    shutters.add(shutter)
                    shuttersId.add(shutter.id)
                }
            }
            println(lights)
            updateDevices()
        }
        else{
            println(responseCode)
            runOnUiThread {
                Toast.makeText(this,"Erreur lors du chargement des données", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun interactWithAll(view:View){
        when (view.id) {
            R.id.btnTurnOnAll -> {
               groupCommand(lightsId,"TURN ON")
            }
            R.id.btnTurnOffAll -> {
                groupCommand(lightsId,"TURN OFF")
            }
            R.id.btnOpenAll -> {
                groupCommand(shuttersId,"OPEN")
            }
            R.id.btnCloseAll -> {
                groupCommand(shuttersId,"CLOSE")
            }
        }
    }
    private fun groupCommand(array:ArrayList<String>,command:String){
        isGroupCommand=true
        var tempType: String
        var tempID: String
        CoroutineScope(Dispatchers.IO).launch{
            try{
                for(id in array){
                    tempType= id.split(" ")[0]
                    tempID=id.split(" ")[1]
                    sendCommand(Command(command),tempType,tempID)
                    delay(200)
                }
                withContext(Dispatchers.Main){
                    initDevices()
                }
            }catch(e:Exception){
                println("Erreur lors de l'execution :${e.message}")
            }finally {
                isGroupCommand=false
            }
        }



    }

    private fun initDevicesListView() {
        listViewShutters.adapter = shutterAdapter
        listViewLights.adapter = lightAdapter
        listViewLights.setOnItemClickListener { _, clickedView, _, _ ->
            val tempSelectedDevice =
                clickedView.findViewById<TextView>(R.id.column1).text.toString()
            val delimiter1 = " "
            selectedDeviceId = tempSelectedDevice.split(delimiter1)[1]
            selectedDeviceType = tempSelectedDevice.split(delimiter1)[0]
            findViewById<TextView>(R.id.txtViewSelectedDeviceName).text=selectedDeviceType+" "+selectedDeviceId


        }
        listViewShutters.setOnItemClickListener { _, clickedView, _, _ ->
            val tempSelectedDevice =
                clickedView.findViewById<TextView>(R.id.column1).text.toString()
            val delimiter1 = " "
            selectedDeviceId = tempSelectedDevice.split(delimiter1)[1]
            selectedDeviceType = tempSelectedDevice.split(delimiter1)[0]
            findViewById<TextView>(R.id.txtViewSelectedDeviceName).text=selectedDeviceType+" "+selectedDeviceId
        }

    }

    private fun updateDevices() {
        runOnUiThread {
            shutterAdapter.notifyDataSetChanged()
            lightAdapter.notifyDataSetChanged()
        }
    }
    fun toggle(view:View){
        switch()
        findViewById<Button>(R.id.btnToggle).text=if(isShuttersAvailable)"Volets" else "Lumières"
    }

    private fun switch() {
        val btnTurnOn = findViewById<Button>(R.id.btnTurnOn)
        val btnTurnOff = findViewById<Button>(R.id.btnTurnOff)
        val btnTurnOnAll =findViewById<Button>(R.id.btnTurnOnAll)
        val btnTurnOffAll =findViewById<Button>(R.id.btnTurnOffAll)
        val btnOpen = findViewById<Button>(R.id.btnOpen)
        val btnClose = findViewById<Button>(R.id.btnClose)
        val btnOpenAll =findViewById<Button>(R.id.btnOpenAll)
        val btnCloseAll =findViewById<Button>(R.id.btnCloseAll)
        val btnStop = findViewById<Button>(R.id.btnStop)
        if (isShuttersAvailable) {
            listViewShutters.visibility = View.VISIBLE
            listViewLights.visibility = View.GONE
            btnTurnOn.visibility = View.GONE
            btnTurnOff.visibility = View.GONE
            btnTurnOnAll.visibility=View.GONE
            btnTurnOffAll.visibility=View.GONE
            btnOpen.visibility = View.VISIBLE
            btnClose.visibility = View.VISIBLE
            btnOpenAll.visibility = View.VISIBLE
            btnCloseAll.visibility = View.VISIBLE
            btnStop.visibility = View.VISIBLE
        } else {
            listViewShutters.visibility = View.GONE
            listViewLights.visibility = View.VISIBLE
            btnTurnOn.visibility = View.VISIBLE
            btnTurnOff.visibility = View.VISIBLE
            btnTurnOnAll.visibility = View.VISIBLE
            btnTurnOffAll.visibility = View.VISIBLE
            btnOpen.visibility = View.GONE
            btnClose.visibility = View.GONE
            btnOpenAll.visibility = View.GONE
            btnCloseAll.visibility = View.GONE
            btnStop.visibility = View.GONE
        }
        isShuttersAvailable = !isShuttersAvailable
        selectedDeviceId = ""
        selectedDeviceType = ""
    }

    private fun sendCommand(command: Command,deviceType:String,deviceId:String) {
        Api().post<Command>(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceType%20$deviceId/command",command,::sendCommandSuccess ,token
        )
    }

    private fun sendCommandSuccess(responseCode: Int) {
        if (responseCode == 200) {
            println("Commande bien envoyée")
            if(!isGroupCommand){
                initDevices()
            }
        }
        errorManage(responseCode)
    }
        fun turnOn(view: View) {
            sendCommand(Command("TURN ON"),selectedDeviceType,selectedDeviceId)
        }

        fun turnOff(view: View) {
            sendCommand(Command("TURN OFF"),selectedDeviceType,selectedDeviceId)
        }

        fun open(view: View) {
            sendCommand(Command("OPEN"),selectedDeviceType,selectedDeviceId)
        }

        fun close(view: View) {
            sendCommand(Command("CLOSE"),selectedDeviceType,selectedDeviceId)
        }

        fun stop(view: View) {
            sendCommand(Command("STOP"),selectedDeviceType,selectedDeviceId)
        }

    fun manageUsers(view:View){
        val intent= Intent(this,ManageUsersActivity::class.java)
        intent.putExtra("token",token)
        intent.putExtra("houseId",houseId)
        startActivity(intent)
    }

    private fun errorManage(responseCode:Int){
        if (responseCode == 400) {
            //println("Données fournies incorrectes")
            runOnUiThread {
                Toast.makeText(this,"Données transmises incorrectes", Toast.LENGTH_LONG).show()
            }
        }
        if (responseCode == 403) {
            //println("Accès interdit")
            runOnUiThread {
                Toast.makeText(this,"Accès refusé", Toast.LENGTH_LONG).show()
            }
        }
        if (responseCode == 500) {
            //println("Erreur Serveur")
            runOnUiThread {
                Toast.makeText(this,"Erreur Serveur", Toast.LENGTH_LONG).show()
            }
        }
    }
}
package com.example.projet_maison
//page pour choisir quelle maison gerer
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api
import com.google.gson.Gson
import com.google.gson.JsonArray

class HomeChoiceActivity : AppCompatActivity() {
    private lateinit var token:String;
    private val housesList = ArrayList<Ownership>()
    private lateinit var ownershipAdapter: HouseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_choice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ownershipAdapter=HouseAdapter(this,housesList)

        token=intent.getStringExtra("token").toString()
        displayChooseHouse()
        initHousesListView()
        initOwnedHouses()


    }

    private fun displayChooseHouse(){
        findViewById<TextView>(R.id.txtViewHomeChoice).text="Veuillez sélectionner une propriété"
    }

    private fun initOwnedHouses(){

        Api().get<JsonArray>("https://polyhome.lesmoulinsdudev.com/api/houses",::initOwnedHousesSuccess,token)
    }

    private fun initOwnedHousesSuccess(responseCode:Int,housesListRaw:JsonArray?){
        val gson=Gson()
        if(responseCode==200 && housesListRaw!=null){
            for(element in housesListRaw) {
                //println(element)
                val house = gson.fromJson(element, Ownership::class.java)
                //println(house)
                housesList.add(house)
            }
        }
        updateOwnership()
    }

    private fun initHousesListView(){

            val listView = findViewById<ListView>(R.id.listOwnerships)
            listView.adapter = ownershipAdapter
        listView.setOnItemClickListener { _, clickedView, _, _ ->
            intentToHouse(clickedView)
        }
    }

    private fun intentToHouse(view: View){
        val houseIdTextView = view.findViewById<TextView>(R.id.houseIdVal)
        val houseId=houseIdTextView.text.toString()
        val intentToHouse= Intent(this,HomeActivity::class.java)
        intentToHouse.putExtra("token",token)
        intentToHouse.putExtra("houseId",houseId)
        startActivity(intentToHouse)
    }

    private fun updateOwnership(){
        runOnUiThread{
            ownershipAdapter.notifyDataSetChanged()
        }
    }
}

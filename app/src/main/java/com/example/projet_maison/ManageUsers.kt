package com.example.projet_maison

import android.os.Bundle
import android.view.View
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
import org.json.JSONObject

class ManageUsers : AppCompatActivity() {

    private lateinit var token: String;
    private lateinit var houseId: String;
    private var users = ArrayList<UserNOwnership>()
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var listViewUsers: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_users)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        token = intent.getStringExtra("token").toString()
        houseId = intent.getStringExtra("houseId").toString()
        usersAdapter=UsersAdapter(this,users)
        listViewUsers=findViewById(R.id.listViewUsers)
        initUsersListView()
        initUsers()


    }

    private fun initUsers() {
        Api().get<JsonArray>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",::initUsersSuccess,token)
    }

    private fun initUsersSuccess(responseCode:Int, userListRaw:JsonArray?){
        val gson= Gson()
        println(userListRaw)
        users.clear()
        if(responseCode==200 && userListRaw != null){
            //println(userListRaw[0])
            for(user in userListRaw){
                val currentUser = gson.fromJson(user, UserNOwnership::class.java)
                users.add(currentUser)
            }

            updateUsers()
        }
        if(responseCode==400){
            println("les données sont incorrectes")
        }
        if(responseCode==403){
            println("Accès interdit")
        }
        if(responseCode==500){
            println("Problème au niveau du serveur")
        }
        if (responseCode == 200 && userListRaw == null) {
            println("Code 200 mais resultat vide")
        }

    }

    private fun initUsersListView() {
        listViewUsers.adapter = usersAdapter
    }

    private fun updateUsers() {
        runOnUiThread {
            usersAdapter.notifyDataSetChanged()
        }
    }

    fun addUser(view: View){
        val userToAdd=findViewById<TextView>(R.id.editTxtUserToManage).text.toString()
        println(token)
        val userToAddCredentials=User(userToAdd)
        println(userToAddCredentials)
        Api().post<User>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",userToAddCredentials,::addUserSuccess,token)
    }

    private fun addUserSuccess(responseCode:Int){
        if (responseCode == 200) {
            println("Utilisateur ajouté")
            initUsers()
            runOnUiThread {
                findViewById<TextView>(R.id.editTxtUserToManage).text=""
                Toast.makeText(this,"Utilisateur ajouté", Toast.LENGTH_LONG).show()
            }
        }
        if(responseCode==400){
            println("Les données fournies sont incorrectes")
        }
        if (responseCode == 403) {
            println("Accès refusé")
        }
        if (responseCode == 500) {
            println("Une erreur s'est produite au niveau du serveur")
        }
    }

    fun deleteUser(view: View){
        val userToDelete=findViewById<TextView>(R.id.editTxtUserToManage).text.toString()
        val userToDeleteCredentials=User(userToDelete)
        Api().delete<User>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",userToDeleteCredentials,::deleteUserSuccess,token)
    }

    private fun deleteUserSuccess(responseCode:Int){
        if (responseCode == 200) {
            println("Suppression bien effectuée")
            initUsers()
            runOnUiThread {
                findViewById<TextView>(R.id.editTxtUserToManage).text=""
                Toast.makeText(this,"Suppression bien effectuée", Toast.LENGTH_LONG).show()
            }
        }
        if(responseCode==400){
            println("Les données fournies sont incorrectes")
        }
        if (responseCode == 403) {
            println("Accès refusé")
        }
        if (responseCode == 500) {
            println("Une erreur s'est produite au niveau du serveur")
        }

    }
}
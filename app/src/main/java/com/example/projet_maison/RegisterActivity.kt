package com.example.projet_maison

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun register(view: View){
        val login =findViewById<EditText>(R.id.txtRegisterLogin).text.toString()
        val password=findViewById<EditText>(R.id.txtRegisterPassword).text.toString()
        val credentials= Credentials(login,password)

        Api().post<Credentials>("https://polyhome.lesmoulinsdudev.com/api/users/register",credentials,::registerSuccess)
    }

    private fun registerSuccess(responseCode:Int){
        if(responseCode==200){
            finish()
        }
        if(responseCode==400){
            Toast.makeText(this,"Les données fournies sont incorrectes",Toast.LENGTH_SHORT).show()
        }
        if(responseCode==409){
            Toast.makeText(this,"Le login est déjà utilisé par un autre compte",Toast.LENGTH_SHORT).show()
        }
        if(responseCode==500){
            Toast.makeText(this,"Une erreur s'est produite au niveau du serveur",Toast.LENGTH_SHORT).show()
        }

    }
}
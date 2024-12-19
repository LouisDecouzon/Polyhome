package com.example.projet_maison
//page de connexion
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api
import com.google.gson.Gson
import com.google.gson.JsonObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun registerNewAccount(view: View){
        val intent= Intent(this,RegisterActivity::class.java)
        startActivity(intent)
    }

    fun login(view:View){
        val login =findViewById<EditText>(R.id.txtLogin).text.toString()
        val password =findViewById<EditText>(R.id.txtPassword).text.toString()
        val credentials=Credentials(login,password)
        Api().post<Credentials, JsonObject>("https://polyhome.lesmoulinsdudev.com/api/users/auth",credentials,::loginSuccess)
    }

    private fun loginSuccess(responseCode:Int,token:JsonObject?){
        if(responseCode==200){
            val gson=Gson()
            val authResponse=gson.fromJson(token,Token::class.java)
            val tokenToSend=authResponse.token
            val intent=Intent(this,HomeChoiceActivity::class.java)
            intent.putExtra("token",tokenToSend)
            startActivity(intent)
        }
        else{
            errorManage()
        }

    }
    private fun errorManage(){
        runOnUiThread{
            Toast.makeText(this,"Les données fournies sont incorrectes", Toast.LENGTH_LONG).show()
        }


    }
}
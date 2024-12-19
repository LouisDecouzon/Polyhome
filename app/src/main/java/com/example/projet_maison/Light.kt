package com.example.projet_maison

data class Light(
    var id:String,
    val type:String,
    val availableCommands:ArrayList<String>,//2 de long
    var power:Int
)

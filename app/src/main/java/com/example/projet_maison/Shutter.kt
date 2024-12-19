package com.example.projet_maison

data class Shutter(
    var id:String,
    val type:String,
    val availableCommands:ArrayList<String>,//3 de long
    var opening:Int
)

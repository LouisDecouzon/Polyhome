package com.example.projet_maison

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class LightAdapter(
    private val context: Context,
    private val dataSource:ArrayList<Light>
    ):BaseAdapter() {

    private val inflater: LayoutInflater =context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(p0: Int): Any {
        return dataSource[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(p0: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView=inflater.inflate(R.layout.two_column_list_item,parent,false)

        val light =getItem(p0) as Light
        val lightId=rowView.findViewById<TextView>(R.id.column1)
        val lightState=rowView.findViewById<TextView>(R.id.column2)
        lightId.text=light.id
        lightState.text=if(light.power==1)"On" else "Off"


        return rowView
    }

}
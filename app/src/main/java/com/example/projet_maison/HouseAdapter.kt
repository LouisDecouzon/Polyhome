package com.example.projet_maison

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class HouseAdapter(
    private val context: Context,
    private val dataSource:ArrayList<Ownership>
    ) : BaseAdapter(){
        private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(p0: Int): Any {
        return dataSource[p0]
    }

    override fun getItemId(p0: Int): Long {
        return dataSource[p0].houseId.toLong()
    }

    @SuppressLint("SetTextI18n", "ViewHolder")
    override fun getView(p0: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView=inflater.inflate(R.layout.houses_list_item,parent,false)

        val ownership=getItem(p0) as Ownership
        val houseId= rowView.findViewById<TextView>(R.id.houseIdVal)
        val ownershipBool= rowView.findViewById<TextView>(R.id.ownershipVal)
        houseId.text=ownership.houseId.toString()
        ownershipBool.text=ownership.owner.toString()
        return rowView
    }
}
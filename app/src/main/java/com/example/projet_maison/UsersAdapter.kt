package com.example.projet_maison

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class UsersAdapter (
    private val context: Context,
    private val dataSource:ArrayList<UserNOwnership>
    ): BaseAdapter() {
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
    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, convertView: View?, parent: ViewGroup?): View{
        val rowView=inflater.inflate(R.layout.two_column_list_item,parent,false)

        val userNOwnership=getItem(p0) as UserNOwnership
        val userLogin= rowView.findViewById<TextView>(R.id.column1)
        val ownership= rowView.findViewById<TextView>(R.id.column2)
        userLogin.text=userNOwnership.userLogin
        ownership.text=if(userNOwnership.owner==1)"Owner" else "Guest"

        return rowView
    }
}
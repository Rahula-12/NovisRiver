package com.learning.novisriverinterview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.learning.novisriverinterview.R
import com.learning.novisriverinterview.model.Employee

class EmployeeAdapter(): PagingDataAdapter<Employee, EmployeeAdapter.EmployeeViewHolder>(EmployeeDiffCallBack()) {

    class EmployeeViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val image=view.findViewById<ImageView>(R.id.employee_image)
        val employeeName=view.findViewById<TextView>(R.id.employee_name)
        val id=view.findViewById<TextView>(R.id.employee_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val layout=LayoutInflater.from(parent.context).inflate(R.layout.employee_detail,parent,false)
        return EmployeeViewHolder(layout)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        getItem(position)?.let {
            holder.employeeName.text=it.name
            holder.id.text=it.id.toString()
            Glide.with(holder.image).load(it.profileUrl).transform(CenterCrop()).into(holder.image)
        }
    }

}

class EmployeeDiffCallBack():DiffUtil.ItemCallback<Employee>() {
    override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem.name==newItem.name
    }

}
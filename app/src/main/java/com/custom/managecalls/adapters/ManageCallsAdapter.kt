package com.custom.managecalls.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.custom.managecalls.R

class ManageCallsAdapter(val context: Context, val itemList: ArrayList<String>): RecyclerView.Adapter<ManageCallsAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(
            R.layout.fake_call_item_layout, parent, false
        )
        return ItemViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return 25
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}
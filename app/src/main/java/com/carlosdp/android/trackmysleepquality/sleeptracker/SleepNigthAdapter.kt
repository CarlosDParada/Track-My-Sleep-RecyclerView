package com.carlosdp.android.trackmysleepquality.sleeptracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carlosdp.android.trackmysleepquality.R
import com.carlosdp.android.trackmysleepquality.TextItemViewHolder
import com.carlosdp.android.trackmysleepquality.database.SleepNight

class SleepNigthAdapter: RecyclerView.Adapter<TextItemViewHolder>(){
    var data = listOf<SleepNight>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size


    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        if (item.sleepQuality <= 1){
            holder.textView.setTextColor(Color.RED)
        }else{
            holder.textView.setTextColor(Color.GREEN)
        }
        holder.textView.text = item.sleepQuality.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.text_item_view, parent , false) as TextView
        return TextItemViewHolder(view)
    }


}/**/

package com.carlosdp.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.carlosdp.android.trackmysleepquality.database.SleepNight
import com.carlosdp.android.trackmysleepquality.databinding.ListItemSleepNightBinding

class SleepNightAdapter (val clickListener: SleepNigthListener): ListAdapter<SleepNight, SleepNightAdapter.ViewHolder>(SleepNightDiffCallback()){
//    var data = listOf<SleepNight>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

//    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!,clickListener)
    }

    class ViewHolder private  constructor(val binding: ListItemSleepNightBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: SleepNight, clickListener: SleepNigthListener) {
            binding.sleepDTO = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding.inflate(layoutInflater ,  parent , false)
                return ViewHolder(binding)
            }
        }

    }
}/**/

class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>(){

    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }
    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }
}


class SleepNigthListener(val clickListener: (sleep : SleepNight) -> Unit){
    fun onClick(night: SleepNight) = clickListener(night)
}

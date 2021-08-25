package org.kpu.taver

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class CafeAdapter(private var cafelist : ArrayList<CafeVO?>) : RecyclerView.Adapter<CafeAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cafename: TextView
        val totalSeat: TextView
        val peopleNum: TextView
        val item: ConstraintLayout
        init {
            cafename = view.findViewById(R.id.item_cafename)
            totalSeat = view.findViewById(R.id.item_totalSeat)
            peopleNum = view.findViewById(R.id.item_peopleNum)
            item = view.findViewById(R.id.item_rcv)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cafelist, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = cafelist.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cafename.text = cafelist[position]?.cafeName
        holder.totalSeat.text = cafelist[position]?.allTable.toString()
        holder.peopleNum.text = cafelist[position]?.peopleNum.toString()

        holder.item.setOnClickListener {
            val intent = Intent(holder.item?.context, CafeMapActivity::class.java )
            intent.putExtra("CafeVO", cafelist[position])
            holder.item.context.startActivity(intent)
        }
    }

}
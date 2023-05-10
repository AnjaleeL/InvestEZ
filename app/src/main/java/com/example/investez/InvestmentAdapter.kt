package com.example.investez

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InvestmentAdapter(private val investments: MutableList<Investment>) :
    RecyclerView.Adapter<InvestmentAdapter.InvestmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvestmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.quick_inves_item, parent, false)
        return InvestmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvestmentViewHolder, position: Int) {
        holder.bind(investments[position])
    }

    override fun getItemCount(): Int {
        return investments.size
    }

    fun updateData(newInvestments: List<Investment>) {
        investments.clear()
        investments.addAll(newInvestments)
        notifyDataSetChanged()
    }

    inner class InvestmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       // private val resultTextView: TextView = itemView.findViewById(R.id.resultTextView)
        private val assetTextView: TextView = itemView.findViewById(R.id.quick_item_name)
        val btnEditQuickInves: Button = itemView.findViewById(R.id.btnEditQuickInves)

        // private val marketTextView: TextView = itemView.findViewById(R.id.marketTextView)
        //private val paymentRequestIDTextView: TextView = itemView.findViewById(R.id.paymentRequestIDTextView)

        fun bind(investment: Investment) {
           // resultTextView.text = investment.result
            assetTextView.text = investment.asset
           // marketTextView.text = investment.market
            //paymentRequestIDTextView.text = investment.paymentRequestID

            btnEditQuickInves.setOnClickListener {
                val intent = Intent(itemView.context, QuickInvestmentViewPage::class.java)
                intent.putExtra("investmentId", investment.id)
                itemView.context.startActivity(intent)
            }
        }
    }
}







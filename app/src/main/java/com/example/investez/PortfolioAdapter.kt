package com.example.investez

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PortfolioAdapter(private var portfolios: MutableList<Portfolio>): RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.portfolio_item, parent, false)
        return PortfolioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val currentPortfolio = portfolios[position]
        holder.tvName.text = currentPortfolio.name
//        holder.tvInvestmentType.text = currentPortfolio.investmentType
//        holder.tvInvestmentAmount.text = currentPortfolio.investmentAmount.toString()
//        holder.tvProfitSoFar.text = currentPortfolio.profitSoFar.toString()
//        holder.tvNotes.text = currentPortfolio.notes

        holder.viewButton.setOnClickListener {
            val intent = Intent(it.context, SavedLayoutsActivity::class.java).apply {
                putExtra("portfolioId", currentPortfolio.portfolioId)
            }
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return portfolios.size
    }

    fun setPortfolios(portfolios: MutableList<Portfolio>) {
        this.portfolios = portfolios
        notifyDataSetChanged()
    }

    inner class PortfolioViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.port_item_name)
//        val tvInvestmentType: TextView = itemView.findViewById(R.id.tvInvestmentType)
//        val tvInvestmentAmount: TextView = itemView.findViewById(R.id.tvInvestmentAmount)
//        val tvProfitSoFar: TextView = itemView.findViewById(R.id.tvProfitSoFar)
//        val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)

        val viewButton: Button = itemView.findViewById(R.id.btnEditPortfolio)

    }
}

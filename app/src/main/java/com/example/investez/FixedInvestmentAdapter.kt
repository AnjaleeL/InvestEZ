package com.example.investez

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FixedInvestmentAdapter(private val context: Context, private val fixedInvestments: List<FixedInvestment>) :
    RecyclerView.Adapter<FixedInvestmentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bankTextView: TextView = itemView.findViewById(R.id.fixed_item_bank)
        val timePlanTextView: TextView = itemView.findViewById(R.id.fixed_item_time)
        val investAmountTextView: TextView = itemView.findViewById(R.id.fixed_item_invest_amount)
        //val maturityTextView: TextView = itemView.findViewById(R.id.maturity_text_view)
        //val profitTextView: TextView = itemView.findViewById(R.id.profit_text_view)
        val viewButton: Button = itemView.findViewById(R.id.btnviewfixed)
        val deleteButton: Button = itemView.findViewById(R.id.btn_fixDeleteG)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fixed_depo_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fixedInvestment = fixedInvestments[position]

        holder.bankTextView.text = fixedInvestment.bank
        holder.timePlanTextView.text = fixedInvestment.timePlan
        holder.investAmountTextView.text = fixedInvestment.investAmount.toString()
        //holder.maturityTextView.text = fixedInvestment.maturity.toString()
        //holder.profitTextView.text = fixedInvestment.profit.toString()

        holder.viewButton.setOnClickListener {
            val intent = Intent(context, FixedDeposite::class.java)
            intent.putExtra("fixedID", fixedInvestment?.fixedID)
            context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            val db = Firebase.firestore
            db.collection("fixedInvestments")
                .document(fixedInvestment?.fixedID ?: "")
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Investment deleted successfully", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, FixedDepositListView::class.java)
                    context.startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                    Toast.makeText(context, "Investment delete failed", Toast.LENGTH_SHORT).show()
                }
        }



    }

    override fun getItemCount(): Int {
        return fixedInvestments.size
    }
}
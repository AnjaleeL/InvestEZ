package com.example.investez

import android.content.ContentValues
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GoldAdapter(private val context: Context, private val goldList: List<Gold>) :
    RecyclerView.Adapter<GoldAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weightTextView: TextView = itemView.findViewById(R.id.weight_of_gold)
        val rateTextView: TextView = itemView.findViewById(R.id.rate_of_gold)
        val amountTextView: TextView = itemView.findViewById(R.id.amount_of_gold)
        val viewButton: Button = itemView.findViewById(R.id.btn_view_gold)
        val deleteButton: Button = itemView.findViewById(R.id.btn_delete_gold)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.gold_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gold = goldList[position]

        holder.weightTextView.text = gold.goldWeight
        holder.rateTextView.text = gold.goldRate
        holder.amountTextView.text = gold.amountOfGold.toString()

        holder.viewButton.setOnClickListener {
            val intent = Intent(context, GoldMainPage::class.java)
            intent.putExtra("goldID", gold?.goldID)
            context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            val db = Firebase.firestore
            db.collection("gold")
                .document(gold?.goldID ?: "")
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Record deleted successfully", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, goldListActivity::class.java)
                    context.startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error deleting document", e)
                    Toast.makeText(context, "Record delete failed", Toast.LENGTH_SHORT).show()
                }
        }

    }

    override fun getItemCount(): Int {
        return goldList.size
    }
}
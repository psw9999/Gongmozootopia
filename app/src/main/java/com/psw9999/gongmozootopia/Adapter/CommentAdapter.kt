package com.psw9999.gongmozootopia.Adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.gongmozootopia.data.CommentListItem

class CommentAdapter : RecyclerView.Adapter<CommentViewHolder>() {
    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        TODO("Not yet implemented")
    }
}


abstract class CommentViewHolder(
    itemView : View
)   : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item : CommentListItem)
}

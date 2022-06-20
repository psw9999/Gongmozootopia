package com.psw9999.gongmozootopia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.gongmozootopia.R
import com.psw9999.gongmozootopia.data.CommentListItem
import com.psw9999.gongmozootopia.databinding.HolderCommentListHeaderBinding
import com.psw9999.gongmozootopia.databinding.HolderCommentListItemBinding

class CommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var commentLists = arrayListOf<CommentListItem>()

    private fun getItem(position: Int): CommentListItem = this.commentLists[position]

    override fun getItemCount(): Int = this.commentLists.size

    override fun getItemViewType(position: Int): Int = getItem(position).layoutId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.holder_comment_list_header -> CommentHeaderViewHolder(
                HolderCommentListHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> CommentItemViewHolder(
                HolderCommentListItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is CommentHeaderViewHolder -> {
                holder.binding.textViewCommentHeader.text = commentLists[position].commentInfo.registDate
            }
            is CommentItemViewHolder -> {
                holder.binding.textViewCommentStcckName.text = commentLists[position].commentInfo.stockName
                holder.binding.textViewCommentContent.text = commentLists[position].commentInfo.comment
            }
        }
    }

    inner class CommentHeaderViewHolder(val binding: HolderCommentListHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    inner class CommentItemViewHolder(val binding : HolderCommentListItemBinding) : RecyclerView.ViewHolder(binding.root)

}
package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.psw9999.gongmozootopia.Adapter.CommentAdapter
import com.psw9999.gongmozootopia.Repository.CommentRepository
import com.psw9999.gongmozootopia.Util.GridViewDecoration
import com.psw9999.gongmozootopia.data.CommentListItem
import com.psw9999.gongmozootopia.databinding.FragmentCommentBinding
import kotlinx.coroutines.*

class CommentFragment : Fragment() {
    lateinit var binding : FragmentCommentBinding
    lateinit var commentAdapter: CommentAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentBinding.inflate(inflater, container, false)
        val comment = deferredComment.getCompleted()
        Log.d("comment","$comment")
        initRecyclerView(comment)
        return binding.root
    }

    private val deferredComment = CoroutineScope(Dispatchers.Default).async {
        // 1. 최근 코멘트 요청
        val deferredResponse = async(Dispatchers.IO) {
            CommentRepository().getComments()
        }
        val commentResponse = deferredResponse.await()
        val result = arrayListOf<CommentListItem>()
        var headerDate = ""
        commentResponse.forEach { comment ->
            comment.registDate = comment.registDate.split(' ')[0]
            if (headerDate != comment.registDate) {
                result.add(CommentListItem.Header(comment))
            }
            result.add(CommentListItem.Item(comment))
            headerDate = comment.registDate
        }
        result
    }

    private fun initRecyclerView(comments : ArrayList<CommentListItem>) {
        commentAdapter = CommentAdapter()
        commentAdapter.commentLists = comments
        binding.recyclerViewComment.adapter = commentAdapter
        binding.recyclerViewComment.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewComment.addItemDecoration(GridViewDecoration(30))
    }

}
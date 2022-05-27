package com.psw9999.gongmozootopia.UI.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.psw9999.gongmozootopia.Repository.CommentRepository
import com.psw9999.gongmozootopia.data.CommentListItem
import com.psw9999.gongmozootopia.databinding.FragmentCommentBinding
import kotlinx.coroutines.*

class CommentFragment : Fragment() {
    lateinit var binding : FragmentCommentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentBinding.inflate(inflater, container, false)
        val comment = deferredComment.getCompleted()
        return binding.root
    }

    val deferredComment = CoroutineScope(Dispatchers.Default).async {
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

}
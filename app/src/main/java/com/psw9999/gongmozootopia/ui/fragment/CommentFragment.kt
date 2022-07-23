package com.psw9999.gongmozootopia.ui.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.psw9999.gongmozootopia.Repository.CommentRepository
import com.psw9999.gongmozootopia.Util.GridViewDecoration
import com.psw9999.gongmozootopia.adapter.CommentAdapter
import com.psw9999.gongmozootopia.base.BaseApplication
import com.psw9999.gongmozootopia.base.BaseFragment
import com.psw9999.gongmozootopia.data.CommentListItem
import com.psw9999.gongmozootopia.databinding.FragmentCommentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class CommentFragment : BaseFragment<FragmentCommentBinding>(FragmentCommentBinding :: inflate) {

    lateinit var commentAdapter: CommentAdapter

    override fun observe() {

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
        binding.recyclerViewComment.addItemDecoration((GridViewDecoration(
            BaseApplication.dpToPx(
                requireContext(),
                10F
            ).toInt())))
    }

}
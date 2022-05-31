package com.psw9999.gongmozootopia.data

import com.psw9999.gongmozootopia.R

sealed class CommentListItem {
    abstract val commentInfo : CommentResponse
    abstract val layoutId : Int
    data class Header(
        override val commentInfo: CommentResponse,
        override val layoutId : Int = VIEW_TYPE
    ) : CommentListItem() {
        companion object {
            const val VIEW_TYPE = R.layout.holder_comment_list_header
        }
    }

    data class Item(
        override val commentInfo: CommentResponse,
        override val layoutId : Int = VIEW_TYPE
    ) : CommentListItem() {
        companion object {
            const val VIEW_TYPE = R.layout.holder_comment_list_item
        }
    }
}
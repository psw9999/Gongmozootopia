package com.psw9999.gongmozootopia.Util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.psw9999.gongmozootopia.R

object BindingConversion {
    @BindingAdapter("is_following")
    @JvmStatic
    fun setFollowingImage(materialToolbar : MaterialToolbar, isFollowing : Boolean) {
        materialToolbar.menu.findItem(R.id.action_following).setIcon(
            when (isFollowing) {
                true -> R.drawable.star_active
                else -> R.drawable.star_border
            }
        )
    }

    @BindingAdapter("is_following")
    @JvmStatic
    fun setFollowingImage(imageView : ImageView, isFollowing : Boolean) {
        imageView.isSelected = isFollowing
    }

}
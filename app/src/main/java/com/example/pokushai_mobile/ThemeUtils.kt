package com.example.pokushai_mobile

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

object ThemeUtils {
    fun applyThemeToPost(context: Context, view: View, lightDrawable: Int, darkDrawable: Int) {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            view.setBackgroundResource(darkDrawable)
        } else {
            view.setBackgroundResource(lightDrawable)
        }
    }

    fun applyThemeToLike(context: Context, linearLayout: LinearLayout, lightDrawable: Int, darkDrawable: Int) {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            linearLayout.setBackgroundResource(darkDrawable)
        } else {
            linearLayout.setBackgroundResource(lightDrawable)
        }
    }

    fun applyClickLike(context: Context, imageView: ImageView, trueClick: Int) {
        imageView.setColorFilter(ContextCompat.getColor(context, trueClick))
    }

    fun applyThemeToView(context: Context, imageView: ImageView, lightColor: Int, darkColor: Int) {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            imageView.setColorFilter(ContextCompat.getColor(context, darkColor))
        } else {
            imageView.setColorFilter(ContextCompat.getColor(context, lightColor))
        }
    }

}

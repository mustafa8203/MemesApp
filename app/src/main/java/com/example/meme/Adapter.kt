package com.example.meme

import android.widget.ImageView
import android.view.ViewGroup
import android.widget.ImageView.ScaleType
import androidx.recyclerview.widget.RecyclerView

class TemplateAdapter(
    private val images: List<Int>,
    private val selectedImageView: ImageView
) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

    class TemplateViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val imageView = ImageView(parent.context)
        val size = (parent.resources.displayMetrics.density * 100).toInt()
        val layoutParams = RecyclerView.LayoutParams(size, size)
        layoutParams.setMargins(8, 8, 8, 8)
        imageView.layoutParams = layoutParams
        imageView.scaleType = ScaleType.CENTER_CROP
        return TemplateViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
        holder.imageView.setOnClickListener {
            selectedImageView.setImageResource(images[position])
        }
    }

    override fun getItemCount() = images.size
}

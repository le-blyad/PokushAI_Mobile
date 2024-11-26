package com.example.pokushai_mobile
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.LinearLayout
import com.squareup.picasso.Picasso


class PostAdapter(private val posts: List<aboutPost>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val recipeName: TextView = itemView.findViewById(R.id.recipeName)
        val description: TextView = itemView.findViewById(R.id.description)
        val countLikes: TextView = itemView.findViewById(R.id.countLikes)
        val countViews: TextView = itemView.findViewById(R.id.countViews)
        val imageLikes: ImageView = itemView.findViewById(R.id.imageLikes)
        val imageViews: ImageView = itemView.findViewById(R.id.imageViews)
        val imagePost: ImageView = itemView.findViewById(R.id.image_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.userName.text = post.author
        holder.recipeName.text = post.title
        holder.description.text = post.des
        holder.countViews.text = post.views
        holder.countLikes.text = post.likes

        if (!post.image.isNullOrEmpty()){
            Picasso.get()
                .load(post.image)
                .placeholder(R.drawable.nophotostep)
                .into(holder.imagePost)
        } else {
            holder.imageViews.setImageResource(R.drawable.nophotostep)
        }

        holder.imageLikes.setImageResource(R.drawable.like)
        holder.imageViews.setImageResource(R.drawable.eye)



        var isLiked = false
        val likeView = holder.itemView.findViewById<LinearLayout>(R.id.likeView)
        likeView.isClickable = true
        likeView.setOnClickListener {
            isLiked = !isLiked

            if (isLiked) {
                ThemeUtils.applyClickLike(holder.itemView.context, holder.itemView.findViewById(R.id.imageLikes), R.color.red)
                // сюда нужен код добавления лайка
            } else {
                // сюда нужен код для того чтобы убрать лайк
                ThemeUtils.applyClickLike(holder.itemView.context, holder.itemView.findViewById(R.id.imageLikes), R.color.gray)
            }
        }

        ThemeUtils.applyThemeToPost(holder.itemView.context, holder.itemView.findViewById(R.id.postBackground), R.drawable.shape_light, R.drawable.shape_dark)
        ThemeUtils.applyThemeToLike(holder.itemView.context, holder.itemView.findViewById(R.id.likeView), R.drawable.like_view_light, R.drawable.like_view_dark)
        ThemeUtils.applyThemeToView(holder.itemView.context, holder.itemView.findViewById(R.id.imageViews), R.color.gray, R.color.white)
    }

    override fun getItemCount() = posts.size
}


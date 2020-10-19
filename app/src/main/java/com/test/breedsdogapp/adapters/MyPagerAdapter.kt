package com.test.breedsdogapp.adapters
import android.content.res.ColorStateList
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.test.breedsdogapp.R
import com.test.breedsdogapp.models.*
import com.test.breedsdogapp.ui.MainViewModel
import kotlinx.android.synthetic.main.item_image_page.view.*
import kotlinx.android.synthetic.main.item_list.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyPagerAdapter(private val items: Images) : RecyclerView.Adapter<PagerViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder =
        PagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_page, parent, false))

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) = holder.itemView.run {
        Picasso.get()
            .load(items.images[position].url)
            .placeholder(R.color.colorPrimaryDark)
            .fit()
            .centerCrop()
            .into(imgMain)

        installColor(imgLike, position)

        imgLike.setOnClickListener {
            changeLike(imgLike, position)
            onItemClickListener?.let { it(items.images[position]) }
        }

    }
    private fun installColor(imgLike:ImageView, position:Int){
        if(items.images[position].isLike)
            ImageViewCompat.setImageTintList(
                imgLike,
                ColorStateList.valueOf(Color.parseColor("#FF0000"))
            )
         else
            ImageViewCompat.setImageTintList(
                imgLike,
                ColorStateList.valueOf(Color.parseColor("#AAAAAA"))
            )

    }
    private fun changeLike(imgLike:ImageView, position:Int) {
        items.images[position].isLike = !items.images[position].isLike
        installColor(imgLike, position)
    }

    override fun getItemCount(): Int {
        return items.images.size
    }

    private var onItemClickListener: ((TImage) -> Unit)? = null

    fun setOnItemClickListener(listener: (TImage) -> Unit) {
        onItemClickListener = listener
    }

}

class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
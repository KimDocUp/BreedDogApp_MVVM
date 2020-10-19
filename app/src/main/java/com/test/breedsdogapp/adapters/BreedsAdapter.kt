package com.test.breedsdogapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.test.breedsdogapp.models.Breed
import kotlinx.android.synthetic.main.item_list.view.*
import com.test.breedsdogapp.R
import com.test.breedsdogapp.models.TBreed
import com.test.breedsdogapp.models.TBreedWithTImage
import com.test.breedsdogapp.models.SubBreed as SubBreed

class BreedsAdapter<T> : RecyclerView.Adapter<BreedsAdapter<T>.BreedViewHolder>() {

    inner class BreedViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<T>() {

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem != newItem
        }

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreedViewHolder {
        return BreedViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((T) -> Unit)? = null

    override fun onBindViewHolder(holder: BreedViewHolder, position: Int) {

        val breed:Any
        val sizeSubOrImg:Int
        val textSubOrImg:String
        val textName:String

        when {
            differ.currentList[position] is TBreedWithTImage -> {
                breed = differ.currentList[position] as TBreedWithTImage
                textName = breed.breed.name
                sizeSubOrImg = breed.images.size
                textSubOrImg = "(${sizeSubOrImg} photos)"
            }
            differ.currentList[position] is SubBreed -> {
                breed = differ.currentList[position] as SubBreed
                textName = breed.breedName
                sizeSubOrImg = breed.images.size
                textSubOrImg = "(${sizeSubOrImg} photos)"
            }
            else -> {
                breed = differ.currentList[position] as Breed
                textName = breed.breedName
                sizeSubOrImg = breed.subBreed.size
                textSubOrImg = "(${sizeSubOrImg} subbreeds)"
            }
        }

        holder.itemView.apply {
            txtNameBreed.text = textName.capitalize()
            if(sizeSubOrImg > 0) {
                txtCountSubBreeds.visibility = View.VISIBLE
                txtCountSubBreeds.text = textSubOrImg
            } else txtCountSubBreeds.visibility = View.GONE

            setOnClickListener {
                if(breed is Breed)
                    onItemClickListener?.let { it(breed as T) }
                else if(breed is TBreedWithTImage)
                    onItemClickListener?.let { it(breed as T) }
                else
                    onItemClickListener?.let { it(breed as T) }
            }

        }
    }

    fun setOnItemClickListener(listener: (T) -> Unit) {
        onItemClickListener = listener
    }

}
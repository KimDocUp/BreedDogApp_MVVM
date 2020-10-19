package com.test.breedsdogapp.ui.fragments

import android.content.res.ColorStateList
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.test.breedsdogapp.R
import com.test.breedsdogapp.adapters.MyPagerAdapter
import kotlinx.android.synthetic.main.fragment_images.*
import com.squareup.picasso.Picasso.Builder
import com.test.breedsdogapp.models.Images
import com.test.breedsdogapp.models.TBreed
import com.test.breedsdogapp.models.TImage
import com.test.breedsdogapp.ui.MainActivity
import com.test.breedsdogapp.ui.MainViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImagesFragment : Fragment(R.layout.fragment_images){

    val args: ImagesFragmentArgs by navArgs()
    lateinit var viewModel: MainViewModel
    lateinit var mImages : Images

    var delImage : MutableList<TImage> = mutableListOf()
    var addImage : MutableList<TImage> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = (activity as MainActivity).viewModel

        mImages = args.subOrBreedImages
        val myPagerAdapter =  MyPagerAdapter(mImages)
        viewPager2.adapter = myPagerAdapter
        //viewPager2.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager2.offscreenPageLimit = 5
        myPagerAdapter.setOnItemClickListener {
            if(!it.isLike) {
                delImage.add(it)
                addImage.remove(it)
            } else {
                delImage.remove(it)
                addImage.add(it)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_images, menu)
    }

    override fun onDestroy() {
        super.onDestroy()

        GlobalScope.launch {
            if(addImage.size > 0) {
                try {
                    var item: Long = viewModel.checkName(mImages.name)!!.toLong()
                    if (item == 0L) {
                        val i: Long? = viewModel.insertFavoriteBreed(TBreed(name = mImages.name))
                        if (i != null) {
                            item = i
                        }
                    }
                    for (addItem in addImage) {
                        val image = TImage(url = addItem.url, isLike = true, breed_id = item)
                        viewModel.insertFavoriteImage(image)
                    }

                } catch (exSQL: SQLiteConstraintException) {

                } finally {
                    viewModel.getTBreedWithTImages()
                }
            }
            if(delImage.size> 0) {
                try {
                    for (delItem in delImage) {
                        viewModel.deleteImages(delItem.url)
                        val splitName = mImages.name.split(" ")
                        Log.d("TESTT","${splitName.size}")
                        if(splitName.size == 1) {
                            for(removedLike in viewModel.breakingBreeds?.value?.data!!)
                                if(removedLike.breedName.equals(mImages.name))
                                    for(img in removedLike.images)
                                        if(img.url.equals(delItem.url))
                                            img.isLike = false

                        } else if(splitName.size == 2) {
                            for(removedLike in viewModel.breakingBreeds?.value?.data!!)
                                if(removedLike.breedName.capitalize().equals(splitName[0].capitalize()))
                                    for(subLike in removedLike.subBreed)
                                        if(subLike.breedName.capitalize().equals(splitName[1].capitalize()))
                                            for(img in subLike.images)
                                                if(img.url.equals(delItem.url))
                                                    img.isLike = false

                        }
                        mImages.images.remove(delItem)
                    }

                    if (mImages.images.size < 1) {
                        viewModel.deleteBreed(mImages.name)
                    }
                } catch (exSQL: SQLiteConstraintException) {

                } finally {
                    viewModel.getTBreedWithTImages()
                }
            }
        }

    }

}
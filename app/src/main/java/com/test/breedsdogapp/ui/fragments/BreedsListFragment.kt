package com.test.breedsdogapp.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.breedsdogapp.adapters.BreedsAdapter
import com.test.breedsdogapp.ui.MainActivity
import com.test.breedsdogapp.ui.MainViewModel
import com.test.breedsdogapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breeds_list.*
import com.test.breedsdogapp.R
import com.test.breedsdogapp.models.Breed
import com.test.breedsdogapp.models.Images
import com.test.breedsdogapp.models.SubBreed
import com.test.breedsdogapp.models.TBreedWithTImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BreedsListFragment : Fragment(R.layout.fragment_breeds_list){

    lateinit var viewModel: MainViewModel
    lateinit var breedsAdapter: BreedsAdapter<Breed>
    lateinit var loadDialog: LoadingDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()
        loadDialog = LoadingDialog(activity as MainActivity)

        breedsAdapter.setOnItemClickListener {
            if(it.subBreed.isEmpty()) {
                GlobalScope.launch(Dispatchers.Main) {
                    if(it.images.size<1) {
                        viewModel.safeBreakingImagesCall(it)
                    }

                    val images = Images(it.breedName, it.images)
                    val bundle = Bundle().apply {
                        putSerializable("subOrBreedImages", images)
                        putSerializable("title", it.breedName.capitalize())
                    }
                    findNavController().navigate(
                        R.id.action_breedsListFragment_to_imagesFragment,
                        bundle
                    )
                }

            } else {
                val bundle = Bundle().apply {
                    putSerializable("breed", it)
                    putSerializable("title", it.breedName.capitalize())
                }
                findNavController().navigate(
                    R.id.action_breedsListFragment_to_subBreedsListFragment,
                    bundle
                )
            }
        }

        viewModel.breakingBreeds?.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    loadDialog.dialog.hide()
                    response.data?.let {
                        breedsAdapter.differ.submitList(it)
                        breedsAdapter.notifyDataSetChanged()
                    }
                }
                is Resource.Error -> {
                    loadDialog.dialog.hide()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    loadDialog.dialog.show()
                }
            }
        })

    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= 20
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                //viewModel.getBreakingBreeds()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        breedsAdapter = BreedsAdapter<Breed>()
        recyclerViewBreeds.apply {
            adapter = breedsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


}
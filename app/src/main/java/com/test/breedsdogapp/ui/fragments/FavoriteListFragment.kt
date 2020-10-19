package com.test.breedsdogapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.breedsdogapp.R
import com.test.breedsdogapp.adapters.BreedsAdapter
import com.test.breedsdogapp.models.Images
import com.test.breedsdogapp.models.TBreedWithTImage
import com.test.breedsdogapp.ui.MainActivity
import com.test.breedsdogapp.ui.MainViewModel
import com.test.breedsdogapp.util.Resource
import kotlinx.android.synthetic.main.fragment_favorite_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavoriteListFragment : Fragment(R.layout.fragment_favorite_list){

    lateinit var viewModel: MainViewModel
    lateinit var breedsAdapterSQLite: BreedsAdapter<TBreedWithTImage>
    lateinit var loadDialog: LoadingDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()
        loadDialog = LoadingDialog(activity as MainActivity)

        breedsAdapterSQLite.setOnItemClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val images = Images(it.breed.name, it.images)
                val bundle = Bundle().apply {
                    putSerializable("subOrBreedImages", images)
                    putSerializable("title", it.breed.name.capitalize())
                }
                findNavController().navigate(
                    R.id.action_favoriteListFragment_to_imagesFragment,
                    bundle
                )
            }
        }

        viewModel.breakingBreedsSQLite?.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    loadDialog.dialog.hide()
                    response.data?.let {
                        breedsAdapterSQLite.differ.submitList(it)
                        breedsAdapterSQLite.notifyDataSetChanged()
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

    private fun setupRecyclerView() {
        breedsAdapterSQLite = BreedsAdapter()
        recyclerViewFavorite.apply {
            adapter = breedsAdapterSQLite
            layoutManager = LinearLayoutManager(activity)
        }
    }

}
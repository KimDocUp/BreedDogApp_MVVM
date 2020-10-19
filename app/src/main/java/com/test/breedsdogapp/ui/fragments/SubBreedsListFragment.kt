package com.test.breedsdogapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.breedsdogapp.R
import com.test.breedsdogapp.adapters.BreedsAdapter
import com.test.breedsdogapp.models.Images
import com.test.breedsdogapp.models.SubBreed
import com.test.breedsdogapp.ui.MainActivity
import com.test.breedsdogapp.ui.MainViewModel
import com.test.breedsdogapp.util.Resource
import kotlinx.android.synthetic.main.fragment_sub_breeds_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SubBreedsListFragment : Fragment(R.layout.fragment_sub_breeds_list){

    lateinit var viewModel: MainViewModel
    lateinit var subBreedsAdapter: BreedsAdapter<SubBreed>
    lateinit var loadDialog: LoadingDialog

    val args: SubBreedsListFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()
        loadDialog = LoadingDialog(activity as MainActivity)

        val mBreed = args.breed

        subBreedsAdapter.setOnItemClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if (it.images.size < 1) {
                    viewModel.safeBreakingSubImagesCall(mBreed, it)
                }
                val images = Images(mBreed.breedName.capitalize()+" "+it.breedName.capitalize(), it.images)
                val bundle = Bundle().apply {
                    putSerializable("subOrBreedImages", images)
                    putSerializable("title", "${mBreed.breedName.capitalize()} ${it.breedName.capitalize()}")
                }
                findNavController().navigate(
                    R.id.action_subBreedsListFragment_to_imagesFragment,
                    bundle
                )
            }
        }

        viewModel.breakingBreeds?.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    loadDialog.dialog.hide()
                    response.data?.let {
                        subBreedsAdapter.differ.submitList(mBreed.subBreed)
                        subBreedsAdapter.notifyDataSetChanged()
                    }
                }
                is Resource.Error -> {
                    loadDialog.dialog.hide()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                is Resource.Loading -> {
                    loadDialog.dialog.show()
                }
            }
        })

    }

    private fun setupRecyclerView() {
        subBreedsAdapter = BreedsAdapter()
        recyclerViewSubBreeds.apply {
            adapter = subBreedsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


}
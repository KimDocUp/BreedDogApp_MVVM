package com.test.breedsdogapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import com.test.breedsdogapp.models.*
import com.test.breedsdogapp.repository.MainRepository
import com.test.breedsdogapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MainViewModel(app: Application, val mainRepository: MainRepository) : AndroidViewModel(app) {

    val breakingBreeds: MutableLiveData<Resource<List<Breed>>>? = MutableLiveData()
    val breakingBreedsSQLite: MutableLiveData<Resource<MutableList<TBreedWithTImage>>>? = MutableLiveData()

    init {
        getTBreedWithTImages()
        GlobalScope.launch(Dispatchers.IO) {
            safeBreakingSubBreedsPost(safeBreakingBreedsCall())
        }
    }

    fun getTBreedWithTImages() {
        GlobalScope.launch {
            breakingBreedsSQLite?.postValue(Resource.Success(mainRepository.loadAll()!!))
        }
    }
    suspend fun checkName(name:String) = mainRepository.checkName(name)
    suspend fun insertFavoriteBreed(breed: TBreed) = mainRepository.insertFavoriteBreed(breed)!!

    suspend fun insertFavoriteImage(image: TImage) = mainRepository.insertFavoriteImage(image)
    suspend fun deleteImages(url: String) = mainRepository.deleteImages(url)
    suspend fun deleteBreed(name: String) = mainRepository.deleteBreed(name)

    fun getImages(item: Long) = mainRepository.getImages(item)

    private suspend fun safeBreakingSubBreedsPost(resource: Resource<List<Breed>>){
        if(resource.data != null) {
            for (item in resource.data) {
                GlobalScope.launch {
                    safeBreakingSubBreedsCall(resource, item)
                }
            }
        }
    }

    internal suspend fun safeBreakingSubImagesCall(breed: Breed, subBreed: SubBreed) {
        val vm = breakingBreeds?.value
        breakingBreeds?.postValue(Resource.Loading())
        try {
            if(hasInternetConnection(context = getApplication())) {
                val response = mainRepository.getSubBreedImages(breed.breedName, subBreed.breedName)
                val h = handleBreakingSubImagesResponse(response, subBreed, breed.breedName)
                subBreed.images.addAll(h)
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> breakingBreeds?.postValue(Resource.Error("Network Failure"))
                else -> breakingBreeds?.postValue(Resource.Error("Conversion Error"))
            }
        } finally {
            breakingBreeds?.postValue(vm)
        }
    }

    private fun handleBreakingSubImagesResponse(response: Response<MessageResponse>, subBreed:SubBreed, breedName:String) : ArrayList<TImage> {
        if(response.isSuccessful) {
            val mSImg:ArrayList<TImage> = ArrayList()
            response.body()?.let { resultResponse ->
                if(subBreed.images.size < 1)
                    for(item in resultResponse.messageData)
                        mSImg.add(TImage(url = item, isLike = checkLike(breedName+" "+subBreed.breedName, item)))
                return mSImg
            }
        }
        return arrayListOf()
    }

    internal suspend fun safeBreakingImagesCall(breed: Breed) {
        val vm = breakingBreeds?.value
        breakingBreeds?.postValue(Resource.Loading())
        try {
            if(hasInternetConnection(context = getApplication())) {
                val response = mainRepository.getBreedImages(breed.breedName)
                val h = handleBreakingImagesResponse(response,breed.breedName)
                breed.images.addAll(h)
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> breakingBreeds?.postValue(Resource.Error("Network Failure"))
                else -> breakingBreeds?.postValue(Resource.Error("Conversion Error"))
            }
        } finally {
            breakingBreeds?.postValue(vm)
        }
    }

    private fun handleBreakingImagesResponse(response: Response<MessageResponse>, breedName: String) : ArrayList<TImage> {
        if(response.isSuccessful) {
            val mImg:ArrayList<TImage> = ArrayList()
            response.body()?.let { resultResponse ->
                for(item in resultResponse.messageData) {
                    mImg.add(TImage(url = item, isLike = checkLike(breedName, item)))
                }
                return mImg
            }
        }
        return arrayListOf()
    }

    private fun checkLike(breedName:String, url: String) : Boolean {
        for(breedSQLite in breakingBreedsSQLite?.value?.data!!)
            if(breedSQLite.breed.name.equals(breedName))
                for(imgSQLite in breedSQLite.images)
                    if(imgSQLite.url.capitalize().equals(url.capitalize()))
                        return true
        return false
    }

    private suspend fun safeBreakingSubBreedsCall(resource: Resource<List<Breed>>, b: Breed) {

        try {
            if(hasInternetConnection(context = getApplication())) {
                val response = mainRepository.getSubBreeds(b.breedName)
                val handle = handleBreakingSubBreedsResponse(response)
                b.subBreed.addAll(handle)
                if(resource.data?.get(resource.data.size-1) == b) {
                    breakingBreeds?.postValue(resource)
                }
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> breakingBreeds?.postValue(Resource.Error("Network Failure"))
                else -> breakingBreeds?.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private fun handleBreakingSubBreedsResponse(response: Response<MessageResponse>) : MutableList<SubBreed> {
        if(response.isSuccessful) {
            val mSub:MutableList<SubBreed> = ArrayList()
            response.body()?.let { resultResponse ->
                for(item in resultResponse.messageData)
                    mSub.add(SubBreed(breedName = item))
                return mSub
            }
        }
        return arrayListOf()
    }


    private suspend fun safeBreakingBreedsCall() : Resource<List<Breed>> {
        breakingBreeds?.postValue(Resource.Loading())
        try {
            if(hasInternetConnection(context = getApplication())) {
                val response = mainRepository.getBreeds()
                val handle = handleBreakingBreedsResponse(response)
                //breakingBreeds?.postValue(handle)
                return handle
            } else {
                breakingBreeds?.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> breakingBreeds?.postValue(Resource.Error("Network Failure"))
                else -> breakingBreeds?.postValue(Resource.Error("Conversion Error"))
            }
        }
        return Resource.Error("Network Failure")
    }

    private fun handleBreakingBreedsResponse(response: Response<MessageResponse>) : Resource<List<Breed>> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                val mB = mutableListOf<Breed>()
                for(item in resultResponse.messageData)
                    mB.add(Breed(item))

                return Resource.Success(mB)
            }
        }
        return Resource.Error(response.message())
    }


    private fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}
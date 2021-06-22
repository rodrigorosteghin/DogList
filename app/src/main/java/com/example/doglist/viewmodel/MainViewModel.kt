package com.example.doglist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doglist.APIService
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel(){
    private val viewModelJob = Job()
    private val uiScope =  CoroutineScope(Dispatchers.Main + viewModelJob)

    val puppies : MutableLiveData<List<String>> = MutableLiveData()
    val observableError : MutableLiveData<Boolean> = MutableLiveData()

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun searchByName(query:String){
        uiScope.launch {

            val response = withContext(Dispatchers.IO) {
                 getRetrofit().create(APIService::class.java).getDogsByBreeds("$query/images")
            }
            if (response.isSuccessful) {
                response.body()?.let {
                    puppies.postValue(it.images)
                }?:run{
                    puppies.postValue(null)
                }

            }else{
                observableError.postValue(true)

            }
        }

    }
}




package cs477.gmu.project3_rdelphec.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class CreateStoryViewModel : ViewModel() {
    // TODO: Implement the ViewModel
//    private val _text = MutableLiveData<String>().apply {
//        value = "this is create story fragment"
//    }
//    val text: LiveData<String> = _text
    private val _storyText = MutableLiveData<String>()
    val storyText: LiveData<String> = _storyText

    fun generateStory(imageFile: File, prompt: String){

    }
}
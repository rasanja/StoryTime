package cs477.gmu.project3_rdelphec.ui.create

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.graphics.Matrix
import android.graphics.drawable.Icon
import android.view.Surface
import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.contentReceiver
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cs477.gmu.project3_rdelphec.R
import cs477.gmu.project3_rdelphec.ui.theme.StoryTimeTheme
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment


class CreateStoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                UIPreview()
            }
        }

    }

    @Preview(showBackground = true)
    @Composable
    fun CreateStoryUI(){
        var isCameraOpen by remember { mutableStateOf(false) }
        val viewModel = viewModel<CreateStoryViewModel>()
        val photo by viewModel.bitmap.collectAsState()

        if(isCameraOpen){ //show camera preview
            CameraUI(
                onClose = {isCameraOpen = false}
            )
        }else{ //show CreateStoryUI
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){

                //Row 1 : Camera Button UI
                Row (
                    modifier = Modifier
                        .weight(0.5f),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    LaunchCameraButton(
                        onClick = { isCameraOpen = true}
                    )
                }

                //Row 2: attachment UI
                if(photo!=null){
                    PhotoAttachmentPreview(
                        bitmap = photo!!,
                        onRemove = {viewModel.onTakePhoto(null)}
                    )
                }

                // Row 3: text submission UI
                var prompt by remember { mutableStateOf("") }
                PromptInputRowUI(
                    prompt = prompt,
                    onPromptChange = { prompt = it},
                    onSend = {} //handle prompt submission
                )


                //Row 4 : LLM API response UI
                var storyText by remember { mutableStateOf("Once upon a time...") }
                StoryOutputUI(
                    storyText = storyText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f)
                )
            }
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CameraUI(
        onClose:() -> Unit
    ){
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val controller = remember {
            LifecycleCameraController(context).apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE //if video capture is needed CameraController.VIDEO_CAPTURE
                )
            }
        }

        val viewModel = viewModel<CreateStoryViewModel>()
        val photoBitmap by viewModel.bitmap.collectAsState()

        if(photoBitmap == null){ //Photo not taken yet
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ){
                CameraPreview(
                    controller = controller,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { //allow user to tap and switch camera
                        controller.cameraSelector =
                            if(controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            }else{
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                    },
                    modifier = Modifier
                        .offset(16.dp, 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "switch Camera"
                    )
                }
                Row ( //row for capturing image
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    IconButton( //open gallery button, implement this later
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = "Open Gallery",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton( //take photo button
                        onClick = {
                            takePhoto(
                                controller = controller,
                                onPhotoTaken = viewModel::onTakePhoto
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Take photo",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }else{ //photo taken, confirm if they want to keep it
            ConfirmPhotoUI(
                bitmap = photoBitmap!!,
                onConfirm = { onClose() },
                onDiscard = { viewModel.onTakePhoto(null) }
            )
        }
    }


    @Composable
    fun StoryOutputUI(
        storyText: String,
        modifier: Modifier = Modifier
    ){
        if (storyText.isNotBlank()) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = "Generated Story",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = storyText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }

    @Composable
    fun LaunchCameraButton(
        onClick: () ->Unit
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clickable{ onClick() },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Launch Camera",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    @Composable
    fun PromptInputRowUI(
        prompt: String,
        onPromptChange: (String) -> Unit,
        onSend: ()->Unit
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            TextField(
                value = prompt,
                onValueChange = onPromptChange,
                placeholder = {Text("Guide your story...")},
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
            IconButton(
                onClick = onSend //handle user input here
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send prompt",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    @Composable
    fun ConfirmPhotoUI(
        bitmap: Bitmap,
        onConfirm: () -> Unit,
        onDiscard: () -> Unit
    ){

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured photo",
                modifier =  Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                contentScale = ContentScale.Crop
            )

            Row (
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Surface (
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f)
                ){
                    IconButton( //confirm photo
                        onClick =  onConfirm
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirm image",
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Surface (
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f)
                ){
                    IconButton( //cancel and retake photo
                        onClick = onDiscard
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "retake image",
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }


    @Composable
    fun PhotoAttachmentPreview(
        bitmap: Bitmap,
        onRemove: ()->Unit
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp, bottom = 8.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(8.dp)
        ){
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "attached photo",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Remove photo",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    @Composable
    private fun UIPreview(){
        StoryTimeTheme {
            CreateStoryUI()
        }
    }



    private fun takePhoto(
        controller: LifecycleCameraController,
        onPhotoTaken: (Bitmap) -> Unit
    ){
        controller.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : OnImageCapturedCallback (){
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }

                    val rotatedBitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        matrix,
                        true
                    )

                    onPhotoTaken(rotatedBitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Couldnt take photo: ", exception)
                }

            }
        )
    }


}
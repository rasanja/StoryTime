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
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cs477.gmu.project3_rdelphec.ui.theme.StoryTimeTheme
import kotlinx.coroutines.launch


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
        if(isCameraOpen){
            CameraUI(
                onClose = {isCameraOpen = false}
            )
        }else{
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){ //row 1 with label and camera button
                    Button(
                        onClick = { //launch camera
                            isCameraOpen = true
                        }
                    ) {
                        Text("Open Camera")
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){ //row 2 with text field with generate story botton

                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f),
                    verticalAlignment = Alignment.Top,
                ){ //row 3 with story output

                }
            }
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CameraUI(onClose:() -> Unit){
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberBottomSheetScaffoldState()
        val context = LocalContext.current
        val controller = remember {
            LifecycleCameraController(context).apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE //if video capture is needed CameraController.VIDEO_CAPTURE
                )
            }
        }

        val viewModel = viewModel<CreateStoryViewModel>()
        val bitmaps by viewModel.bitmaps.collectAsState()

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 0.dp,
            sheetContent = {
                PhotoBottomSheet(  //Photo bottom sheet UI
                    bitmaps = bitmaps,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
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
                    IconButton( //open gallery button
                        onClick = {
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = "Open Gallery"
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
                            contentDescription = "Take photo"
                        )
                    }
                }
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
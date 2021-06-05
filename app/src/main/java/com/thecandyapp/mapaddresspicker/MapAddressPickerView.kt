package com.thecandyapp.mapaddresspicker

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.LatLng

@Composable
fun MapAddressPickerView(viewModel: MapViewModel){
    Surface(color = MaterialTheme.colors.background) {
        val mapView = rememberMapViewWithLifecycle()
        val currentLocation = viewModel.location.collectAsState()
        var text by remember { viewModel.addressText }
        val context = LocalContext.current

        Column(Modifier.fillMaxWidth()) {

            Box{
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                        if(!viewModel.isMapEditable.value)
                            viewModel.onTextChanged(context, text)
                    },
                    modifier = Modifier.fillMaxWidth().padding(end = 80.dp),
                    enabled = !viewModel.isMapEditable.value,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
                )

                Column(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).padding(bottom = 20.dp),
                    horizontalAlignment = Alignment.End
                ){
                    Button(
                        onClick = {
                            viewModel.isMapEditable.value = !viewModel.isMapEditable.value
                        }
                    ) {
                        Text(text = if(viewModel.isMapEditable.value) "Edit" else "Save")
                    }
                }
            }

            Box(modifier = Modifier.height(500.dp)){

                currentLocation.value.let {
                    if(viewModel.isMapEditable.value) {
                        text = viewModel.getAddressFromLocation(context)
                    }
                    MapViewContainer(viewModel.isMapEditable.value, mapView, viewModel)
                }

                MapPinOverlay()
            }
        }
    }
}

@Composable
fun MapPinOverlay(){
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ){
            Image(
                modifier = Modifier.size(50.dp),
                bitmap = ImageBitmap.imageResource(id = R.drawable.pin).asAndroidBitmap().asImageBitmap(),
                contentDescription = "Pin Image"
            )
        }
        Box(
            Modifier.weight(1f)
        ){}
    }
}

@Composable
private fun MapViewContainer(
    isEnabled: Boolean,
    mapView: MapView,
    viewModel: MapViewModel
) {
    AndroidView(
        factory = { mapView }
    ) {

        mapView.getMapAsync { map ->

            map.uiSettings.setAllGesturesEnabled(isEnabled)

            val location = viewModel.location.value
            val position = LatLng(location.latitude, location.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,  15f))

            map.setOnCameraIdleListener {
                val cameraPosition = map.cameraPosition
                viewModel.updateLocation(cameraPosition.target.latitude, cameraPosition.target.longitude)
            }
        }

    }
}

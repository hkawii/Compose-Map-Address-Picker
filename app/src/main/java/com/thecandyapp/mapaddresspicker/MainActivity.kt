package com.thecandyapp.mapaddresspicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MapAddressPickerView(viewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
        val vm = MapViewModel()
        MapAddressPickerView(vm)
}
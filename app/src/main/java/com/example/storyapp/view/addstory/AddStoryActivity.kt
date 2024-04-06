package com.example.storyapp.view.addstory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.example.storyapp.view.ViewModelFactoryStory
import com.example.storyapp.view.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import java.util.concurrent.TimeUnit

class AddStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactoryStory.getInstance(this)
    }

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.currentImageUri.observe(this) { uri ->
            showImage(uri)
        }


        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.btnCamera.setOnClickListener {
            startCamera()
        }

        binding.cbLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                createLocationRequest()
                viewModel.setIsUseLocation(true)
            } else {
                viewModel.setIsUseLocation(false)
            }
        }

        binding.btnUpload.setOnClickListener {
            val description = binding.edDescription.text.toString()
            uploadImage(viewModel.currentImageUri.value, description)
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
    }

    private val resolutionLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    Toast.makeText(
                        this,
                        getString(R.string.location_activated),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                RESULT_CANCELED -> {
                    Toast.makeText(this, getString(R.string.enable_gps), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    @Suppress("DEPRECATION")
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(this, sendEx.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
            when {
                permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {
                    binding.cbLocation.isChecked = false
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.setLocation(location)
                    Toast.makeText(
                        this,
                        getString(R.string.get_my_location, location.latitude, location.longitude),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        viewModel.setImageUriCamera(getImageUri(this))
        launcherIntentCamera.launch(getImageUri(this))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setCurrentImageUri(uri)
        } else {
            if (viewModel.currentImageUri.value == null) {
                showSnackBar(getString(R.string.no_media_selected))
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.imageUriCamera.value?.let { viewModel.setCurrentImageUri(it) }
        } else {
            if (viewModel.currentImageUri.value == null) {
                showSnackBar(getString(R.string.no_picture_taken))
            }
        }
    }

    private fun showImage(uri: Uri?) {
        if (uri != null) {
            binding.ivPreview.setImageURI(uri)
        }
    }

    private fun uploadImage(uri: Uri?, description: String) {
        if (uri != null) {
            val imageFile = uriToFile(uri, this).reduceFileImage()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            viewModel.isUseLocation.observe(this) { useLocation ->
                if (useLocation) {
                    viewModel.location.observe(this) { location ->
                        if (location != null) {
                            val lat = location.latitude
                            val lon = location.longitude
                            viewModel.addNewStoryWithLocation(multipartBody, requestBody, lat, lon)
                                .observe(this) { result ->
                                    if (result != null) {
                                        when (result) {
                                            is Result.Loading -> {
                                                binding.pbAddStory.visibility = View.VISIBLE
                                            }

                                            is Result.Success -> {
                                                binding.pbAddStory.visibility = View.GONE
                                                val message = result.data.message
                                                alertResponse(message.toString())
                                            }

                                            is Result.Error -> {
                                                binding.pbAddStory.visibility = View.GONE
                                                val message = result.error
                                                Snackbar.make(
                                                    binding.root,
                                                    message,
                                                    Snackbar.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                        }
                    }

                } else {
                    viewModel.addNewStory(multipartBody, requestBody)
                        .observe(this) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        binding.pbAddStory.visibility = View.VISIBLE
                                    }

                                    is Result.Success -> {
                                        binding.pbAddStory.visibility = View.GONE
                                        val message = result.data.message
                                        alertResponse(message.toString())
                                    }

                                    is Result.Error -> {
                                        binding.pbAddStory.visibility = View.GONE
                                        val message = result.error
                                        Snackbar.make(
                                            binding.root,
                                            message,
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                }
            }

        } else {
            showSnackBar(getString(R.string.image_not_available))
        }
    }

    private fun alertResponse(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success))
            setMessage(message)
            setPositiveButton(getString(R.string.ok)) { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this, getString(R.string.style_parsing_failed), Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Resources.NotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.cannot_found_style, e.message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
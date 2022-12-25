package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(),OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private var marker:Marker?=null

    private val TAG= SelectLocationFragment::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1
    private val fusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment= childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
           mapFragment.getMapAsync(this)




        return binding.root
    }
//         call this function after the user confirms on the selected location

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       binding.saveButton.setOnClickListener { onLocationSelected() }
    }



    private fun onLocationSelected() {
        //         When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
         marker?.let { marker ->
             _viewModel.latitude.value= marker.position.latitude
             _viewModel.longitude.value= marker.position.longitude
             _viewModel.navigationCommand.value=NavigationCommand.Back
             _viewModel.reminderSelectedLocationStr.value= marker.title
         }



    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
           map.mapType= GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
          map.mapType= GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
           map.mapType= GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
           map.mapType= GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map=googleMap
        setMapLongClick(map)
        setPoiClick(map)
        setMapStyle(map)
        if (isPermissionGranted()){
            userLocation()
        }else{
            requestLocationPermission()
        }


    }

    private fun requestLocationPermission(){
       if( ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                  map.isMyLocationEnabled= true
       }else{this.requestPermissions(
           arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
           REQUEST_LOCATION_PERMISSION
       )}



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
          super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                userLocation()
            }else{ alertDialog()}

    }

    private fun alertDialog(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
              AlertDialog.Builder(requireActivity()).setTitle(R.string.location_permission)
                  .setMessage(R.string.permission_denied_explanation).setPositiveButton("OK"){_,_,->
                      requestLocationPermission()
                  }.create().show()

        }else{ requestLocationPermission()}
        
    }



    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.clear()
           marker= map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)

            )
            marker?.showInfoWindow()
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            map.clear()
            marker= map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
           marker?.showInfoWindow()
           map.animateCamera(CameraUpdateFactory.newLatLng(poi.latLng))
        }
    }

    //        add style to the map
    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    //        put a marker to location that the user selected
    private fun userLocation(){
        val zoomlevel = 15f
        map.isMyLocationEnabled= true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location:Location?->
              location?.let {
                  val userLatLang= LatLng(location.latitude,location.longitude)
                  //      zoom to the user location after taking his permission
                  map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLang,zoomlevel))
                  marker= map.addMarker(MarkerOptions().position(userLatLang).title(getString(R.string.my_location)))
                  marker?.showInfoWindow()
              }


        }







    }
}

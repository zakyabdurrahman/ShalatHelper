package com.martabak.shalathelper


import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var viewPager : ViewPager2
    lateinit var tabLayout : TabLayout
    lateinit var mFusedLocationClient : FusedLocationProviderClient
    lateinit var locButton : Button
    lateinit var locationText : TextView
    val permissionId = 2
    val tabTexts = listOf<String>("Waktu Solat", "Qiblat")
    var location : Location? = null
    val sharedModel: MainViewModel by viewModels()
    var sharedPref : SharedPreferences? = null
    var lastlatitude : Double? = null
    var lastlongitude : Double? = null
    var city : String = "Jakarta Pusat"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        //global variables here
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPref = getSharedPreferences("lokasi", Context.MODE_PRIVATE)
        var FragmentList = listOf<Fragment>(PrayerTime(), QiblatFragment())

        viewPager = findViewById(R.id.MainPager)
        //very easy to make swipeable fragment do it like this. make fragment list then connect it to
        //fragment state adapter then connect the adapter to tablayout using tablayoutmediator

        locationText = findViewById(R.id.locationText)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return FragmentList[position]
            }

            override fun getItemCount(): Int {
                return 2
            }
        }
        // attach it to tab layout
        tabLayout = findViewById(R.id.tab_layout)
        //"tab" refers to the tab in the UI (has text property)
        TabLayoutMediator(tabLayout, viewPager) {
            tab, pos -> tab.text =  tabTexts[pos]
        }.attach()

        //fetch location saved in sharedprefences
        getInitLocation()
        //bring that data to viewmodel
        sharedModel.updateCoordinate(Coordinate(lastlatitude!!, lastlongitude!!))
        locButton = findViewById(R.id.UpdateLocButton)
        locButton.setOnClickListener {
            if (isLocationEnabled()) {
                Log.d("zaky", "Location is currently ON")
                getLocation()
            } else {
                showLocationError()
            }

        }

    }

    override fun onStart() {
        super.onStart()

    }


    fun getInitLocation() {
        val jakartaLat = -6.17388
        val jakartaLong = 106.83308
        lastlongitude = Double.fromBits(sharedPref!!.getLong("long", jakartaLong.toBits()))
        lastlatitude = Double.fromBits(sharedPref!!.getLong("lat", jakartaLat.toBits()))
        city = sharedPref!!.getString("city", "Jakarta Pusat")!!
        locationText.text = "$city - Indonesia"
        Log.d("zaky", "Retrieved city is $city")
    }

    fun saveLocToSP(latitude: Double, longitude: Double) {
        sharedPref!!.edit().apply {
            putLong("lat", latitude.toBits())
            putLong("long", longitude.toBits())
            putString("city", city)
            apply()
        }

    }


    // function to check if location is enabled public so can be called from fragment
    fun isLocationEnabled() : Boolean {
        val locationManager : LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var networkIsOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && networkIsOn

    }

    //basically permission checking
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                //should be written like this NOT Manifest.permission. unless you import the manifest not permission
                permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
            permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
    return false
    }

    fun showLocationError () {
        Toast.makeText(this@MainActivity, "Please Turn On Both Location and Network", Toast.LENGTH_LONG)
            .show()
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                permission.ACCESS_COARSE_LOCATION,
                permission.ACCESS_FINE_LOCATION
            ),
            //permissionId = free as long bigger than 0, to identify which app request the permission
            permissionId
        )
    }

    private fun getAddress(latitude : Double, longitude : Double) : Address {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: MutableList<Address> =
            geocoder.getFromLocation(latitude, longitude, 1)!!
        return addresses[0]
    }
    
    private fun updateLocation() {
        //update location in sharedpreferences and livedata

    }
    
    private fun getLocation() {
        if(checkPermission()) {
            if(isLocationEnabled()) {
                mFusedLocationClient.lastLocation
                    .addOnSuccessListener { result : Location? ->
                        location = result
                        if (location != null) {
                            var latitude = location!!.latitude
                            var longitude = location!!.longitude
                            updateLocationUI(longitude, latitude)
                            saveLocToSP(latitude, longitude)
                        }
                        else {
                            //when the last location returns null we request location update to start
                            //actively scan for GPS location so we got the location services going on
                            startLocationRequest()
                        }
                    }
            } else {
                showLocationError()
            }
        } else {
            requestPermission()
        }
    }

    private fun updateLocationUI(longitude: Double, latitude: Double) {
        sharedModel.updateCoordinate(Coordinate(latitude, longitude))
        var address = getAddress(latitude, longitude)
        var country : String = address.countryName
        city = address.subAdminArea
        Log.d("zaky", "Kota: $city Negara: $country")
        locationText.text = "$city - $country"
    }

    @SuppressLint("MissingPermission")
    private fun startLocationRequest() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 1000
        mLocationRequest.numUpdates = 2
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLoc : Location = p0.lastLocation!!
            updateLocationUI(lastLoc.longitude, lastLoc.latitude)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //this line of code is so that although I override this method I still use some of it using super.method
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            //chek permission result for this permission ID
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation()
            }
        }
    }

}
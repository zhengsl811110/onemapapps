package com.example.zhengsl.onemapapp.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.layers.ArcGISTiledLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.example.zhengsl.onemapapp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.AppSettingsDialog



class MainActivity : AppCompatActivity(),EasyPermissions.PermissionCallbacks,NavigationView.OnNavigationItemSelectedListener {

    var map: ArcGISMap? = null
    var locationDisplay: LocationDisplay? = null
    var permissions=Manifest.permission.ACCESS_FINE_LOCATION
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        resetExtent.setOnClickListener {
            setViewPointCenter()
        }
        gpsLocation.setOnClickListener {
            gpsLocation()
        }
        init()
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun init() {
        var layer = ArcGISTiledLayer("http://58.210.9.131/arcgis/rest/services/CS/GXQMAP/MapServer")
        val baseMap = Basemap(layer)
        map = ArcGISMap(baseMap)
        mapView.isAttributionTextVisible = false
        mapView.map = map
        map?.addDoneLoadingListener {
            setViewPointCenter()
        }
        //GPS
        locationDisplay = mapView.locationDisplay;
        locationDisplay?.isShowLocation = true;
    }

    private fun setViewPointCenter() {
        val center = Point(36059.0, 46659.0, map?.spatialReference)
        var scale = 36111.909643
        mapView.setViewpointCenterAsync(center, scale)
    }

    private fun gpsLocation() {
        if (checkPermission()) {
            locationDisplay?.autoPanMode = LocationDisplay.AutoPanMode.RECENTER
            locationDisplay?.startAsync()
        } else {
            EasyPermissions.requestPermissions(this, "当前功能需要GPS权限", 100, permissions)
        }
    }

    private fun checkPermission():Boolean {
        var isChecked = false
        if (Build.VERSION.SDK_INT >= 23) {
            isChecked = EasyPermissions.hasPermissions(this, permissions)
        }
        return isChecked
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }



    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        gpsLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            //拒绝授权后，从系统设置了授权后，返回APP进行相应的操作
            gpsLocation();
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.dispose()
    }
}

package org.kpu.taver

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_cafe_map.*
import kotlinx.android.synthetic.main.activity_cafe_map.menuBtn
import kotlinx.android.synthetic.main.activity_cafe_map_list.*
import net.daum.android.map.coord.MapCoordLatLng
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class CafeMapListActivity : AppCompatActivity() {
    var mapView : MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cafe_map_list)

        val intent = intent
        val cafelist : ArrayList<CafeVO> = intent.getSerializableExtra("CafeList") as ArrayList<CafeVO>

        mapView = MapView(this)
        val mapViewContainer = map_view_list as ViewGroup
        mapViewContainer.addView(mapView)

        val locationmanager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled: Boolean = locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean = locationmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var myLatitude : Double = 37.340824
        var myLongitude : Double = 126.731501
        //메니페스트에 권한이 추가되어 있지만 다시 한번 확인
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@CafeMapListActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else{
            when{   //프로바이더 제공자 활성화 여부 체크
                isNetworkEnabled -> {
                    val location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    myLongitude = location?.longitude!!
                    myLatitude = location.latitude
                    try{
                        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1.0f, mLocationListener)
                        locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1.0f, mLocationListener)
                    }catch(e : Exception){

                    }
                }
                isGPSEnabled->{
                    val location = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    myLongitude = location?.longitude!!
                    myLatitude = location.latitude
                    try{
                        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1.0f, mLocationListener)
                        locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 1.0f, mLocationListener)
                    }catch(e : Exception){

                    }
                }
                else -> { }
            }
        }

        val myPoint = MapPoint.mapPointWithGeoCoord(myLatitude, myLongitude)
        val mapCircle  = MapCircle(myPoint, 15, Color.argb(200,255,255,255), Color.argb(200, 255, 0, 0))
        mapCircle.tag = 1
        val mapCircle2  = MapCircle(myPoint, 19, Color.argb(200,255,0,0), Color.argb(0, 255, 255, 255))
        mapCircle.tag = 2
        mapView?.addCircle(mapCircle)
        mapView?.addCircle(mapCircle2)

        for(cafevo in cafelist){
            val map_Point = MapPoint.mapPointWithGeoCoord(cafevo.latitude, cafevo.longitude)
            val marker = MapPOIItem()
            marker.apply{
                itemName = cafevo.cafeName + "\n" + cafevo.peopleNum.toString() + "/" + cafevo.allTable.toString()
                mapPoint = map_Point
                markerType = MapPOIItem.MarkerType.RedPin
            }
            mapView?.setMapCenterPoint(myPoint, true)
            mapView?.addPOIItem(marker)
        }

        menuBtn2.setOnClickListener {
            finish()
        }
    }

    var mLocationListener : LocationListener = object : LocationListener{
        override fun onLocationChanged(location : Location?){
            mapView?.removeAllCircles()

            var latitude = 0.0
            var longitude = 0.0
            if( location != null) {
                latitude = location?.latitude
                longitude = location?.longitude
            }
            val my_Point = MapPoint.mapPointWithGeoCoord(latitude, longitude)
            val mapCircle  = MapCircle(my_Point, 15, Color.argb(200,255,255,255), Color.argb(200, 255, 0, 0))
            mapCircle.tag = 1
            val mapCircle2  = MapCircle(my_Point, 19, Color.argb(200,255,0,0), Color.argb(0, 255, 255, 255))
            mapCircle.tag = 2
            mapView?.addCircle(mapCircle)
            mapView?.addCircle(mapCircle2)

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {

        }

        override fun onProviderDisabled(provider: String?) {

        }
    }

}
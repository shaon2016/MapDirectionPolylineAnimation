package com.lastblade.googlemapdirection

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.errors.ApiException
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException
import java.util.concurrent.TimeUnit


class RouteAnimActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_anim)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val origin = LatLng(22.3660, 91.8288)
        val target1 = LatLng(22.3518, 91.8331)
        val target2 = LatLng(22.3610, 91.8111)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 12f))
        addMarkersToMap(origin)
        addMarkersToMap(target1)
        addMarkersToMap(target2)

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    drawPath(origin, target1, target2)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                }
            }).check()

    }

    private fun addMarkersToMap(ll: LatLng) {
        mMap.addMarker(MarkerOptions().position(ll))
    }

    private fun drawPath(
        origin: LatLng, target1: LatLng, target2: LatLng
    ) {
        val results1 = getDirectionDetails(origin, target1)
        val results2 = getDirectionDetails(origin, target2)

        var mapAnimator = MapAnimator.getInstance()

        results1?.let {
            val decodedPath = PolyUtil.decode(
                results1.routes[0]
                    .overviewPolyline.encodedPath
            )

//            mMap.addPolyline(
//                PolylineOptions()
//                    .addAll(decodedPath)
//            )


            val builder = LatLngBounds.Builder()
            decodedPath.forEach { latlngs ->
                builder.include(latlngs)
            }

            val bounds = builder.build()

            //BOUND_PADDING is an int to specify padding of bound.. try 100.
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, 150)
            mMap.animateCamera(cu)

            mapAnimator?.animateRoute(mMap, decodedPath)
        }

        results2?.let {
            val decodedPath = PolyUtil.decode(
                results2.routes[0]
                    .overviewPolyline.encodedPath
            )

            val builder = LatLngBounds.Builder()
            decodedPath.forEach { latlngs ->
                builder.include(latlngs)
            }

            val bounds = builder.build()

            //BOUND_PADDING is an int to specify padding of bound.. try 100.
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, 100)
            mMap.animateCamera(cu)

            mapAnimator = MapAnimator.getInstance()
            mapAnimator.animateRoute(mMap, decodedPath)
        }
    }




    private fun getDirectionDetails(
        origin: LatLng,
        target: LatLng
    ): DirectionsResult? {
        try {
            return DirectionsApi.newRequest(getGeoApiContext())
                .mode(TravelMode.DRIVING)
                .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(com.google.maps.model.LatLng(target.latitude, target.longitude))
                .await()
        } catch (e: ApiException) {
            e.printStackTrace();
            return null;
        } catch (e: InterruptedException) {
            e.printStackTrace();
            return null;
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    private fun getGeoApiContext(): GeoApiContext? {
        return GeoApiContext()
            .setQueryRateLimit(3)
            .setApiKey(getString(R.string.google_maps_key))
            .setConnectTimeout(1, TimeUnit.SECONDS)
            .setReadTimeout(1, TimeUnit.SECONDS)
            .setWriteTimeout(1, TimeUnit.SECONDS);
    }


}

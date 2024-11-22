package com.example

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import com.example.navigatorapp.R
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

class Util {
    companion object {
        fun getWeatherDate(timestamp: Long, index: Int): String {
            try {
                val calendar = Calendar.getInstance()
                val tz = TimeZone.getDefault()
                calendar.timeInMillis = timestamp * 1000
                calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
                val sdf = SimpleDateFormat("dd-MMM-yyyy hh:a")
                val currenTimeZone = calendar.time as Date
                //return sdf.format(currenTimeZone)

                val delimiter = " "
                val parts = sdf.format(currenTimeZone).split(delimiter)
                val currentTime = parts[1]
                val currentDate = parts[0]
                if (index == 1) {
                    return currentDate
                } else {
                    return currentTime
                }
            } catch (e: Exception) {
            }
            return ""
        }
    }


    class OsmHelper {
        companion object {


            //origional bingkey 1
             const val bingMapApiKey = "Am_mGualKcgksp9Yk358G9Ek0Qr3KOvQSdMucPb4XNUYKCK0TXfb-o1Ulmfx0QfL"



            fun zoomWithAnimate(mController: IMapController, point: GeoPoint, zoom: Int) {
                mController.animateTo(point, zoom.toDouble(), 8000, 0.75F)
            }


            fun bingMapStyle(mMapView: MapView) {
                BingMapTileSource.setBingKey(bingMapApiKey)
                val bing = BingMapTileSource(null)
                bing.style = BingMapTileSource.IMAGERYSET_AERIALWITHLABELS
//            bing.style = BingMapTileSource.IMAGERYSET_AERIAL
                bing.initMetaData()
                mMapView.setTileSource(bing)
            }

            fun setStdTileProvider(context: Context, mMapView: MapView) {
                if (mMapView.tileProvider !is MapTileProviderBasic) {
                    val bitmapProvider = MapTileProviderBasic(context)
                    mMapView.tileProvider = bitmapProvider
                }
            }

            fun setMarkerIconAsPhoto(context: Context, marker: Marker, thumbnail: Bitmap) {
                var thumbnail = thumbnail
                val borderSize = 2
                thumbnail = Bitmap.createScaledBitmap(thumbnail, 60, 60, true)
                val withBorder = Bitmap.createBitmap(
                    thumbnail.width + borderSize * 2,
                    thumbnail.height + borderSize * 2,
                    thumbnail.config
                )
                val canvas = Canvas(withBorder)
                canvas.drawColor(Color.TRANSPARENT)
                canvas.drawBitmap(thumbnail, borderSize.toFloat(), borderSize.toFloat(), null)
                val icon = BitmapDrawable(context.resources, withBorder)
                marker.icon = icon
            }


            fun setMarkerIconAsOnMap(context: Context, marker: Marker, thumbnail: Bitmap) {
                var thumbnail = thumbnail
                val borderSize = 2
                thumbnail = Bitmap.createScaledBitmap(thumbnail, 100, 100, true)
                val withBorder = Bitmap.createBitmap(
                    thumbnail.width + borderSize * 2,
                    thumbnail.height + borderSize * 2,
                    thumbnail.config
                )
                val canvas = Canvas(withBorder)
                canvas.drawColor(Color.TRANSPARENT)
                canvas.drawBitmap(thumbnail, borderSize.toFloat(), borderSize.toFloat(), null)
                val icon = BitmapDrawable(context.resources, withBorder)
                marker.icon = icon
            }


            fun addMarkerOnMapWithDrawable(
                context: Context,
                mOriginPoint: GeoPoint,
                snip: String,
                mapView: MapView
            ) {
                try {
                    val startMarker = Marker(mapView)
                    startMarker.position = mOriginPoint
                    startMarker.title = snip
                    val icon =
                        BitmapFactory.decodeResource(context.resources, R.drawable.p_o_i_marker)
                    startMarker.icon = null
                    setMarkerIconAsPhoto(context, startMarker, icon!!)
                    mapView.overlays.add(startMarker)


                } catch (e: Exception) {
                }
            }

            fun addMarkerOnMapWithCommon(
                context: Context,
                mOriginPoint: GeoPoint,
                mapView: MapView
            ) {
                try {
                    val startMarker = Marker(mapView)
                    startMarker.position = mOriginPoint
                    val icon =
                        BitmapFactory.decodeResource(context.resources, R.drawable.p_o_i_marker)
                    startMarker.icon = null
                    setMarkerIconAsPhoto(context, startMarker, icon!!)
                    mapView.overlays.add(startMarker)


                } catch (e: Exception) {
                }
            }

            fun addMarkerOnMapWithDrawableSatellite(
                context: Context,
                mOriginPoint: GeoPoint,
                snip: String,
                mapView: MapView
            ) {
                try {
                    val startMarker = Marker(mapView)
                    startMarker.position = mOriginPoint
                    startMarker.title = snip
                    val icon =
                        BitmapFactory.decodeResource(context.resources, R.drawable.p_o_i_marker)
                    startMarker.icon = null
                    setMarkerIconAsPhoto(context, startMarker, icon!!)
                    mapView.overlays.add(startMarker)
                } catch (e: Exception) {
                }
            }

            fun addMarkerOnMapBitmapWithName(
                context: Context,
                mOriginPoint: GeoPoint,
                titleName: String,
                bitmap: Bitmap,
                mapView: MapView
            ) {
                try {
                    val startMarker = Marker(mapView)
                    startMarker.position = mOriginPoint
                    startMarker.title = titleName
                    startMarker.icon = null
                    setMarkerIconAsOnMap(context, startMarker, bitmap)
                    mapView.overlays.add(startMarker)
                } catch (e: Exception) {
                }
            }

            fun setMapBoxMapStyleLayer(context: Context): OnlineTileSourceBase {
                var mMabBoxStyle: OnlineTileSourceBase? = null

                mMabBoxStyle = MapBoxTileSource("MapBoxSatelliteLabelled", 1, 19, 256, ".png")
                (mMabBoxStyle as MapBoxTileSource).retrieveAccessToken(context)
                (mMabBoxStyle as MapBoxTileSource).retrieveMapBoxMapId(context)
                return TileSourceFactory.addTileSource(mMabBoxStyle) as OnlineTileSourceBase
            }

            fun hav(x: Double): Double {
                val sinHalf = Math.sin(x * 0.5)
                return sinHalf * sinHalf
            }

            fun arcHav(x: Double): Double {
                return 2.0 * Math.asin(Math.sqrt(x))
            }

            fun havDistance(lat1: Double, lat2: Double, dLng: Double): Double {
                return hav(lat1 - lat2) + hav(dLng) * Math.cos(lat1) * Math.cos(lat2)
            }

            private fun distanceRadians(
                lat1: Double,
                lng1: Double,
                lat2: Double,
                lng2: Double
            ): Double {

                return arcHav(havDistance(lat1, lat2, lng1 - lng2))
            }


            var mapbox_token="pk.eyJ1IjoiYXBwbGUyNyIsImEiOiJjbGJ6M2F1NGkwbjdvM3dwaTBnejB4M3hnIn0._O1l1o3IQrZ-fdXINKc4zg"
            fun getRandomKeyMapBox(): String {
                val mKeyList = ArrayList<String>()
                mKeyList.add("pk.eyJ1IjoiYXBwbGUzMDEiLCJhIjoiY2xjMDFmbml1MDZ2ejNubzRqaTNpMzZtdCJ9.6xmHXDDWPRuO8WZLgsFuKg")
                mKeyList.add("pk.eyJ1IjoiYXBwbGUzMDIiLCJhIjoiY2xjMDFqNDc0MG52MjNxb2NwOHZpeHB6ZCJ9.yCce-sccM_xwLoKWcIFKLg")
                mKeyList.add("pk.eyJ1IjoiYXBwbGUzMDMiLCJhIjoiY2xjMDFtMjN0MHp1ejNvbWR0NTY1emU0MiJ9.Cg6EnwldCTuaZLXfwDT6mQ")
                mKeyList.add("pk.eyJ1IjoiYXBwbGUzMDQiLCJhIjoiY2xjMDFwdXMyMG5wcjNwb2NlN2wyYWx1bCJ9.0Cw1QFLXtLTdJhE1ELfX_w")
                mKeyList.add("pk.eyJ1IjoiYXBwbGUzMDUiLCJhIjoiY2xjMDF0bDNzMDZpODN4cTV0bHRnYmpyciJ9.06U0biHDlq_9zStOyQZNqA")
                mKeyList.add("pk.eyJ1IjoiYXBwbGUzMDYiLCJhIjoiY2xjMDF4M3g1MHc1bjNwcGlkaGYyZGM2ZCJ9.1xisEJ7WXzOtkqXLPVfBHw")
                mKeyList.add("pk.eyJ1IjoiYXBwbGUzMDciLCJhIjoiY2xjMDIweno4MGVqZzNwcDd2Mjc4eHlveCJ9.1QQjjQxcb_--6iwPoVnc7A")
//                mKeyList.add("pk.eyJ1IjoiYXBwbGUzMDgiLCJhIjoiY2xjMDIzc3ZrMHN0YjNxbW5oeWc0NHBqdSJ9.XPzfzp6L9Eq6t0X-QF37dA")

                return mKeyList.random()

            }

            fun computeAngleBetween(from: GeoPoint, to: GeoPoint): Double {
                return distanceRadians(
                    Math.toRadians(from.latitude),
                    Math.toRadians(from.longitude),
                    Math.toRadians(to.latitude),
                    Math.toRadians(to.longitude)
                )
            }

            fun computeDistanceBetween(from: GeoPoint, to: GeoPoint): Double {
                return computeAngleBetween(from, to) * 6371009.0
            }

             fun getIcon(b: String): Int {
                var drw: Int? = null
                when (b) {
                    "01d" -> drw= R.drawable.icon_weather_01d
                    "02d" -> drw= R.drawable.icon_weather_02d
                    "03d" -> drw= R.drawable.icon_weather_03d
                    "04d" -> drw= R.drawable.icon_weather_04d
                    "09d" -> drw= R.drawable.icon_weather_09d
                    "10d" -> drw= R.drawable.icon_weather_10d
                    "11d" -> drw= R.drawable.icon_weather_11d
                    "13d" -> drw= R.drawable.icon_weather_13d
                    "50d" -> drw= R.drawable.icon_weather_50d
                    "01n" -> drw= R.drawable.icon_weather_01n
                    "02n" -> drw= R.drawable.icon_weather_02n
                    "03n" -> drw= R.drawable.icon_weather_03n
                    "04n" -> drw= R.drawable.icon_weather_04n
                    "09n" -> drw= R.drawable.icon_weather_09n
                    "10n" -> drw= R.drawable.icon_weather_10n
                    "11n" -> drw= R.drawable.icon_weather_11n
                    "13n" -> drw= R.drawable.icon_weather_13n
                    "50n" -> drw= R.drawable.icon_weather_50n
                }
                return drw!!
            }
        }
    }
}

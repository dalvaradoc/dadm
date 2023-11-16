package co.edu.unal.reto9

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.core.content.ContextCompat
import org.mapsforge.core.graphics.Bitmap
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Point
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import org.mapsforge.map.view.MapView
import org.mapsforge.poi.storage.PointOfInterest
import java.io.FileInputStream
import kotlin.coroutines.coroutineContext
import android.location.Location
import androidx.core.app.ActivityCompat
import android.Manifest
import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import org.mapsforge.map.android.graphics.AndroidBitmap
import org.mapsforge.map.layer.GroupLayer
import java.io.File
import java.util.function.Consumer


class MainActivity : ComponentActivity() {

    companion object {
        val HUMANAS = LatLong(4.634030, -74.084679)
        val BERLIN = LatLong(52.519125, 13.403525)
        val MONSERRATE = LatLong(4.607262, -74.055176)
        var center = MONSERRATE
        val oneKm = 0.00008116224
        var radius = oneKm
        lateinit var mapView : MapView
    }

    lateinit var button : Button
    lateinit var gpsButton : Button
    lateinit var bubble : Bitmap
    lateinit var seekBar : SeekBar
    
    lateinit var locationManager : LocationManager
//    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var db : SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidGraphicFactory.createInstance(application)

        setContentView(R.layout.map_viewer_layout)

        mapView = findViewById(R.id.mapView)
        button = findViewById(R.id.button)
        gpsButton = findViewById(R.id.gps_btn)
        seekBar = findViewById(R.id.seekBar)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val contract = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result ->
            result?.data?.data?.let{uri ->
                openMap(uri)
            }
        }

        button.setOnClickListener {
            contract.launch(
                Intent(
                    Intent.ACTION_OPEN_DOCUMENT
                ).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
            )
        }

//        var permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
            if (isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                locationManager.getCurrentLocation(
                    LocationManager.GPS_PROVIDER,
                    null,
                    application.mainExecutor,
                    Consumer<Location?> {  }.andThen {
                        if (it != null) {
                            center = LatLong(it.latitude, it.longitude)
                            mapView.setCenter(center)
                            mapView.setZoomLevel(17)
                            radius = oneKm / 2
                            drawMarkers()
                        }
                    }
                )
            }
        }
        
        gpsButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        println("Sorry, api too low")
                    } else {
                        locationManager.getCurrentLocation(
                            LocationManager.GPS_PROVIDER,
                            null,
                            application.mainExecutor,
                           Consumer<Location?> {  }.andThen {
                               if (it != null) {
                                   center = LatLong(it.latitude, it.longitude)
                                   mapView.setCenter(center)
                                   mapView.setZoomLevel(17)
                                   radius = oneKm / 2
                                   drawMarkers()
                               }
                           }
                        )
                    }
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }

        db = SQLiteDatabase.openDatabase(externalMediaDirs[0].absolutePath + "/colombia.poi",null, SQLiteDatabase.CREATE_IF_NECESSARY)

        seekBar.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                radius = oneKm * progress / 2
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                drawMarkers()
            }
        })

    }

    override fun onDestroy() {
        bubble.decrementRefCount()
        mapView.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    @SuppressLint("Range")
    private fun openMap (uri : Uri){
        mapView.mapScaleBar.isVisible = true

        val cache = AndroidUtil.createTileCache(
            this,
            "mycache",
            mapView.model.displayModel.tileSize,
            1f,
            mapView.model.frameBufferModel.overdrawFactor
        )

        val stream = contentResolver.openInputStream(uri) as FileInputStream

        val mapStore = MapFile(stream)

        val renderLayer = TileRendererLayer(
            cache,
            mapStore,
            mapView.model.mapViewPosition,
            AndroidGraphicFactory.INSTANCE
        )

        renderLayer.setXmlRenderTheme(
            InternalRenderTheme.DEFAULT
        )

        mapView.layerManager.layers.add(renderLayer)

        mapView.setCenter(MONSERRATE)
        mapView.setZoomLevel(17)

        // Add bubbles
        val bubbleView = TextView(this)
        Utils.setBackground(
            bubbleView,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) getDrawable(R.drawable.balloon_overlay_unfocused) else resources.getDrawable(
                R.drawable.balloon_overlay_unfocused
            )
        )
        bubbleView.gravity = Gravity.CENTER
        bubbleView.maxEms = 20
        bubbleView.textSize = 15f
        bubbleView.setTextColor(Color.BLACK)
        bubbleView.setText("Humanas")
        bubbleView.setOnClickListener {
            print("LOL")
        }
        bubble = Utils.viewToBitmap(this, bubbleView)
        bubble.incrementRefCount()
        val poi = PointOfInterest(1, HUMANAS.latitude, HUMANAS.longitude, "Test Name", null);
        var marker = MarkerImpl(HUMANAS, bubble, bubble.width/2, bubble.height/2, poi)
        mapView.getLayerManager().getLayers()
            .add(marker)

//        val cursor = db.rawQuery("SELECT * FROM poi_data LEFT JOIN poi_index ON poi_data.id = poi_index.id WHERE poi_index.lat BETWEEN 4.607262 - 0.01 AND 4.607262 + 0.01 AND poi_index.lon BETWEEN -74.055176 - 0.01 AND -74.055176 + 0.01;", null)
        val cursor = db.rawQuery("SELECT * FROM poi_data LEFT JOIN poi_index ON poi_data.id = poi_index.id WHERE ((poi_index.lat - 4.607262)*(poi_index.lat - 4.607262)) + ((poi_index.lon + 74.055176)*(poi_index.lon + 74.055176)) <= 0.00008116224", null)
        var groupLayer = GroupLayer()
        val bubble = AndroidBitmap(BitmapFactory.decodeResource(resources, R.drawable.marker_red))
        if (cursor != null && cursor.moveToFirst()){
            println("Number of rows: " + cursor.count)
            do {
                val cdata = cursor.getString(cursor.getColumnIndex("data"))
                if (!cdata.contains("name=")) continue
                val cid = cursor.getLong(cursor.getColumnIndex("id"))
                val clat = cursor.getDouble(cursor.getColumnIndex("lat"))
                val clon = cursor.getDouble(cursor.getColumnIndex("lon"))
                val poi = PointOfInterest(cid, clat, clon, cdata, null);
                var marker = MarkerImpl(LatLong(clat,clon), bubble, 0, -bubble.height/2, poi)
                groupLayer.layers.add(marker)
            } while (cursor.moveToNext())
            cursor.close()
            mapView.layerManager.layers.add(groupLayer)
            mapView.layerManager.redrawLayers()
        }
    }

    @SuppressLint("Range")
    private fun drawMarkers() {
        if (mapView.layerManager.layers.count() > 1){
            mapView.layerManager.layers.remove(mapView.layerManager.layers.count()-1)
        }
        val query = "SELECT * FROM poi_data LEFT JOIN poi_index ON poi_data.id = poi_index.id WHERE ((poi_index.lat - " + center.latitude.toBigDecimal().toPlainString() + ")*(poi_index.lat - " + center.latitude.toBigDecimal().toPlainString() + ")) + ((poi_index.lon - " + center.longitude.toBigDecimal().toPlainString() + ")*(poi_index.lon - " + center.longitude.toBigDecimal().toPlainString() + ")) <= " + radius.toBigDecimal().toPlainString()
        val cursor = db.rawQuery(query, null)
        var groupLayer = GroupLayer()
        val bubble = AndroidBitmap(BitmapFactory.decodeResource(resources, R.drawable.marker_red))
        if (cursor != null && cursor.moveToFirst()) {
            println("Number of rows: " + cursor.count)
            do {
                val cdata = cursor.getString(cursor.getColumnIndex("data"))
                if (!cdata.contains("name=")) continue
                val cid = cursor.getLong(cursor.getColumnIndex("id"))
                val clat = cursor.getDouble(cursor.getColumnIndex("lat"))
                val clon = cursor.getDouble(cursor.getColumnIndex("lon"))
                val poi = PointOfInterest(cid, clat, clon, cdata, null);
                var marker = MarkerImpl(LatLong(clat, clon), bubble, 0, -bubble.height / 2, poi)
                groupLayer.layers.add(marker)
            } while (cursor.moveToNext())
            cursor.close()
            mapView.layerManager.layers.add(groupLayer)
            mapView.layerManager.redrawLayers()
        }
    }

    private inner class MarkerImpl constructor(
        latLong: LatLong,
        bitmap: Bitmap,
        horizontalOffset: Int,
        verticalOffset: Int,
        pointOfInterest: PointOfInterest
    ) :
        Marker(latLong, bitmap, horizontalOffset, verticalOffset) {
        private val pointOfInterest: PointOfInterest

        init {
            this.pointOfInterest = pointOfInterest
        }

        override fun onTap(tapLatLong: LatLong?, layerXY: Point?, tapXY: Point?): Boolean {
            // GroupLayer does not have a position, layerXY is null
            var layerXY: Point? = layerXY
            layerXY = mapView.mapViewProjection.toPixels(position)
            if (contains(layerXY, tapXY)) {
                Toast.makeText(this@MainActivity, pointOfInterest.name, Toast.LENGTH_SHORT)
                    .show()
                return true
            }
            return false
        }
    }
}
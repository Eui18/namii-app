package com.namii.app.features.map.presentation.components

import androidx.annotation.ColorInt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

private val NamiiOsmHttpsTileSource = XYTileSource(
    "NamiiOSM",
    0,
    19,
    256,
    ".png",
    arrayOf("https://tile.openstreetmap.org/"),
    "© OpenStreetMap contributors"
)

private data class MapOverlayRenderKey(
    val pins: List<MapPinItem>,
    val selectedPinId: Int?,
    val userLocation: Pair<Double, Double>?,
    val isPickerMode: Boolean,
    val pickerLocation: Pair<Double, Double>?,
    @param:ColorInt val pickerColorInt: Int,
    val isInteractive: Boolean
)

/**
 * Componente de mapa compartido con osmdroid.
 *
 * - Conserva CopyrightOverlay intacto (nunca llama view.overlays.clear()).
 * - Usa mosaicos sobre HTTPS (https://tile.openstreetmap.org/).
 * - Separa movimientos explícitos de cámara ([cameraCommand]) de la posición de marcadores.
 * - Solo reconstruye overlays cuando cambia [MapOverlayRenderKey].
 */
@Composable
fun OsmMapView(
    initialLat: Double,
    initialLng: Double,
    modifier: Modifier = Modifier,
    initialZoom: Double = 15.0,
    cameraCommand: MapCameraCommand? = null,
    pins: List<MapPinItem> = emptyList(),
    selectedPinId: Int? = null,
    userLocation: Pair<Double, Double>? = null,
    isInteractive: Boolean = true,
    isPickerMode: Boolean = false,
    pickerLocation: Pair<Double, Double>? = null,
    @ColorInt pickerColorInt: Int = 0xFFB8501A.toInt(),
    onPinClick: (MapPinItem) -> Unit = {},
    onVisibleRegionChanged: (MapVisibleRegion) -> Unit = {},
    onPickerLocationChanged: (Double, Double) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestOnPinClick = rememberUpdatedState(onPinClick)
    val latestOnVisibleRegionChanged = rememberUpdatedState(onVisibleRegionChanged)
    val latestOnPickerLocationChanged = rememberUpdatedState(onPickerLocationChanged)

    val userLocationFolder = remember { FolderOverlay() }
    val pinsFolder = remember { FolderOverlay() }
    val pickerFolder = remember { FolderOverlay() }
    val lastRenderKey = remember { mutableStateOf<MapOverlayRenderKey?>(null) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(NamiiOsmHttpsTileSource)
            setMultiTouchControls(isInteractive)
            isClickable = isInteractive
            isFocusable = isInteractive
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(initialZoom)
            controller.setCenter(GeoPoint(initialLat, initialLng))

            // Agregamos capas dedicadas encima de CopyrightOverlay sin borrarlo nunca.
            overlays.add(userLocationFolder)
            overlays.add(pinsFolder)
            overlays.add(pickerFolder)

            if (isPickerMode && isInteractive) {
                val tapReceiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        if (p != null) {
                            latestOnPickerLocationChanged.value(p.latitude, p.longitude)
                            return true
                        }
                        return false
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean = false
                }
                overlays.add(0, MapEventsOverlay(tapReceiver))
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView, isInteractive, isPickerMode) {
        val mapListener = if (isInteractive && !isPickerMode) {
            DelayedMapListener(
                object : MapListener {
                    override fun onScroll(event: ScrollEvent?): Boolean {
                        emitBoundsIfReady(mapView, latestOnVisibleRegionChanged.value)
                        return false
                    }

                    override fun onZoom(event: ZoomEvent?): Boolean {
                        emitBoundsIfReady(mapView, latestOnVisibleRegionChanged.value)
                        return false
                    }
                },
                450L
            )
        } else {
            null
        }

        if (mapListener != null) {
            mapView.addMapListener(mapListener)
        }

        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            if (mapListener != null) {
                mapView.removeMapListener(mapListener)
            }
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            mapView.onDetach()
        }
    }

    LaunchedEffect(cameraCommand?.requestId) {
        val cmd = cameraCommand ?: return@LaunchedEffect
        val targetPoint = GeoPoint(cmd.latitude, cmd.longitude)
        if (cmd.animate) {
            mapView.controller.animateTo(targetPoint, cmd.zoomLevel, 650L)
        } else {
            mapView.controller.setZoom(cmd.zoomLevel)
            mapView.controller.setCenter(targetPoint)
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            val currentKey = MapOverlayRenderKey(
                pins = pins,
                selectedPinId = selectedPinId,
                userLocation = userLocation,
                isPickerMode = isPickerMode,
                pickerLocation = pickerLocation,
                pickerColorInt = pickerColorInt,
                isInteractive = isInteractive
            )
            if (currentKey == lastRenderKey.value) {
                return@AndroidView
            }
            lastRenderKey.value = currentKey

            userLocationFolder.items.clear()
            userLocation?.let { (uLat, uLng) ->
                val userMarker = Marker(view).apply {
                    position = GeoPoint(uLat, uLng)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    infoWindow = null
                    icon = MapPinHelper.createUserLocationDrawable(context)
                }
                userLocationFolder.add(userMarker)
            }

            pinsFolder.items.clear()
            if (!isPickerMode) {
                pins.forEach { pin ->
                    val marker = Marker(view).apply {
                        position = GeoPoint(pin.latitude, pin.longitude)
                        title = pin.title
                        infoWindow = null
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = MapPinHelper.createCategoryPinDrawable(
                            context = context,
                            categoryColor = pin.categoryColorInt,
                            isSelected = pin.id == selectedPinId
                        )
                        if (isInteractive) {
                            setOnMarkerClickListener { _, _ ->
                                latestOnPinClick.value(pin)
                                true
                            }
                        }
                    }
                    pinsFolder.add(marker)
                }
            }

            pickerFolder.items.clear()
            if (isPickerMode && pickerLocation != null) {
                val (pLat, pLng) = pickerLocation
                val pickerMarker = Marker(view).apply {
                    position = GeoPoint(pLat, pLng)
                    infoWindow = null
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = MapPinHelper.createSellerPickerDrawable(context, pickerColorInt)
                    isDraggable = isInteractive
                    if (isInteractive) {
                        setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                            override fun onMarkerDrag(marker: Marker?) = Unit

                            override fun onMarkerDragStart(marker: Marker?) = Unit

                            override fun onMarkerDragEnd(marker: Marker?) {
                                val pos = marker?.position ?: return
                                latestOnPickerLocationChanged.value(pos.latitude, pos.longitude)
                            }
                        })
                    }
                }
                pickerFolder.add(pickerMarker)
            }

            view.invalidate()
        }
    )
}

private fun emitBoundsIfReady(
    mapView: MapView,
    callback: (MapVisibleRegion) -> Unit
) {
    if (mapView.width <= 0 || mapView.height <= 0) return
    val box = mapView.boundingBox
    val region = MapVisibleRegion(
        north = box.latNorth,
        south = box.latSouth,
        east = box.lonEast,
        west = box.lonWest
    )
    if (region.isValid()) {
        callback(region)
    }
}

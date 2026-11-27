package com.namii.app.core.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.namii.app.ui.theme.BorderLight
import com.namii.app.ui.theme.DuraznoClear
import com.namii.app.ui.theme.ErrorRed
import com.namii.app.ui.theme.InputBackground
import com.namii.app.ui.theme.NaranjaTostado
import com.namii.app.ui.theme.PureWhite
import com.namii.app.ui.theme.SellerCacaoInk
import com.namii.app.ui.theme.SellerSoftBrown
import java.io.File

private enum class PendingPhotoSource {
    CAMERA,
    GALLERY
}

/**
 * Controlador reutilizable de Cámara + Galería con Vista Previa obligatoria antes de confirmar.
 */
@Composable
fun rememberPhotoPickerController(
    onPhotoConfirmed: (localPath: String) -> Unit,
    onPermissionDenied: (String) -> Unit
): PhotoPickerController {
    val context = LocalContext.current
    var showSourceSheet by remember { mutableStateOf(false) }
    var canRemoveOption by remember { mutableStateOf(false) }
    var onRemoveAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    var pendingCameraPath by remember { mutableStateOf<String?>(null) }
    var pendingPreviewPath by remember { mutableStateOf<String?>(null) }
    var pendingPreviewSource by remember { mutableStateOf(PendingPhotoSource.GALLERY) }
    var previewTitle by remember { mutableStateOf("Vista previa de foto") }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val path = pendingCameraPath
        if (success && path != null && File(path).length() > 0L) {
            pendingPreviewSource = PendingPhotoSource.CAMERA
            pendingPreviewPath = path
        }
    }

    fun launchCameraInternal() {
        val outputFile = createPhotoCacheFile(context, "cam")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outputFile
        )
        pendingCameraPath = outputFile.absolutePath
        takePictureLauncher.launch(uri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCameraInternal()
        } else {
            onPermissionDenied("Activa el permiso de cámara para tomar fotos")
        }
    }

    val pickGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val copied = copyUriToCacheFile(context, uri)
            if (copied != null) {
                pendingPreviewSource = PendingPhotoSource.GALLERY
                pendingPreviewPath = copied
            } else {
                onPermissionDenied("No se pudo leer la imagen seleccionada")
            }
        }
    }

    fun startCamera() {
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasCameraPermission) {
            launchCameraInternal()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun startGallery() {
        pickGalleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    if (showSourceSheet) {
        PhotoSourceBottomSheet(
            showRemoveOption = canRemoveOption,
            onDismiss = { showSourceSheet = false },
            onTakePhoto = {
                showSourceSheet = false
                startCamera()
            },
            onPickFromGallery = {
                showSourceSheet = false
                startGallery()
            },
            onRemovePhoto = {
                showSourceSheet = false
                onRemoveAction?.invoke()
            }
        )
    }

    pendingPreviewPath?.let { previewPath ->
        MandatoryPhotoPreviewDialog(
            title = previewTitle,
            localPhotoPath = previewPath,
            source = pendingPreviewSource,
            onConfirmUsePhoto = {
                pendingPreviewPath = null
                onPhotoConfirmed(previewPath)
            },
            onRetakeCamera = {
                pendingPreviewPath = null
                startCamera()
            },
            onChooseAnotherGallery = {
                pendingPreviewPath = null
                startGallery()
            },
            onCancel = {
                pendingPreviewPath = null
            }
        )
    }

    return remember {
        PhotoPickerController(
            openSourcePicker = { title, allowRemove, onRemove ->
                previewTitle = title
                canRemoveOption = allowRemove
                onRemoveAction = onRemove
                showSourceSheet = true
            }
        )
    }
}

class PhotoPickerController(
    private val openSourcePicker: (title: String, allowRemove: Boolean, onRemove: (() -> Unit)?) -> Unit
) {
    fun launch(
        title: String = "Vista previa de foto",
        allowRemove: Boolean = false,
        onRemove: (() -> Unit)? = null
    ) {
        openSourcePicker(title, allowRemove, onRemove)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoSourceBottomSheet(
    showRemoveOption: Boolean,
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickFromGallery: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 26.dp)
        ) {
            Text(
                text = "Elegir fotografía",
                style = MaterialTheme.typography.titleMedium,
                color = SellerCacaoInk
            )
            Spacer(modifier = Modifier.height(14.dp))

            SourceOptionTile(
                icon = Icons.Rounded.CameraAlt,
                title = "Tomar foto con cámara",
                subtitle = "Captura una foto al momento",
                onClick = onTakePhoto
            )

            Spacer(modifier = Modifier.height(10.dp))

            SourceOptionTile(
                icon = Icons.Rounded.PhotoLibrary,
                title = "Seleccionar de la galería",
                subtitle = "Elige una imagen guardada en tu dispositivo",
                onClick = onPickFromGallery
            )

            if (showRemoveOption) {
                Spacer(modifier = Modifier.height(10.dp))
                SourceOptionTile(
                    icon = Icons.Rounded.DeleteOutline,
                    title = "Quitar foto actual",
                    subtitle = "Tu negocio conservará la imagen genérica de categoría",
                    tint = ErrorRed,
                    onClick = onRemovePhoto
                )
            }
        }
    }
}

@Composable
private fun SourceOptionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: androidx.compose.ui.graphics.Color = NaranjaTostado,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = InputBackground,
        border = BorderStroke(1.dp, BorderLight),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DuraznoClear,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (tint == ErrorRed) ErrorRed else SellerCacaoInk
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = SellerSoftBrown
                )
            }
        }
    }
}

@Composable
private fun MandatoryPhotoPreviewDialog(
    title: String,
    localPhotoPath: String,
    source: PendingPhotoSource,
    onConfirmUsePhoto: () -> Unit,
    onRetakeCamera: () -> Unit,
    onChooseAnotherGallery: () -> Unit,
    onCancel: () -> Unit
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = SellerCacaoInk
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Revisa cómo se verá antes de aplicarla.",
                    fontSize = 12.sp,
                    color = SellerSoftBrown
                )
                Spacer(modifier = Modifier.height(14.dp))

                AsyncImage(
                    model = File(localPhotoPath),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(InputBackground)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onConfirmUsePhoto,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NaranjaTostado,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        text = "Usar esta foto",
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = if (source == PendingPhotoSource.CAMERA) {
                            onRetakeCamera
                        } else {
                            onChooseAnotherGallery
                        },
                        border = BorderStroke(1.dp, BorderLight),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (source == PendingPhotoSource.CAMERA) {
                                "Tomar de nuevo"
                            } else {
                                "Elegir otra imagen"
                            },
                            color = SellerCacaoInk,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    TextButton(onClick = onCancel) {
                        Text(
                            text = "Cancelar",
                            color = SellerSoftBrown,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

private fun createPhotoCacheFile(context: Context, prefix: String): File {
    val dir = File(context.cacheDir, "namii_photos").apply { mkdirs() }
    return File(dir, "${prefix}_${System.currentTimeMillis()}.jpg")
}

private fun copyUriToCacheFile(context: Context, uri: Uri): String? {
    return runCatching {
        val target = createPhotoCacheFile(context, "gal")
        context.contentResolver.openInputStream(uri)?.use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        target.takeIf { it.exists() && it.length() > 0L }?.absolutePath
    }.getOrNull()
}

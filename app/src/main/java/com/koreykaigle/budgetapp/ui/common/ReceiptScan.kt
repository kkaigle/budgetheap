package com.koreykaigle.budgetapp.ui.common

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.koreykaigle.budgetapp.util.ReceiptScanner
import com.koreykaigle.budgetapp.util.ScanResult
import kotlinx.coroutines.launch
import java.io.File

/**
 * Hands back a `() -> Unit` that, when invoked, prompts for camera permission if
 * needed, opens the system camera, runs the photo through on-device OCR, and
 * immediately deletes the photo -- it is never written anywhere persistent
 * (no gallery, no MediaStore entry, no database row) and never leaves the device.
 * [onResult] receives whatever text/amount could be guessed so a screen can
 * pre-fill its add/edit form; nothing is ever saved automatically.
 */
@Composable
fun rememberReceiptScanner(onResult: (ScanResult) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pendingFile by remember { mutableStateOf<File?>(null) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = pendingUri
        val file = pendingFile
        if (success && uri != null) {
            scope.launch {
                val result = ReceiptScanner.scan(context, uri)
                file?.delete()
                if (result.rawText.isBlank()) {
                    Toast.makeText(
                        context,
                        "Couldn't read any text from that photo -- you can still fill this in by hand.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                onResult(result)
            }
        } else {
            file?.delete()
        }
        pendingFile = null
        pendingUri = null
    }

    fun launchCamera() {
        val dir = File(context.cacheDir, "scans").apply { mkdirs() }
        val file = File(dir, "scan_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        pendingFile = file
        pendingUri = uri
        takePicture.launch(uri)
    }

    val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            launchCamera()
        } else {
            Toast.makeText(context, "Camera permission is needed to scan.", Toast.LENGTH_SHORT).show()
        }
    }

    return {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            launchCamera()
        } else {
            requestPermission.launch(Manifest.permission.CAMERA)
        }
    }
}

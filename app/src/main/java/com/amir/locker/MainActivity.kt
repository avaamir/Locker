package com.amir.locker

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.behraz.fastermixer.batch.utils.general.PermissionHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), PermissionHelper.Interactions {


    private val rootDir by lazy {
        val rootPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        File("$rootPath/a")
    }

    private companion object {
        const val REQ_GO_TO_SETTINGS_PERMISSION = 1223
    }

    private val permissionHelper: PermissionHelper by lazy {
        PermissionHelper(
            arrayListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), this, this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionHelper.checkPermission()

        initViews()


    }

    private fun initViews() {
        btnAdd.setOnClickListener {
            lock(rootDir)
            //findAllSubDirectories(rootDir)
        }

        btnRemove.setOnClickListener {
            unLock(rootDir)
        }
    }

    private fun findAllSubDirectories(rootDir: File) {
        if (rootDir.isDirectory) {
            val directories = ArrayList<File>()
            val files = LinkedList<File>()

            directories.add(rootDir)

            var i = 0

            while (i < directories.size) {
                directories[i].listFiles()?.forEach { file ->
                    if (file.isDirectory) {
                        println("debug:dir:${file.absolutePath}")
                        directories.add( file)
                    } else {
                        files.add(file)
                    }
                }
                i++
            }


                println("debug:dir:${directories.size}")

            println("debug:=============================================================")
            /* files.forEach {
                 println("debug:file:${it.absolutePath}")
             }*/


        } else {
            throw IllegalStateException("rootDir should be directory")
        }
    }

    private fun lock(rootFile: File) {
        if (rootFile.exists()) {
            rootFile.listFiles()?.forEach {
                val index = it.absolutePath.indexOf(".lck", it.absolutePath.length - 5)
                if (index == -1) { //if not already locked
                    it.renameTo(File("${it.absolutePath}.lck"))
                }
            }
        } else {
            Toast.makeText(this, "Path not exists", Toast.LENGTH_SHORT).show()
        }
    }

    private fun unLock(rootFile: File) {
        if (rootFile.exists()) {
            rootFile.listFiles()?.forEach {
                val index = it.absolutePath.indexOf(".lck", it.absolutePath.length - 5)
                if (index != -1) { //if Locked
                    it.renameTo(File(it.absolutePath.substring(0, index)))
                }
            }
        }
    }

    private fun encryptName() {
        TODO("not yet implemented")
    }


    private fun decryptName() {
        TODO("not yet implemented")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionHelper.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_GO_TO_SETTINGS_PERMISSION) {
            permissionHelper.checkPermission()
        }
    }

    override fun beforeRequestPermissionsDialogMessage(
        notGrantedPermissions: ArrayList<String>,
        permissionRequesterFunction: () -> Unit
    ) {
        permissionRequesterFunction.invoke()
    }

    override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
        Toast.makeText(this, "Permissions not Granted", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDeniedWithNeverAskAgain(permission: String) {
        Toast.makeText(this, "Permissions Needed", Toast.LENGTH_SHORT).show()
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            addCategory(Intent.CATEGORY_DEFAULT)
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }
        startActivityForResult(intent, REQ_GO_TO_SETTINGS_PERMISSION)
    }

    override fun onPermissionsGranted() {
        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
    }
}
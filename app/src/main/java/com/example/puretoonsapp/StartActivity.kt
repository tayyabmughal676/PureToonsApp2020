package com.example.puretoonsapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import com.google.android.material.bottomnavigation.BottomNavigationView

open class StartActivity : AppCompatActivity() {

    lateinit var mWebView: WebView
    lateinit var mBottomNavigationOption: BottomNavigationView
    lateinit var mProgressBar: ProgressBar

    //    Array List
    lateinit var listItems: Array<String>
    lateinit var categoryList: Array<String>

    //    Request code
    companion object {
        private const val MY_PERMISSION_REQUEST_CODE = 123
        const val myUrl = "https://puretoons.me/"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

//      Initialized
        mWebView = findViewById(R.id.layoutWebView)
        mBottomNavigationOption = findViewById(R.id.layoutBottomNavigationView)
        mProgressBar = findViewById(R.id.layoutProgressBar)
        mProgressBar.max = 100

//        List Item
        listItems = resources.getStringArray(R.array.DownloadOption)

        mBottomNavigationOption.setOnNavigationItemSelectedListener(
            mOnNavigationItemSelectedListener
        )
//       Open Website Fun:)
        mOpenMyWeb(myUrl)
//        Download

        mWebView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->

            if (mWebView.url?.contains(".3gp")!! || mWebView.url!!.contains(".mp4")) {
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse(url)
//                startActivity(i)
//                var  videoPath : String = mWebView.url
//                var videoPath = URLUtil.guessUrl()

                val videoPath = URLUtil.guessFileName(url, contentDisposition, mimetype)

                val mBuilder = AlertDialog.Builder(this)
                mBuilder.setTitle("Choose An Item")
                mBuilder.setSingleChoiceItems(
                    listItems,
                    -1,
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        // mResult.setText(listItems[i])

                        if (listItems[i] == "Watch Online") {
                            Toast.makeText(this, "Your Choice is Online", Toast.LENGTH_SHORT).show()

                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            startActivity(i)

//                        val intent = Intent(applicationContext, VideoActivity::class.java)
//                        intent.putExtra("videoPath", videoPath)
//                        startActivity(intent)

                        } else if (listItems[i] == "Download") {
                            Toast.makeText(this, "Your Choice is download", Toast.LENGTH_SHORT)
                                .show()

                            val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
                            //returns a string of the name of the file THE IMPORTANT PART

                            val myRequest = DownloadManager.Request(Uri.parse(url))
                            myRequest.allowScanningByMediaScanner()

                            myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            myRequest.setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                fileName
                            )
                            val myManager =
                                getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                            myManager.enqueue(myRequest)
                            myDownloadingAlert(fileName)
                            Toast.makeText(
                                this,
                                "Your file is Downloading... ${fileName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        dialogInterface.dismiss()
                    })
                val mDialog = mBuilder.create()
                mDialog.show()
            }
        }

    }

    private fun linkPassing() {

        val CartoonSeriesHindiDubbed =
            " http://puretoons.com/site_cartoon_series_hindi_dubbed.xhtml"

        val HindiCartoonSeries = "https://puretoons.me/hindi-cartoon-series/"
        val HindiCartoonsAnimationMovies =
            "https://puretoons.me/all-new-hindi-cartoons-animation-movies/"
        val AnimeSeries = "https://puretoons.me/anime-series/"
        val EnglishCartoonSeries = "https://puretoons.me/english-cartoon-series/"
        categoryList = resources.getStringArray(R.array.CartoonsCatergories)

        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Choose a Category")
        mBuilder.setSingleChoiceItems(
            categoryList,
            -1,
            DialogInterface.OnClickListener { dialogInterface, i ->
                // mResult.setText(listItems[i])
                when {
                    categoryList[i] == "Cartoon Series Hindi Dubbed" -> {
                        mOpenMyWeb(HindiCartoonSeries)
                    }
                    categoryList[i] == "Cartoon Series English" -> {
                        mOpenMyWeb(EnglishCartoonSeries)
                    }
                    categoryList[i] == "Cartoon/Animated Movies" -> {
                        mOpenMyWeb(HindiCartoonsAnimationMovies)
                    }
                    categoryList[i] == "Special Anime Cartoons" -> {
                        mOpenMyWeb(AnimeSeries)
                    }
//                    categoryList[i] == "Anime Cartoon Special Episode" -> {
//                        mOpenMyWeb(AnimeCartoonSpecialEpisode)
//                    }
//                    categoryList[i] == "Fan Hindi Dubbed Videos" -> {
//                        mOpenMyWeb(FanHindiDubbedVideos)
//                    }
//                    categoryList[i] == "Anime Cartoon Songs" -> {
//                        mOpenMyWeb(AnimeCartoonSongs)
//                    }
//                    categoryList[i] == "Cartoon Series Bangla Dubbed" -> {
//                        mOpenMyWeb(CartoonSeriesBanglaDubbed)
//                    }
                }
                dialogInterface.dismiss()
            })
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    private fun myDownloadingAlert(filename: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setMessage("Your Downloading is start ${filename}")
            .setCancelable(false)
            //.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id -> finish() })
            .setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun mOpenMyWeb(mWebURL: String) {
        mWebView.loadUrl(mWebURL)
        mWebView.settings.javaScriptEnabled = true
        mWebView.settings.loadWithOverviewMode = true
        mWebView.settings.useWideViewPort = true
        mWebView.settings.domStorageEnabled = true
        mWebView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        mWebView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        mWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        mWebView.settings.databaseEnabled = true
        mWebView.settings.setSupportMultipleWindows(false)

        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                mProgressBar.visibility = View.VISIBLE;
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                mProgressBar.visibility = View.VISIBLE;
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                mProgressBar.visibility = View.GONE;
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
            }
        }



        this.mProgressBar.progress = 0

        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                mProgressBar.progress = newProgress
            }

            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                supportActionBar!!.title = title
            }

            override fun onReceivedIcon(view: WebView, icon: Bitmap) {
                super.onReceivedIcon(view, icon)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        checkConnectivity()
        checkPermission()
    }

    override fun onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private fun onForwardPressed() {
        if (mWebView.canGoForward()) {
            mWebView.goForward()
        } else {
            Toast.makeText(this, "Can't go further !", Toast.LENGTH_SHORT).show()
        }
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    Toast.makeText(applicationContext, "Home ", Toast.LENGTH_SHORT).show()
                    val myHome = "https://puretoons.me/"
                    mOpenMyWeb(myHome)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_back -> {
                    Toast.makeText(applicationContext, "Press Back ", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_forward -> {
                    onForwardPressed()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_refresh -> {
                    Toast.makeText(applicationContext, "Press Refresh ", Toast.LENGTH_SHORT).show()
                    mWebView.reload()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_option -> {
                    Toast.makeText(applicationContext, "categories ", Toast.LENGTH_SHORT).show()
//                  mWebView.reload()
                    linkPassing()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkConnectivity(): Int {
        var enabled = true
        var internet = 2
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
//        val info = connectivityManager.activeNetwork

        if (info == null || !info.isConnected || !info.isAvailable) {
            internet = 0//sin conexion

            Toast.makeText(
                applicationContext,
                "Internet connection is not available...",
                Toast.LENGTH_SHORT
            ).show()

            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.app_name)
            builder.setIcon(R.mipmap.ic_launcher)
            builder.setMessage("Internet connection is not available ")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes",
                    DialogInterface.OnClickListener { dialog, id -> finish() })
                .setNegativeButton(
                    "Cancel",
                    DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
            val alert = builder.create()
            alert.show()

            enabled = false

        } else {
            internet = 1//conexión
            Toast.makeText(applicationContext, "Internet is available...", Toast.LENGTH_SHORT)
                .show()
        }
        return internet
    }

//    fun internetCheck(context: Context): Boolean {
//        var available = false
//        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//        if (connectivity != null) {
//            val networkInfo = connectivity.allNetworkInfo
//            if (networkInfo != null) {
//                for (i in networkInfo.indices) {
//                    if (networkInfo[i].state == NetworkInfo.State.CONNECTED) {
//                        available = true
//                        break
//                    }
//                }
//            }
//        }
//        return available
//    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // show an alert dialog
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Write external storage permission is required.")
                    builder.setTitle("Please grant permission")
                    builder.setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MY_PERMISSION_REQUEST_CODE
                        )
                    }
                    builder.setNeutralButton("Cancel", null)
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                // Permission already granted
                Toast.makeText(applicationContext, "Permission already Granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Permission denied
                    Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}
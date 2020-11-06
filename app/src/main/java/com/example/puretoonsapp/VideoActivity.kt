package com.example.puretoonsapp

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.MediaController
import android.widget.VideoView

class VideoActivity : AppCompatActivity() {

    private lateinit var mVideoPlayer: VideoView
    private lateinit var mPlayer: MediaPlayer
    private lateinit var mMediaController: MediaController
    private lateinit var pDialog: ProgressDialog

    private lateinit var mDownloadEditTextLink: EditText
    private lateinit var mDownloadBtn : Button

    companion object {
        const val mDemoVideoLink =
            "https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_480_1_5MG.mp4"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        mVideoPlayer = findViewById(R.id.layoutVideoView)
        mMediaController = MediaController(this)

        mDownloadBtn = findViewById(R.id.layoutDownloadBtn)
        mDownloadEditTextLink = findViewById(R.id.layoutEditText)


        mDownloadBtn.setOnClickListener {
            startDownloadVideo()
        }


        pDialog = ProgressDialog(this)
//        // Set progressbar title
        pDialog.setTitle("Video playing")
        // Set progressbar message
        pDialog.setMessage("Buffering...")
        pDialog.isIndeterminate = false
        pDialog.setCancelable(false)
        // Show progressbar
        pDialog.show()


        try {
            // Start the MediaController
            val mediacontroller = MediaController(this)
            mediacontroller.setAnchorView(mVideoPlayer)
            // Get the URL from String VideoURL
//            val video = Uri.parse(videolink)
            val mDemoVideoUri = Uri.parse(mDemoVideoLink)
            mVideoPlayer.setMediaController(mediacontroller)
            mVideoPlayer.setVideoURI(mDemoVideoUri)

        } catch (e: Exception) {
            e.message?.let { Log.e("Error", it) }
            e.printStackTrace()
        }

        mVideoPlayer.requestFocus()
        mVideoPlayer.setOnPreparedListener {
            pDialog.dismiss()
            mVideoPlayer.start()
        }
    }

    private fun startDownloadVideo() {
        val link = mDownloadEditTextLink.text.toString()
        val mLink = "https://file-examples-com.github.io/uploads/2018/04/file_example_AVI_1280_1_5MG.avi"
        val request = DownloadManager.Request(Uri.parse(mLink))

        request.apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            setTitle("Download")
            setDescription("File Downloading...")
            allowScanningByMediaScanner()
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${System.currentTimeMillis()}")
        }

//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
//        request.setTitle("Download")
//        request.setDescription("File Downloading...")
//        request.allowScanningByMediaScanner()
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${System.currentTimeMillis()}")

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }
}
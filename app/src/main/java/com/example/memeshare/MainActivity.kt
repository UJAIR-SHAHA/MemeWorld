package com.example.memeshare

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File

class MainActivity : AppCompatActivity() {
    var myDownloadId : Long = 0
    var currentImageUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadMeme()
    }

   private fun loadMeme(){
       // Instantiate the RequestQueue.
       val progressBar = findViewById<ProgressBar>(R.id.progressBar)
       progressBar.visibility = View.VISIBLE

       val url = "https://meme-api.com/gimme"

       // Request a string response from the provided URL.
       val JsonObjectRequest = JsonObjectRequest(
           Request.Method.GET, url, null,
           { response ->
               currentImageUrl = response.getString("url")


               val memeImage = findViewById<ImageView>(R.id.memeImage)
               Glide.with(this,).load(currentImageUrl).listener(object :
                   RequestListener<Drawable> {
                   override fun onLoadFailed(
                       e: GlideException?,
                       model: Any?,
                       target: Target<Drawable>?,
                       isFirstResource: Boolean
                   ): Boolean {
                       progressBar.visibility = View.GONE
                       return false
                   }

                   override fun onResourceReady(
                       resource: Drawable?,
                       model: Any?,
                       target: Target<Drawable>?,
                       dataSource: DataSource?,
                       isFirstResource: Boolean
                   ): Boolean {
                       progressBar.visibility = View.GONE
                       return false
                   }

               }).into(memeImage);

           },
           {
               Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()



           })

       // Add the request to the RequestQueue.
       MySingleton.getInstance(this).addToRequestQueue(JsonObjectRequest)
   }

    fun shareMeme(view: View) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "Hey check this cool meme i got from Reddit $currentImageUrl")
        val chooser = Intent.createChooser(intent, "Share this meme Using....")
        startActivity(chooser)


    }
    fun nextMeme(view: View) {
        loadMeme()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.about,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.about -> Toast.makeText(this, "This app is made by Uzair",Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item)
    }

    fun download(view: View) {
        Toast.makeText(applicationContext,"Image Downloading",Toast.LENGTH_SHORT).show()


        var request = DownloadManager.Request(
            Uri.parse(currentImageUrl))
            .setTitle("Meme World")
            .setDescription("Image Downloading")

            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
        var dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request).also { myDownloadId = it }

        Toast.makeText(applicationContext,"Image Downloaded",Toast.LENGTH_SHORT).show()


    }
    var br =object:BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            var id:Long? = p1?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)
            if(id == myDownloadId){

            }
        }
    }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?): Intent? {
        return super.registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }

}


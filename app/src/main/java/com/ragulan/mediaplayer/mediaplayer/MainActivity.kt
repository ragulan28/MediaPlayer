package com.ragulan.mediaplayer.mediaplayer

import android.content.pm.PackageManager

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.song_ticket.view.*

class MainActivity : AppCompatActivity() {

    var listSong = ArrayList<SongInfo>()
    var adapter: MySongAdapter? = null
    var mp: MediaPlayer? = null
    private val REQUEST_CODE_ASK_PERMISSION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //loadURLOnLine()
        checkUserPension()


        val myTracking = mySongTrack()
        myTracking.start()
    }

    fun loadURLOnLine() {
        listSong.add(SongInfo("aa1" , "r1" , "http://server6.mp3quran.net/thubti/001.mp3"))
        listSong.add(SongInfo("aa2" , "r2" , "http://server6.mp3quran.net/thubti/002.mp3"))
        listSong.add(SongInfo("aa3" , "r3" , "http://server6.mp3quran.net/thubti/003.mp3"))
        listSong.add(SongInfo("aa4" , "r4" , "http://server6.mp3quran.net/thubti/004.mp3"))
        listSong.add(SongInfo("aa5" , "r5" , "http://server6.mp3quran.net/thubti/005.mp3"))

    }

    inner class MySongAdapter(private var myListView: ArrayList<SongInfo>) : BaseAdapter() {

        override fun getView(position: Int , convertView: View? , parent: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.song_ticket , null)
            val song = this.myListView[position]
            myView.tvName.text = song.Title
            myView.tvAuthor.text = song.AuthorName
            myView.btnPlay.setOnClickListener(View.OnClickListener {

                if (myView.btnPlay.text.equals("Stop")) {
                    mp!!.stop()
                    myView.btnPlay.text = "Start"
                } else {
                    mp = MediaPlayer()
                    try {
                        mp!!.setDataSource(song.SongURL)
                        mp!!.prepare()
                        mp!!.start()
                        myView.btnPlay.text = "Stop"
                        sbProgress.max = mp!!.duration
                    } catch (e: Exception) {
                    }
                }
            })
            return myView
        }

        override fun getItem(position: Int): Any {
            return this.myListView[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return this.myListView.size
        }

    }

    inner class mySongTrack : Thread() {
        override fun run() {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {

                }
                runOnUiThread {
                    if (mp != null) {
                        sbProgress.progress = mp!!.currentPosition
                    }
                }
            }
        }

    }

    fun checkUserPension() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this , android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE) , REQUEST_CODE_ASK_PERMISSION)
                return
            }
        }
        loadSong()
    }

    override fun onRequestPermissionsResult(requestCode: Int , permissions: Array<out String> , grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSong()
            } else {
                Toast.makeText(this , "Error" , Toast.LENGTH_SHORT).show()
            }
            else                        -> super.onRequestPermissionsResult(requestCode , permissions , grantResults)
        }
    }

    fun loadSong() {
        val allSongURL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = contentResolver.query(allSongURL , null , selection , null , null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val songURL = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songAuthor = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    listSong.add(SongInfo(songName , songAuthor , songURL))
                } while (cursor.moveToNext())
                cursor.close()
                adapter = MySongAdapter(listSong)
                lvListSong.adapter = adapter
            }
        }
    }


}

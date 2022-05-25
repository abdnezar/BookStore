package com.androidcamp.bookstore

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.androidcamp.bookstore.databinding.ExoActivityVideoBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

class ExoVideoActivity : AppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ExoActivityVideoBinding
    private lateinit var vUrl: Uri
    private lateinit var sep: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ExoActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fullScreenCall()

        vUrl = Uri.parse(intent.getStringExtra("vUrl")!!)
        Log.e(TAG, "vUrl : vUrl")
    }

    override fun onResume() {
        super.onResume()

        sep = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        val factory = DefaultHttpDataSourceFactory("exoplayer_video")
        val ef = DefaultExtractorsFactory()
        val ms = ExtractorMediaSource(vUrl, factory, ef, null, null)

        binding.playerView.player = sep
        binding.playerView.keepScreenOn = true
        sep.apply {
            prepare(ms)
            playWhenReady = true
            addListener(object : Player.EventListener {
                override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                }

                override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?, ) {
                }

                override fun onLoadingChanged(isLoading: Boolean) {
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            binding.pd.visibility = View.VISIBLE
                        }
                        Player.STATE_IDLE -> {
                            binding.pd.visibility = View.GONE
                            sep.release()
                            finish()
                        }
                        Player.STATE_ENDED -> {
                            binding.pd.visibility = View.GONE
                            Toast.makeText(applicationContext, "Finished", Toast.LENGTH_SHORT).show()
                            sep.release()
                            finish()
                        }
                        else -> {
                            binding.pd.visibility = View.GONE
                        }
                    }
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                }

                override fun onPlayerError(error: ExoPlaybackException?) {
                    Toast.makeText(applicationContext, "Can't Play Video", Toast.LENGTH_SHORT).show()
                    finish()
                }

                override fun onPositionDiscontinuity(reason: Int) {
                }

                override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                }

                override fun onSeekProcessed() {
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        sep.playWhenReady = false
        sep.stop(false)
        sep.playbackState
    }

    override fun onRestart() {
        super.onRestart()
        sep.playWhenReady = true
        sep.playbackState
    }

    override fun onDestroy() {
        super.onDestroy()
        sep.playWhenReady = false
        sep.stop()
    }

    private fun fullScreenCall() {
        if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView: View = window.decorView
            val uiOptions: Int = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.setSystemUiVisibility(uiOptions)
        }
    }
}
package com.rsschool.myapplication.mediaplayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.rsschool.myapplication.mediaplayer.R
import com.rsschool.myapplication.mediaplayer.databinding.FragmentPlayerBinding
import com.rsschool.myapplication.mediaplayer.ext.isPlaying
import com.rsschool.myapplication.mediaplayer.model.AudioItem
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val playerViewModel: AudioItemViewModel by viewModels()
    private var curPlayingSong: AudioItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        binding.buttons.playPauseButton.setOnClickListener {
            curPlayingSong?.let {
                playerViewModel.playOrToggleSong(it, true)
            }
        }

        binding.buttons.skipPrevious.setOnClickListener {
            playerViewModel.skipToPreviousSong()
        }
        binding.buttons.skipNext.setOnClickListener {
            playerViewModel.skipToNextSong()
        }
        binding.buttons.fastForward.setOnClickListener {
            playerViewModel.fastForward()
        }
        binding.buttons.fastRewind.setOnClickListener {
            playerViewModel.rewind()
        }
    }

    private fun subscribeToObservers() {
        playerViewModel.audioList.observe(viewLifecycleOwner) { songs ->
            if (curPlayingSong == null && songs.isNotEmpty()) {
                curPlayingSong = songs[0]
                updateImage(curPlayingSong)
                updateTitle(curPlayingSong)
            }
        }

        playerViewModel.curPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            val itSong = AudioItem(
                id = it.description.mediaId ?: "",
                title = it.description.title.toString(),
                artist = it.description.subtitle.toString(),
                trackUri = it.description.mediaUri.toString(),
                bitmapUri = it.description.iconUri.toString()
            )
            curPlayingSong = itSong
            updateImage(curPlayingSong)
            updateTitle(curPlayingSong)
        }

        playerViewModel.playbackStateCompat.observe(viewLifecycleOwner) {
            binding.buttons.playPauseButton.setImageResource(
                if (it?.isPlaying == true)
                    R.drawable.ic_baseline_pause_24
                else R.drawable.ic_baseline_play_arrow_24
            )
        }

        playerViewModel.isConnected.observe(viewLifecycleOwner) {
            if (!it) {
                Snackbar.make(
                    binding.imageCard,
                    "An unknown error occurred",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        playerViewModel.networkError.observe(viewLifecycleOwner) {
            if (!it) {
                Snackbar.make(
                    binding.imageCard,
                    "An unknown network occurred",
                    Snackbar.LENGTH_LONG
                ).show()

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateTitle(audioItem: AudioItem?) {
        binding.artist.text = audioItem?.artist
        binding.song.text = audioItem?.title
    }

    private fun updateImage(audioItem: AudioItem?) {
        binding.apply {
            Glide.with(this@PlayerFragment).load(audioItem?.bitmapUri).centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imageCard)
        }
    }
}

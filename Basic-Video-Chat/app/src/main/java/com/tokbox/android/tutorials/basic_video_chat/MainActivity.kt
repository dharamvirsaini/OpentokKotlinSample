package com.tokbox.android.tutorials.basic_video_chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.tokbox.android.tutorials.basicvideochat.R
import com.tokbox.android.tutorials.basicvideochat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //val mainViewModel: MainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }
}
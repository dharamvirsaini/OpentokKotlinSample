package com.tokbox.android.tutorials.basic_video_chat

import android.Manifest
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tokbox.android.tutorials.basic_video_chat.utilities.API_KEY
import com.tokbox.android.tutorials.basic_video_chat.utilities.SESSION_ID
import com.tokbox.android.tutorials.basic_video_chat.utilities.TOKEN
import com.opentok.android.*
import com.tokbox.android.tutorials.basicvideochat.R
import com.tokbox.android.tutorials.basicvideochat.databinding.FragmentMainBinding
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainFragment: Fragment(), EasyPermissions.PermissionCallbacks, Session.SessionListener, PublisherKit.PublisherListener, SubscriberKit.SubscriberListener {

    private lateinit var mPublisher: Publisher
    private var mSubscriber: Subscriber? = null
    private lateinit var mSession: Session
    private lateinit var mBinding: FragmentMainBinding
    private val LOG_TAG = "MainFragment"
    private val RC_SETTINGS_SCREEN_PERM = 123
    private val RC_VIDEO_APP_PERM = 124

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentMainBinding.inflate(inflater, container, false)
        requestPermissions()

        return mBinding.root
    }

    @AfterPermissionGranted(124)
    private fun requestPermissions(): Unit {
        val perms = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        if (EasyPermissions.hasPermissions(activity, *perms)) {
            initializeSession(API_KEY, SESSION_ID, TOKEN)
        } else {
            EasyPermissions.requestPermissions(this, getString(com.tokbox.android.tutorials.basicvideochat.R.string.rationale_video_app), RC_VIDEO_APP_PERM, *perms)
        }
    }

    private fun initializeSession(api_key: String, session_id: String, token: String): Unit {
        mSession = Session.Builder(activity, api_key, session_id).build()
        mSession.setSessionListener(this)
        mSession.connect(token)
    }

    override fun onStreamDropped(session: Session?, stream: Stream?) {
        Log.d(
            LOG_TAG,
            "onStreamDropped: Stream Dropped: " + stream!!.streamId + " in session: " + session!!.sessionId
        )

        if(mSubscriber != null) {
            mSubscriber = null
            mBinding.subscriberContainer.removeAllViews()
        }
    }

    override fun onStreamReceived(session: Session?, stream: Stream?) {
        Log.d(
            LOG_TAG,
            "onStreamReceived: New Stream Received " + stream!!.streamId + " in session: " + session!!.sessionId
        )
        if(mSubscriber == null) {
            mSubscriber = Subscriber.Builder(activity, stream).build().apply { renderer.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL) }
            mSubscriber?.setSubscriberListener(this)

            mSession.subscribe(mSubscriber)
            mBinding.subscriberContainer.addView(mSubscriber?.view)
        }

    }

    override fun onConnected(session: Session?) {

        Log.d(
            LOG_TAG,
            "onConnected: Connected to session: " + session!!.sessionId
        )

        mPublisher = Publisher.Builder(activity).build().apply { renderer.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
            BaseVideoRenderer.STYLE_VIDEO_FILL) }
        mPublisher.setPublisherListener(this)

        mBinding.publisherContainer.addView(mPublisher.view)
        if(mPublisher.view is GLSurfaceView) {
            (mPublisher.view as GLSurfaceView).setZOrderOnTop(true)
        }

        session?.publish(mPublisher)

    }

    override fun onDisconnected(session: Session?) {
        Log.d(
            LOG_TAG,
            "onDisconnected: Disconnected from session: " + session!!.sessionId
        )
    }

    override fun onError(sesison: Session?, error: OpentokError?) {

    }

    override fun onConnected(p0: SubscriberKit?) {

    }

    override fun onDisconnected(p0: SubscriberKit?) {

    }

    override fun onError(p0: SubscriberKit?, p1: OpentokError?) {

    }

    override fun onStreamCreated(p0: PublisherKit?, p1: Stream?) {

    }

    override fun onStreamDestroyed(p0: PublisherKit?, p1: Stream?) {

    }

    override fun onError(p0: PublisherKit?, p1: OpentokError?) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {

        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms!!)) {
            AppSettingsDialog.Builder(this).apply { setTitle(getString(com.tokbox.android.tutorials.basicvideochat.R.string.title_settings_dialog))
            setRationale(getString(com.tokbox.android.tutorials.basicvideochat.R.string.rationale_video_app))
            setPositiveButton(getString(com.tokbox.android.tutorials.basicvideochat.R.string.setting))
            setNegativeButton(getString(R.string.cancel))
            setRequestCode(RC_SETTINGS_SCREEN_PERM)}.build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {

        Log.d(
            LOG_TAG,
            "onPermissionsGranted:" + requestCode + ":" + perms!!.size
        )

    }
}
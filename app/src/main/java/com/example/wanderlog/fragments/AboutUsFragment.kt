package com.example.wanderlog.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.wanderlog.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AboutUsFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var googleIcon: ImageView
    private lateinit var facebookIcon: ImageView
    private lateinit var twitterIcon: ImageView
    private val facebookUrl = "https://www.facebook.com/denardemanuel.laza"
    private val googleUrl = "https://www.beatravelbuddy.com/"
    private val twitterUrl = "https://twitter.com/TiurbeSerban21"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_about_us, container, false)
        initViews(view)
        setGoogleIconOnClickListener()
        setFacebookIconOnClickListener()
        setTwitterIconOnClickListener()
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutUsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun initViews(view: View) {
        googleIcon = view.findViewById(R.id.iv_google_aboutus)
        facebookIcon = view.findViewById(R.id.iv_facebook_aboutus)
        twitterIcon = view.findViewById(R.id.iv_twitter_aboutus)
    }

    private fun setGoogleIconOnClickListener() {
        googleIcon.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(googleUrl)
            startActivity(intent)
        }
    }

    private fun setFacebookIconOnClickListener() {
        facebookIcon.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(facebookUrl)
            startActivity(intent)
        }
    }

    private fun setTwitterIconOnClickListener() {
        twitterIcon.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(twitterUrl)
            startActivity(intent)
        }
    }
}
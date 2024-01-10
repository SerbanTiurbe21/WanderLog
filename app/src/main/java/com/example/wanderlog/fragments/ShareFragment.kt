package com.example.wanderlog.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.wanderlog.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ShareFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var shareButton: ImageView

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
        val view: View = inflater.inflate(R.layout.fragment_share, container, false)
        initViews(view)
        setShareButtonOnClickListener()
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ShareFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun initViews(view: View) {
        shareButton = view.findViewById(R.id.iv_shareButton)
    }

    private fun setShareButtonOnClickListener() {
        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "WanderLog")
            intent.putExtra(Intent.EXTRA_TEXT, "WanderLog is a travel app that allows you to share your travel experiences with your friends and family. Download it now!")
            startActivity(Intent.createChooser(intent, "Share using"))
        }
    }
}
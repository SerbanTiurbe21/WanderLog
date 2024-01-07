package com.example.wanderlog.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.wanderlog.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ContactFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sendMessageButton: ImageView
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var problemTextContact: EditText
    private lateinit var adapterItem: ArrayAdapter<String>

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
        val view: View = inflater.inflate(R.layout.fragment_contact, container, false)
        initViews(view)
        setSendMessageButtonOnClickListener()
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun initViews(view: View) {
        sendMessageButton = view.findViewById(R.id.sendMessageButton)
        autoCompleteTextView = view.findViewById(R.id.autoComplete)
        problemTextContact = view.findViewById(R.id.problemTextContact)
    }

    private fun setSendMessageButtonOnClickListener() {
        val myList = resources.getStringArray(R.array.stringsList)
        adapterItem = ArrayAdapter(requireContext(), R.layout.dropdown_item, myList)
        autoCompleteTextView.setAdapter(adapterItem)
        sendMessageButton.setOnClickListener {
            val problemText = problemTextContact.text.toString()
            val problemType = autoCompleteTextView.text.toString()
            if (validateProblemText(problemText) && validateProblemType(problemType)) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("travelbuddy@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, autoCompleteTextView.text.toString())
                intent.putExtra(Intent.EXTRA_TEXT, problemTextContact.text.toString())
                intent.type = "message/rfc822"
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "There is no application to support this action!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(), "Please fill in all the fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateProblemText(problemText: String): Boolean {
        return problemText.isNotEmpty()
    }

    private fun validateProblemType(problemType: String): Boolean {
        return problemType.isNotEmpty() && problemType != "Option"
    }
}
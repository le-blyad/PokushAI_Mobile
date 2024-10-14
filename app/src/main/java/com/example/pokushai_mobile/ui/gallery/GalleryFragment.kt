package com.example.pokushai_mobile.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pokushai_mobile.R
import com.example.pokushai_mobile.*

class GalleryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)



        val imageButtons = mutableListOf<ImageButton>()
        for (i in 0..49) {
            val imageButtonId = resources.getIdentifier("imageButton$i", "id", requireContext().packageName)
            val imageButton = view.findViewById<ImageButton>(imageButtonId)
            imageButtons.add(imageButton)
        }

        val textViews = mutableListOf<TextView>()
        for (i in 0..49) {
            val textViewId = resources.getIdentifier("textView$i", "id", requireContext().packageName)
            val textView = view.findViewById<TextView>(textViewId)
            textViews.add(textView)
        }


        imageButtons[0].setOnClickListener {
            val intent = Intent(requireContext(), Belyashi::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        imageButtons[10].setOnClickListener {
            val intent = Intent(requireContext(), cutlets::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        return view
    }
}

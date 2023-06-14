package com.martabak.shalathelper

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import org.w3c.dom.Text


class QiblatFragment : Fragment() {
    lateinit var latText : TextView
    lateinit var longText : TextView
    private val sharedModel : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qiblat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        latText = view.findViewById(R.id.latitudeText)
        longText = view.findViewById(R.id.longitudeText)
        sharedModel.liveCoordinate.observe(viewLifecycleOwner, Observer { coor ->
            latText.text = "Latitude : ${coor.latitude.toString()}"
            longText.text = "Longitude : ${coor.longitude.toString()}"
            Log.d("zaky", "Fragment Data Change observed")
        })
    }

    override fun onResume() {
        super.onResume()
        sharedModel.liveCoordinate.observe(viewLifecycleOwner, Observer { coor ->
            latText.text = "Latitude : ${coor.latitude.toString()}"
            longText.text = "Longitude : ${coor.longitude.toString()}"
        })
    }


}
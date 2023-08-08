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



class QiblatFragment : Fragment() {
    lateinit var latText : TextView
    lateinit var longText : TextView
    private var compass : Compass? = null
    //this need elaboration, class initiation delegation is quite complicated, but this is the way to do it
    //from fragment inside an activity
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
        var activity = activity
        compass = Compass(activity!!)
        compass!!.arrowView = view.findViewById(R.id.arrowImage)

        //this bind the textViews values to the live state of the variable
        sharedModel.liveCoordinate.observe(viewLifecycleOwner, Observer { coor ->
            compass!!.coordinate = coor
            latText.text = "Latitude : ${coor.latitude.toString()}"
            longText.text = "Longitude : ${coor.longitude.toString()}"
            Log.d("zaky", "Fragment Data Change observed")
        })
    }

    override fun onResume() {
        super.onResume()
        compass!!.start()
        sharedModel.liveCoordinate.observe(viewLifecycleOwner, Observer { coor ->
            latText.text = "Latitude : ${coor.latitude.toString()}"
            longText.text = "Longitude : ${coor.longitude.toString()}"
        })
    }

    override fun onPause() {
        super.onPause()
        compass!!.stop()
    }

    //TODO : make a function that take long and lat, convert it qiblat azimuth
}
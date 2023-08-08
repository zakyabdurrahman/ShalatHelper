package com.martabak.shalathelper

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import java.util.*


class PrayerTime : Fragment() {
    private val sharedModel : MainViewModel by activityViewModels()
    lateinit var SubuhText : TextView
    lateinit var DzuhurText : TextView
    lateinit var AsharText : TextView
    lateinit var MaghribText : TextView
    lateinit var IsyaText : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prayer_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initiate all view variables
        SubuhText = view.findViewById(R.id.Subuh)
        DzuhurText = view.findViewById(R.id.Dzuhur)
        AsharText = view.findViewById(R.id.Ashar)
        MaghribText = view.findViewById(R.id.Maghrib)
        IsyaText = view.findViewById(R.id.Isya)

        //this is for fetching prayertime object
        val today = SimpleDate(GregorianCalendar())

        sharedModel.liveCoordinate.observe(viewLifecycleOwner, Observer { coor ->
            //everytime coor value changed this get executed
            val location = Location(coor.latitude, coor.longitude, 7.0, 0)
            val azan = Azan(location, Method.MUSLIM_LEAGUE)
            val prayerTimes = azan.getPrayerTimes(today)
            // update the UI
            SubuhText.text = "Subuh ${prayerTimes.fajr()}"
            DzuhurText.text = "Dzuhur ${prayerTimes.thuhr()}"
            AsharText.text = "Ashar ${prayerTimes.assr()}"
            MaghribText.text = "Maghrib ${prayerTimes.maghrib()}"
            IsyaText.text = "Isya ${prayerTimes.ishaa()}"
        })
    }

}
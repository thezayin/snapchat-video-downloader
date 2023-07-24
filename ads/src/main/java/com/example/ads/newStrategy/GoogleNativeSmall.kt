package com.example.ads.newStrategy

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.nativead.NativeAd
import timber.log.Timber
import java.util.Arrays
import java.util.Stack

class GoogleNativeSmall(context: Context?) {
    private val totalLevels = 4
    private var adUnits: ArrayList<ArrayList<Any>>? = null
    private val native5 = "ca-app-pub-9507635869843997/8809111197"
    private val native4 = "ca-app-pub-9507635869843997/4267230957"
    private val nativeHigh = "ca-app-pub-9507635869843997/9129826771"
    private val nativeMed = "ca-app-pub-9507635869843997/3182531373"
    private val nativeAll = "ca-app-pub-9507635869843997/5190581766"

    init {
        instantiateList()
        loadnativead(context)
    }

    private fun instantiateList() {
        adUnits = ArrayList()
        adUnits!!.add(0, ArrayList(listOf(native5, Stack<NativeAd>())))
        adUnits!!.add(1, ArrayList(listOf(native4, Stack<NativeAd>())))
        adUnits!!.add(2, ArrayList(listOf(nativeHigh, Stack<NativeAd>())))
        adUnits!!.add(3, ArrayList(listOf(nativeMed, Stack<NativeAd>())))
        adUnits!!.add(4, ArrayList(listOf(nativeAll, Stack<NativeAd>())))
    }

    fun loadnativead(context: Context?) {
        NativeAdLoad(context, totalLevels)
    }

    fun getDefaultAd(activity: Context?): NativeAd? {
        if (false) { // Check Premium here
            return null
        }
        val levels = totalLevels
        for (i in levels downTo 0) {
            val list = adUnits!![i]
            val adunitid = list[0] as String
            val stack = list[1] as Stack<NativeAd>
            NativeAdLoadSpecific(activity, adunitid, stack)
            if (stack == null) {
            } else if (stack.isEmpty()) {
            } else {
                return stack.pop()
            }
        }
        return null
    }

    fun NativeAdLoad(activity: Context?, level: Int) {
        if (level < 0) {
            return
        }
        if (adUnits!!.size < level) {
            Timber.tag("ERROR").e("Size is less than ad Units size")
        }
        val list = adUnits!![level]
        val adunitid = list[0] as String
        val stack = list[1] as Stack<NativeAd>
        val adLoader = AdLoader.Builder(activity, adunitid)
            .forNativeAd { ad -> stack.push(ad) }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.tag("ADS_INFO")
                        .e("--- NO --- Ad Failed to Load of level $level With ad Id $adunitid")
                    NativeAdLoad(activity, level - 1)
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun NativeAdLoadSpecific(activity: Context?, adUnitId: String?, stack: Stack<NativeAd>?) {
        val adLoader = AdLoader.Builder(activity, adUnitId)
            .forNativeAd { ad -> stack!!.push(ad) }.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}
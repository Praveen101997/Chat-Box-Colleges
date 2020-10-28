@file: JvmName("modelprediction")
@file:JvmMultifileClass
package com.example.chatterbox

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

object StaticData {
    var result = false
}


class modelprediction : CoroutineScope {

    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    private val labels =
            listOf("Toxic", "Severe Toxic", "Obscene", "Threat", "Insult", "Identity Hate")
    private val thresholds = listOf(0.70, 0.30, 0.30, 0.15, 0.40, 0.20)

    private var classifier:Classifier

    public var finalres = false

    constructor(message: String,applicationContext: Context){
        mJob = Job()
        classifier = Classifier(applicationContext)
    }



    fun finalCheck(message: String,applicationContext: Context):Boolean{
        mJob = Job()
        classifier = Classifier(applicationContext)
        classifier.init()
        val text = message.toLowerCase(Locale.ROOT).trim()
        val resArr = classifier.classifyText(text)

        Log.d("Checkpp","before : "+finalres);
        return updateResultUI(resArr,applicationContext);
    }



    private fun updateResultUI(res: FloatArray,context: Context) :Boolean {
        var isAboveThreshold = false

        val sortedRes = res.sorted().reversed()
        val labelList = mutableListOf<String>()
        val thresholdList = mutableListOf<Double>()
        for (i in 0..2) {
            val index = res.indexOf(sortedRes[i])
            labelList.add(labels[index])
            thresholdList.add(thresholds[index])
        }

        for (i in 0..2) {
            Log.d("Checkpp","qq1 : "+sortedRes[i]+"");
            if (sortedRes[i] > thresholdList[i]) {
                Log.d("Checkpp","IN IF qq1 : "+sortedRes[i]+"");

                isAboveThreshold = true
                break
            }
        }

        val percentSortedRes = sortedRes.map { it * 100 }

        finalres = isAboveThreshold

        Log.d("Checkpp","Inning : "+"Finalres : "+finalres+" ISAbove :"+isAboveThreshold)


        if (isAboveThreshold) {
            var profpercent = ""
            for (i in 0..2) {
                profpercent+=labelList[i]
                profpercent+=" : "
                profpercent+=percentSortedRes[i].toString()+" %"
                profpercent+="\n"
            }
            Toast.makeText(context,"Profanity Detected \n"+profpercent,Toast.LENGTH_SHORT).show()
        }

        StaticData.result = isAboveThreshold
        return isAboveThreshold
    }

}
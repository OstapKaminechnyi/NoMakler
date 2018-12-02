package com.openclassrooms.nomakler

import com.openclassrooms.nomakler.utils.Utils
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*


class UtilsTest {

    /** Tests the $/€ conversion */
    @Test
    fun testConvertDollarsToEuro(){
        assert(Utils.convertDollarToEuro(0.0) == 0.0)
        val rndVal = Random().nextDouble() * 100
        assert(Utils.convertDollarToEuro(rndVal) == rndVal * Utils.dollarEuro)
    }

    /** Tests the value of today's date */
    @Test
    fun testGetTodayDate(){
        assert(Utils.getTodayDate() == SimpleDateFormat(Utils.dateFormat).format(Date()))
    }

}
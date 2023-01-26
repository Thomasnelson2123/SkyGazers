package com.example.skygazers

import java.util.*

// how to use:
// first, initialize SunPosition class. Give it the users latitutde, longitude, and timezone
// use UTC timezone. Example: Bellingham is -8 UTC, so for bellingham we would pass it -8

// then there are 3 functions you can call:
// calculateSunPosition, getSunrise, and getSunset
// all require the same arguments: pass in integer values representing the year, month, day, hour, and minute
// calculateSunPosition returns a doublearray: first item is the elevation, second is the azimuth
// getSunrise and getSunset both return an intarray; first item is the hour, second is the minute
class SunPosition(private val lat: Double, private val lon: Double, private val tz: Int) {
    private var jd = 0.0
    private var jc = 0.0
    private var gmls = 0.0
    private var moe = 0.0
    private var eeo = 0.0
    private var oc = 0.0
    private var vy = 0.0
    private var gmas = 0.0
    private var seoc = 0.0
    private var stl = 0.0
    private var sal = 0.0
    private var eot = 0.0
    private var tst = 0.0
    private var sd = 0.0
    private var ha = 0.0
    private var sza = 0.0
    private var sea = 0.0
    private var aar = 0.0
    private var elevation = 0.0
    private var azimuth = 0.0
    private fun calculateCommon(year: Int, month: Int, day: Int, hour: Int, minute: Int, tz: Int) {
        jd = julianDate(year, month, day, hour, minute, tz.toDouble())
        jc = julianCentury(jd)
        moe = meanObliqEliptic(jc)
        gmls = geomMeanLongSun(jc)
        gmas = geomMeanAnomSun(jc)
        eeo = eccentEarthOrbit(jc)
        oc = obliqCor(moe, jc)
        seoc = sunEqOfCtr(gmas, jc)
        stl = sunTrueLong(gmls, seoc)
        sal = sunAppLong(stl, jc)
        sd = sunDeclination(oc, sal)
        vy = varY(oc)
        eot = eqOfTime(vy, gmls, eeo, gmas)
    }

    fun calculateSunPosition(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int
    ): DoubleArray {
        calculateCommon(year, month, day, hour, minute, tz)
        tst = trueSolarTime((hour * 60 + minute) / 1440.0, eot, lon, tz.toDouble())
        ha = hourAngle(tst)
        sza = solarZenithAngle(lat, sd, ha)
        sea = solarElevationAngle(sza)
        aar = approx_atmo_refraction(sea)
        elevation = solarElevationAngleWithRefraction(aar, sea)
        azimuth = azimuth(lat, ha, sza, sd)
        return doubleArrayOf(elevation, azimuth)
    }

    fun getSunrise(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int
    ): IntArray {
        calculateCommon(year, month, day, hour, minute, tz)
        val sn = solarNoon(eot)
        val has = haSunrise(sd)
        val sr = sunrise(sn, has)
        return convertToTime(sr)
    }

    fun getSunset(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int
    ): IntArray {
        calculateCommon(year, month, day, hour, minute, tz)
        val sn = solarNoon(eot)
        val has = haSunrise(sd)
        val ss = sunset(sn, has)
        return convertToTime(ss)
    }

    private fun julianDate(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        tz: Double
    ): Double {
        val c = Calendar.getInstance()
        c[year, month, day, hour] = minute
        var jd = (c.timeInMillis / 1000 / 3600 / 24 + 25538).toDouble()
        jd += 2415018.5
        jd += (hour + minute / 60.0) / 24
        jd -= tz / 24
        return jd
    }

    private fun julianCentury(jd: Double): Double {
        return (jd - 2451545) / 36525.0
    }

    private fun geomMeanLongSun(jc: Double): Double {
        return (280.46646 + jc * (36000.76983 + jc * 0.0003032)) % 360
    }

    private fun meanObliqEliptic(jc: Double): Double {
        return 23 + (26 + (21.448 - jc * (46.815 + jc * (0.00059 - jc * 0.001813))) / 60) / 60
    }

    private fun eccentEarthOrbit(jc: Double): Double {
        return 0.016708634 - jc * (0.000042037 + 0.0000001267 * jc)
    }

    private fun geomMeanAnomSun(jc: Double): Double {
        return 357.52911 + jc * (35999.05029 - 0.0001537 * jc)
    }

    private fun obliqCor(moe: Double, jc: Double): Double {
        return moe + 0.00256 * Math.cos(Math.toRadians(125.04 - 1934.136 * jc))
    }

    private fun varY(oc: Double): Double {
        return Math.tan(Math.toRadians(oc / 2)) * Math.tan(Math.toRadians(oc / 2))
    }

    private fun sunEqOfCtr(gmas: Double, jc: Double): Double {
        return Math.sin(Math.toRadians(gmas)) * (1.914602 - jc * (0.004817 + 0.000014 * jc)) + Math.sin(
            Math.toRadians(2 * gmas)
        ) * (0.019993 - 0.000101 * jc) + Math.sin(Math.toRadians(3 * gmas)) * 0.000289
    }

    private fun sunTrueLong(gmls: Double, seoc: Double): Double {
        return gmls + seoc
    }

    private fun sunAppLong(stl: Double, jc: Double): Double {
        return stl - 0.00569 - 0.00478 * Math.sin(Math.toRadians(125.04 - 1934.136 * jc))
    }

    private fun eqOfTime(vy: Double, gmls: Double, eeo: Double, gmas: Double): Double {
        return 4 * Math.toDegrees(
            vy * Math.sin(2 * Math.toRadians(gmls)) - 2 * eeo * Math.sin(Math.toRadians(gmas)) + 4 * eeo * vy * Math.sin(
                Math.toRadians(gmas)
            ) * Math.cos(2 * Math.toRadians(gmls)) - 0.5 * vy * vy * Math.sin(
                4 * Math.toRadians(
                    gmls
                )
            ) - 1.25 * eeo * eeo * Math.sin(2 * Math.toRadians(gmas))
        )
    }

    private fun trueSolarTime(time: Double, eot: Double, lon: Double, tz: Double): Double {
        return (time * 1440 + eot + 4 * lon - 60 * tz) % 1440
    }

    private fun sunDeclination(oc: Double, sal: Double): Double {
        return Math.toDegrees(Math.asin(Math.sin(Math.toRadians(oc)) * Math.sin(Math.toRadians(sal))))
    }

    private fun hourAngle(tst: Double): Double {
        return if (tst / 4 < 0) {
            tst / 4 + 180
        } else {
            tst / 4 - 180
        }
    }

    private fun solarZenithAngle(lat: Double, sd: Double, ha: Double): Double {
        return Math.toDegrees(
            Math.acos(
                Math.sin(Math.toRadians(lat)) * Math.sin(Math.toRadians(sd)) + Math.cos(
                    Math.toRadians(lat)
                ) * Math.cos(Math.toRadians(sd)) * Math.cos(Math.toRadians(ha))
            )
        )
    }

    private fun solarElevationAngle(sza: Double): Double {
        return 90 - sza
    }

    private fun approx_atmo_refraction(sea: Double): Double {
        if (sea > 85) {
            return 0.0
        }
        if (sea > 5) {
            return (58.1 / Math.tan(Math.toRadians(sea)) - 0.07 / Math.pow(
                Math.tan(
                    Math.toRadians(
                        sea
                    )
                ), 3.0
            ) + 0.000086 / Math.pow(
                Math.tan(Math.toRadians(sea)), 5.0
            )) / 3600
        }
        return if (sea > -0.575) {
            (1735 + sea * (-518.2 + sea * (103.4 + sea * (-12.79 + sea * 0.711)))) / 3600
        } else {
            -20.772 / Math.tan(Math.toRadians(sea)) / 3600
        }
    }

    private fun solarElevationAngleWithRefraction(aar: Double, sea: Double): Double {
        return aar + sea
    }

    private fun azimuth(lat: Double, ha: Double, sza: Double, sd: Double): Double {
        return if (ha > 0) {
            (Math.toDegrees(
                Math.acos(
                    (Math.sin(
                        Math.toRadians(
                            lat
                        )
                    ) * Math.cos(Math.toRadians(sza)) - Math.sin(
                        Math.toRadians(
                            sd
                        )
                    )) / (Math.cos(Math.toRadians(lat)) * Math.sin(
                        Math.toRadians(
                            sza
                        )
                    ))
                )
            ) + 180) % 360
        } else {
            (540 - Math.toDegrees(
                Math.acos(
                    (Math.sin(
                        Math.toRadians(
                            lat
                        )
                    ) * Math.cos(Math.toRadians(sza)) - Math.sin(
                        Math.toRadians(
                            sd
                        )
                    )) / (Math.cos(Math.toRadians(lat)) * Math.sin(
                        Math.toRadians(
                            sza
                        )
                    ))
                )
            )) % 360
        }
    }

    private fun haSunrise(sd: Double): Double {
        return Math.toDegrees(
            Math.acos(
                Math.cos(Math.toRadians(90.833)) / (Math.cos(
                    Math.toRadians(
                        lat
                    )
                ) * Math.cos(Math.toRadians(sd))) - Math.tan(
                    Math.toRadians(
                        lat
                    )
                ) * Math.tan(Math.toRadians(sd))
            )
        )
    }

    private fun solarNoon(eot: Double): Double {
        return (720 - 4 * lon - eot + tz * 60) / 1440
    }

    private fun sunrise(sn: Double, has: Double): Double {
        return sn - has * 4 / 1440
    }

    private fun sunset(sn: Double, has: Double): Double {
        return sn + has * 4 / 1440
    }

    private fun convertToTime(t: Double): IntArray {
        var t = t
        t = t * 24
        val hr = t.toInt()
        val min = ((t - hr) * 60).toInt()
        return intArrayOf(hr, min)
    }
}
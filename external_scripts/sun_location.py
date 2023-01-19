import math as m
import datetime
import pandas as pd
import sys
 
def sun_declination(oc, sal):
    return m.degrees(m.asin(m.sin(m.radians(oc)) * m.sin(m.radians(sal))))
#print(sun_declination(23.43, 89.78))

def obliq_cor(moe, jc):
    return moe  +0.00256*m.cos(m.radians(125.04-1934.136*jc))

def mean_obliq_eliptic(jc):
    return 23+(26+((21.448-jc*(46.815+jc*(0.00059-jc*0.001813))))/60)/60

def julian_century(jd):
    return (jd-2451545)/36525

def julian_day(ts, tz):
    return ts.to_julian_date() - tz /24
    
def sun_app_long(stl, jc):
    return stl-0.00569-0.00478*m.sin(m.radians(125.04-1934.136*jc))
    
def sun_true_long(gmls, seoc):
    return gmls+seoc

def geom_mean_long_sun(jc):
    return (280.46646+jc*(36000.76983 + jc*0.0003032)) % 360

def sun_eq_of_ctr(gmas, jc):
    return m.sin(m.radians(gmas))*(1.914602-jc*(0.004817+0.000014*jc))+m.sin(m.radians(2*gmas))*(0.019993-0.000101*jc)+m.sin(m.radians(3*gmas))*0.000289

def geom_mean_anom_sun(jc):
    return 357.52911+jc*(35999.05029 - 0.0001537*jc)

def hour_angle(tst):
    if (tst / 4) < 0:
        return (tst / 4) + 180
    else:
        return (tst / 4) - 180
    
def true_solar_time(time, eot, long, tz):
    return (time*1440+eot+4*long-60*tz) % 1440

def eq_of_time(vy, gmls, eeo, gmas):
    return 4*m.degrees(vy*m.sin(2*m.radians(gmls))-2*eeo*m.sin(m.radians(gmas))+4*eeo*vy*m.sin(m.radians(gmas))*m.cos(2*m.radians(gmls))-0.5*vy*vy*m.sin(4*m.radians(gmls))-1.25*eeo*eeo*m.sin(2*m.radians(gmas)))

def eccent_earth_orbit(jc):
    return 0.016708634-jc*(0.000042037+0.0000001267*jc)

def var_y(oc):
    return m.tan(m.radians(oc/2))*m.tan(m.radians(oc/2))

def solar_zenith_angle(lat, sd, ha):
    return m.degrees(m.acos(m.sin(m.radians(lat))*m.sin(m.radians(sd))+m.cos(m.radians(lat))*m.cos(m.radians(sd))*m.cos(m.radians(ha))))

def solar_elevation_angle(sza):
    return 90 - sza

def solar_elevation_angle_with_refraction(aar, sea):
    return aar + sea

def approx_atmo_refraction(sea):
    if sea > 85:
        return 0  
    if sea > 5:
        return (58.1 / m.tan(m.radians(sea)) - 0.07 / m.pow(m.tan(m.radians(sea)), 3) + 0.000086 / m.pow(m.tan(m.radians(sea)), 5)) / 3600
    if sea > -0.575:
        return (1735 + sea * (-518.2 + sea * (103.4 + sea * (-12.79 + sea * 0.711)))) / 3600
    else:
        return (-20.772 / m.tan(m.radians(sea))) / 3600

def azimuth(lat, ha, sza, sd):
    if ha > 0:
        return (m.degrees(m.acos(((m.sin(m.radians(lat)) * m.cos(m.radians(sza))) - m.sin(m.radians(sd))) / (m.cos(m.radians(lat)) * m.sin(m.radians(sza))))) + 180) % 360
    else:
        return  (540-m.degrees(m.acos(((m.sin(m.radians(lat))*m.cos(m.radians(sza)))-m.sin(m.radians(sd)))/(m.cos(m.radians(lat))*m.sin(m.radians(sza)))))) % 360

    
    
# command line arguments: lat, long, year, month, day, hour, minute, timezone
#lat, long: float
#year, month, day, hour, minute: int
# timezone: in UTC, ex: Bellingham is -8
def main():
    if len(sys.argv) != 9:
        sys.exit("incorrect number of arguments")
    lat = float(sys.argv[1])
    long = float(sys.argv[2])
    year = int(sys.argv[3])
    month = int(sys.argv[4])
    day = int(sys.argv[5])
    hour = int(sys.argv[6])
    minute = int(sys.argv[7])
    tz = int(sys.argv[8])
    date = pd.Timestamp(year=year, month=month, day=day, hour=hour, minute=minute, tz='UTC')
    
    jd = julian_day(date, tz) 
    jc = julian_century(jd) 
    gmas = geom_mean_anom_sun(jc) 
    gmls = geom_mean_long_sun(jc) 
    moe = mean_obliq_eliptic(jc) 
    oc = obliq_cor(moe, jc) 
    seoc = sun_eq_of_ctr(gmas, jc) 
    stl = sun_true_long(gmls, seoc) 
    sal = sun_app_long(stl, jc) 
    eeo = eccent_earth_orbit(jc) 
    vy = var_y(oc) 
    eot = eq_of_time(vy, gmls, eeo, gmas) 
    sd = sun_declination(oc, sal) 
    time_since_midnight = (date.hour * 60 + date.minute) * 0.1 / 144
    tst = true_solar_time(time_since_midnight, eot, long, tz) 
    ha = hour_angle(tst) 
    sza = solar_zenith_angle(lat, sd, ha) 
    az = azimuth(lat, ha, sza, sd)
    sea = solar_elevation_angle(sza)
    aar = approx_atmo_refraction(sea) 
    seawr = solar_elevation_angle_with_refraction(aar, sea)
    
    print(f"azimuth: {az} elevation: {seawr}")
    return [az, seawr]
    

if __name__=="__main__":
    main()

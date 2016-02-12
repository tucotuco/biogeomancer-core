# Web Services Vision #
A georeferencing web service that accepts a DwC record and returns that record along with all georeferences for it generated by the BioGeomancer Core API.

# Single Georeference Requirements #

## Web Service Interface ##
The web service interface accepts HTTP GET requests using the following case insensitive URL parameters:

| **Parameter**  | **Reference** |
|:---------------|:--------------|
| highergeography, hg | http://rs.tdwg.org/dwc/terms/index.htm#higherGeography |
| continent, cont | http://rs.tdwg.org/dwc/terms/index.htm#continent  |
| waterbody, wb, w | http://rs.tdwg.org/dwc/terms/index.htm#waterBody  |
| islandgroup, ig | http://rs.tdwg.org/dwc/terms/index.htm#islandGroup |
| island, is     | http://rs.tdwg.org/dwc/terms/index.htm#island  |
| country, cy, gadm0  | http://rs.tdwg.org/dwc/terms/index.htm#country |
| stateprovince, sp, s, gadm1 | http://rs.tdwg.org/dwc/terms/index.htm#stateProvince |
| county, co, gadm2 | http://rs.tdwg.org/dwc/terms/index.htm#county |
| locality, loc, l | http://rs.tdwg.org/dwc/terms/index.htm#locality |
| verbatimlatitude, latitude, vlat, lat | http://rs.tdwg.org/dwc/terms/index.htm#verbatimLatitude |
| verbatimlongitude, longitude, vlong, vlng, long, lng | http://rs.tdwg.org/dwc/terms/index.htm#verbatimLongitude |
| interpreter, int, i | BioGeomancer locality interpreter ('yale', 'uiuc', 'tulane'; default is 'yale')|
| header         | BioGeomancer flag to show locality input in output header ('true', 'false'; default is 'false')|

Example:
`http://bg.berkeley.edu/ws/single?locality=Stuttgart&country=Germany&interpreter=yale&header=true`

## Web Service Output ##
The web service output format will be XML (UTF-8) with `dwc` as the namespace for Darwin Core elements. All URL request parameters will appear in the output if the header parameter is set to 'true'. For example:

```
<?xml version="1.0" encoding="utf-8"?>
<biogeomancer xmlns:dwc="http://rs.tdwg.org/dwc/terms/">
  <interpreter>yale</interpreter>
  <dwc:country>Germany</dwc:country>
  <dwc:locality>Stuttgart</dwc:locality>
  <georeference>
    <interpretedLocality>Germany (gadm2:58:countries); Stuttgart (GADM:16422:countries, 2nd order divisions)</interpretedLocality>
    <dwc:decimalLatitude>49.1430493</dwc:decimalLatitude>
    <dwc:decimalLongitude>9.6254345</dwc:decimalLongitude>
    <dwc:geodeticDatum>WGS84</dwc:geodeticDatum>
    <dwc:coordinateUncertaintyInMeters>93264.0</dwc:coordinateUncertaintyInMeters>
  </georeference>
</biogeomancer>
```


# Batch Georeference Requirements #
## Web Service Interface ##
Batch georeferencing will be supported by HTTP POST of an XML batch request with a 5000 record limit. In addition to an optional field "id" (case sensitive), the following DwC elements shown in the table above are accepted and interpreted as input.

Example batch request XML:

```
<?xml version=\"1.0\" encoding=\"utf-8\"?>"
<biogeomancer xmlns="http://bg.berkeley.edu" xmlns:dwc="http://rs.tdwg.org/dwc/terms">
<request type="batch" interpreter="yale" header="true">
  <record>
   <dwc:localityId>1</dwc:localityId>
   <dwc:locality>Berkeley</dwc:locality>
   <dwc:stateProvince>California</dwc:stateProvince>
  </record>
  <record>
   <dwc:locality>Stuttgart</dwc:locality>
   <dwc:country>Germany</dwc:country>
  </record>
  <record>
   <dwc:locality>3 mi E Lolo</dwc:locality>
   <dwc:county>Missoula</dwc:county>
  </record>
 </request>
</biogeomancer>
```

## Web Service Output ##
Batch georeferencing output will be XML (UTF-8) with a dwcore and dwgeo namespaces reflecting concepts in the Darwin Core and Geospatial Extension schemas. For example, the response for the request shown above should be:
<?xml version="1.0" encoding="UTF-8"?>
<biogeomancer xmlns:dwc="http://rs.tdwg.org/dwc/terms">
  <records>
   <record>
    <interpreter>yale</interpreter>
    <dwc:id>1<dwc:id>
    <dwc:stateProvince>California</dwc:stateProvince>
    <dwc:locality>Berkeley</dwc:locality>
    <georeference>
     <interpretedLocality>California (gadm2:3691:countries, 1st order divisions); Berkeley (GNIS:78808477:populated places)</interpretedLocality>
     <dwc:decimalLatitude>37.8715897</dwc:decimalLatitude>
     <dwc:decimalLongitude>-122.26812</dwc:decimalLongitude>
     <dwc:geodeticDatum>WGS84</dwc:geodeticDatum>
     <dwc:coordinateUncertaintyInMeters>5391.0</dwc:coordinateUncertaintyInMeters>
    </georeference>
   </record>
   <record>
    <interpreter>yale</interpreter>
    <dwc:country>Germany</dwc:country>
    <dwc:locality>Stuttgart</dwc:locality>
    <georeference>
     <interpretedLocality>Germany (gadm2:58:countries); Stuttgart (GADM:16422:countries, 2nd order divisions)</interpretedLocality>
     <dwc:decimalLatitude>49.1430493</dwc:decimalLatitude>
     <dwc:decimalLongitude>9.6254345</dwc:decimalLongitude>
     <dwc:geodeticDatum>WGS84</dwc:geodeticDatum>
     <dwc:coordinateUncertaintyInMeters>93264.0</dwc:coordinateUncertaintyInMeters>
    </georeference>
    <georeference>
     <interpretedLocality>Germany (gadm2:58:countries); Stuttgart (GeoNet Names (NIMA):68012471:agricultural sites)</interpretedLocality>
     <dwc:decimalLatitude>47.6852721</dwc:decimalLatitude>
     <dwc:decimalLongitude>8.9012315</dwc:decimalLongitude>
     <dwc:geodeticDatum>WGS84</dwc:geodeticDatum>
     <dwc:coordinateUncertaintyInMeters>4552.0</dwc:coordinateUncertaintyInMeters>
    </georeference>
    <georeference>
     <interpretedLocality>Germany (gadm2:58:countries); Stuttgart (GeoNet Names (NIMA):68012472:airport features)</interpretedLocality>
     <dwc:decimalLatitude>48.6833324</dwc:decimalLatitude>
     <dwc:decimalLongitude>9.1999998</dwc:decimalLongitude>
     <dwc:geodeticDatum>WGS84</dwc:geodeticDatum>
     <dwc:coordinateUncertaintyInMeters>4242.0</dwc:coordinateUncertaintyInMeters>
    </georeference>
    <georeference>
     <interpretedLocality>Germany (gadm2:58:countries); Stuttgart (GeoNet Names (NIMA):68012473:capitol buildings)</interpretedLocality>
     <dwc:decimalLatitude>48.7666683</dwc:decimalLatitude>
     <dwc:decimalLongitude>9.1833339</dwc:decimalLongitude>
     <dwc:geodeticDatum>WGS84</dwc:geodeticDatum>
     <dwc:coordinateUncertaintyInMeters>2282.0</dwc:coordinateUncertaintyInMeters>
    </georeference>
    <georeference>
     <interpretedLocality>Germany (gadm2:58:countries); Stuttgart (usersdb:8999:user added feature)</interpretedLocality>
     <dwc:decimalLatitude>48.7666702</dwc:decimalLatitude>
     <dwc:decimalLongitude>9.1833296</dwc:decimalLongitude>
     <dwc:geodeticDatum>WGS84</dwc:geodeticDatum>
     <dwc:coordinateUncertaintyInMeters>10048.0</dwc:coordinateUncertaintyInMeters>
    </georeference>
   </record>
   <record>
    <interpreter>yale</interpreter>
    <dwc:county>Missoula</dwc:county>
    <dwc:locality>3 mi E Lolo</dwc:locality>
    <georeference>
     <interpretedLocality>Missoula (gadm2:34036:countries, 2nd order divisions); 3 mi E of Lolo (GNIS:78928362:manmade features)</interpretedLocality>
     <dwc:decimalLatitude>46.7588158</dwc:decimalLatitude>
     <dwc:decimalLongitude>-114.017748</dwc:decimalLongitude>
     <dwc:geodeticDatum>WGS84</dwc:geodeticDatum>
     <dwc:coordinateUncertaintyInMeters>6303.0</dwc:coordinateUncertaintyInMeters>
    </georeference>
    <georeference>
     <interpretedLocality>Missoula (gadm2:34036:countries, 2nd order divisions); 3 mi E of Lolo (GNIS:79784822:populated places)</interpretedLocality>
     <dwc:decimalLatitude>46.7588158</dwc:decimalLatitude>
     <dwc:decimalLongitude>-114.017748</dwc:decimalLongitude>
     <dwc:geodeticDatum>WGS84</dwc:geodeticDatum>
     <dwc:coordinateUncertaintyInMeters>8061.0</dwc:coordinateUncertaintyInMeters>
    </georeference>
    <georeference>
     <interpretedLocality>Missoula (gadm2:34036:countries, 2nd order divisions); 3 mi E of Lolo (CONUS TIGER:8009251:populated places)</interpretedLocality>
     <dwc:decimalLatitude>46.7742577</dwc:decimalLatitude>
     <dwc:decimalLongitude>-114.0447684</dwc:decimalLongitude>
     <dwc:geodeticDatum>WGS84</dwc:geodeticDatum>
     <dwc:coordinateUncertaintyInMeters>8172.0</dwc:coordinateUncertaintyInMeters>
    </georeference>
   </record>
  </records>
</biogeomancer>}}}```
"TelematixWU" is intended to be a post process on the output csv after we run the "Telematix" app
- this app will examine the csv produced by the "Telematix" app and fill in weather variables where needed
- the missing historical data is gathered from the Weather Underground api with a call like this.. http://api.wunderground.com/api/249598d1abbd181c/history_20130616/geolookup/q/43,-89.json
- this is a necessity because we are missing historical data for 6/16 & 6/20 (there is a gap in time between Mike Stiller's archives & our Glacier archives)
- the csv should be complete after running this app, all rows should contain weather variables
"Telematix" application is a POC throw away app written with the sole purpose of adding weather variables of interest to a known csv of latlons & times, the csv is included in the repo
- the sample csv input file exists at ../src/main/resources/sample_trip.csv
- the app reads through each record in the csv (the app refere to one record as a "moment") and initiates a point request into a netcdf file
- the app requires about 350gb of netcdf files to run, for obvious reasons these are not included in the repo (ask Wes Edge for the netcdf files if you need them)
- config file is a good place to look, you will most certainly need to modify this file if you're gonna run this app, config exists at ../src/main/resources/config.properties

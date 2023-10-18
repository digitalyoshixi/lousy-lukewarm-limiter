## Lousy Lukewarm Limiter

Hello, install this program to protect the eyes. it allows you to manually or automatically configure display temperature and brightness.

## Compiling(If it wasn't already compiled)
1. open the 'limiter' folder in terminal
2. javac -cp jnativehook.jar davidspackage/testkeys.java davidspackage/lousylukewarmlimiter.java

## Starting the Program
1. open the 'limiter' folder in terminal
2. java -cp jnativehook.jar;. davidspackage.Main
#### Running with config file
java -cp jnativehook.jar;. davidspackage.Main -config C:/pathtofile.cfg

#### Config default file contents:
```
# Automatic Settings
REDSHIFTBIND={Alt,Shift,N}
AUTOBRIGHTBIND={Alt,Shift,M}
BRIGHTNESSMIN=4
BRIGHTNESSCAP=10
REDSHIFTMIN=3300
REDSHIFTCAP=5500
LONGITUDE=-79.323281
# tickrate update for locaiton based. default is every 20 seconds
UPDATECOUNTER=20
# No Automatic Settings
NOAUTORED=4500
NOAUTOBRIGHT=5
# logfile. locally referenced depending on current working directory.
LOGFILE=logfile.txt
```

#### Libraries and Software used:
- https://github.com/kwhat/jnativehook
- https://github.com/jonls/redshift

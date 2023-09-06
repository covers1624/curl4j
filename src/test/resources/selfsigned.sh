#!/bin/bash

keytool -genkey -keyalg RSA -alias key -keystore selfsigned.jks -storepass password -validity 9999 -keysize 2048 -ext SAN=DNS:localhost,IP:127.0.0.1 -validity 9999

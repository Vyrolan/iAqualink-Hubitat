# iAqualink-Hubitat
Hubitat Driver for the iAqualink Pool/Spa Controller

## Intro
This driver aims to bring all of the functionality provided by the iAqualink device/app into Hubitat.  Some functionality was missing from existing drivers, and the representation of the devices was not always ideal (IMHO). I had the following goals when building out this driver:
* All status data available in the iAqualink mobile app should be available via the driver.
* The parent device of the driver should contain all status data as attributes to simplify gathering all pool/spa status into Node-Red in one shot.
* The pool and spa heater should be represented properly as Thermostat devices.
* The air, pool, and spa current temperatures should be represented properly as Temperature sensors.
* Support having multiple iAqualinks linked to the account (config option to pick which one to use)
* Account username/password should be the only necessary config to get started (pull device serial number via api)
* Customizable auto-refresh time interval
* Minimize API calls where possible including reusing logged in session as long as possible
* Customizable number of Aux Devices and OneTouch setups (I doubt many real users are using all of their aux devices or OneTouch slots.)
* Customizable temperature units (F, C, or follow the hub's setting)
* Uncluttered attributes (internal values held in state and attributes reserved for user-facing values useful for control/automation)
* Good quality logging, preference, descriptions, etc for a good user experience

## Overview
The driver creates a parent "iAqualink" device which provides no capabilities other than initialize to create the child devices.  However, this parent device has attributes for all of the available data within the driver.  This makes it an excellent choice to use via Maker API in other tools (e.g. Node-Red) to obtain a complete picture of your entire system from a single node.

The main filter pump, auxillary devices (e.g. spa jets, booster pumps, etc.), and OneTouch setups are setup as simple Switch (on/off) child devices.  The switch for Spa mode and for a solar heater are also created as Switch child devices if enabled in preferences.  The parent device has attributes for the current air, pool, and spa temperatures, but they can also be made available as Temperature sensor child devices via the preferences.  Color-supporting lights are also supported at a very basic level (see Limitations below).

The pool and spa heaters (both if enabled in preferences) are created as Thermostat child devices. They correctly report mode, operating status, current temperature, and setpoint.  Controlling them works like controlling any other Thermostat device to disable/enable the heater or set the target temperature.  They will automatically enter the "emergency heat" mode if freeze protection is active on the Aqualink.  For operating status, they report "idle" when the heater is disabled and either "pending heat" or "heating" while it is enabled based on whether or not the heater is actively running.

## Install

### Install via HPM
The package is available as "iAqualink Driver" in the [Hubitat Package Manager (HPM)](https://github.com/dcmeglio/hubitat-packagemanager).

### Install manually on hub

* Follow [these instructions](https://docs.hubitat.com/index.php?title=How_to_Install_Custom_Drivers) for how to install custom drivers.
* There are several drivers to import:
  * Parent Device "iAqualink": https://raw.githubusercontent.com/Vyrolan/iAqualink-Hubitat/main/iAqualink.groovy
  * Simple On/Off Child Device: https://raw.githubusercontent.com/Vyrolan/iAqualink-Hubitat/main/iAqualink-ToggleDevice.groovy
  * Heater Child Device: https://raw.githubusercontent.com/Vyrolan/iAqualink-Hubitat/main/iAqualink-Heater.groovy
  * Temperature Sensor Child Device: https://raw.githubusercontent.com/Vyrolan/iAqualink-Hubitat/main/iAqualink-TempSensor.groovy
  * Color Light Child Device: https://raw.githubusercontent.com/Vyrolan/iAqualink-Hubitat/main/iAqualink-ColorLight.groovy

## Configuration
When you initially create the parent device and assign the parent driver "iAqualink" to it, you will be presented with a set of configuration options.  Most importantly, you must enter the username (email address) and password used to login to your iAqualink account.  

If your account has more than one iAqualink linked to it, you may select which one by giving its zero-based index to the Device Index preference.  Most users likely don't need to do this and can leave the default to pull in the one and only device on their account.

If your system has a Spa, enable the control for Spa Mode (which switches the filter pump from pool+spa to spa only).  You can also enable pool, spa, and solar heaters as appropriate for your system.

If you want separte Temperature sensor child devices for the current air, pool, and spa temperatures, you may enable them in the preferences. You may also select the temperature unit.  If selected, all attribute values presented by the devices will be converted accordingly.  This may also mismatch with the iAqualink itself (for example if you select Celsius in the driver but your iAqualink is Farenheit, you will see Celsius values and also control the devices with Celsius values which will be automatically converted to Farenheit for actually controlling the iAqualink).

For auxillary devices, you may enter a number indicating how many are actually in use on your system.  This prevents meaningless child devices for unused devices like "N/A 6" from being created.  Similarly for OneTouch setups, you may enter how many are actually setup on your system to again avoid meaningless child devices (notably the driver won't include OneTouch setups that aren't active so setting this is not entirely necessary).

Finally, you may configure the auto update interval and logging preferences as you see fit.  The driver preserves the logged in session to reuse while updating, so an aggressive auto update interval should not be a problem (and even at the minimum of every minute is likely less traffic than if one leaves the iAqualink mobile app open).  You could of course also automate changing the interval based on time of day, presence, or whatever else makes sense for you.

## Limitations
My system does not have any of the following so I am unable to build or verify this functionality:
* Variable Speed Pumps (VSP)
* Dimmable Lights
* Color Lights (included based on an existing Aqualink driver but I cannot verify functionality and can't build out proper color-based capabilities for them)

## Special Thanks
* Mike Coffey for his [Aqualink driver](https://github.com/mikec85/hubitatdrivers/tree/master/aqualink) that simplified working out the API calls.
* Dan Cunningham for his [openHAB iAqualink addon](https://github.com/digitaldan/openhab-addons/tree/iaqualink-serialid-fix/bundles/org.openhab.binding.iaqualink) which also helped me better understand the API calls.
* Patrick Rigney for his [Venstar ColorTouch Hubitat driver](https://github.com/toggledbits/VenstarColorTouch-Hubitat) that helped me learn a lot about writing Hubitat drivers and to better understand the Thermostat capability.

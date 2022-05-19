metadata {
    definition(
            name: "iAqualink Heater",
            namespace: "iAqualink",
            author: "Vyrolan",
            importUrl: "https://raw.githubusercontent.com/Vyrolan/iAqualink-Hubitat/main/iAqualink-Heater.groovy"
    ) {
        capability "Thermostat"
        command "Update", null

        /*
        Thermostat Attributes
        coolingSetpoint - NUMBER, unit:°F || °C
        heatingSetpoint - NUMBER, unit:°F || °C
        schedule - JSON_OBJECT (Deprecated)
        supportedThermostatFanModes - JSON_OBJECT
        supportedThermostatModes - JSON_OBJECT
        temperature - NUMBER, unit:°F || °C
        thermostatFanMode - ENUM ["on", "circulate", "auto"]
        thermostatMode - ENUM ["auto", "off", "heat", "emergency heat", "cool"]
        thermostatOperatingState - ENUM ["heating", "pending cool", "pending heat", "vent economizer", "idle", "cooling", "fan only"]
        thermostatSetpoint - NUMBER, unit:°F || °C
        */
        /*
        Thermostat Commands
        auto()
        cool()
        emergencyHeat()
        fanAuto()
        fanCirculate()
        fanOn()
        heat()
        off()
        setCoolingSetpoint(temperature)
        setHeatingSetpoint(temperature)
        setThermostatFanMode(fanmode)
        setThermostatMode(thermostatmode)
         */
    }

    preferences {
        section {
            input(
                    name: "debugLogEnable",
                    type: "bool",
                    title: "Enable debug logging",
                    defaultValue: false
            )
            input(
                    name: "infoLogEnable",
                    type: "bool",
                    title: "Enable info logging",
                    defaultValue: false
            )
            input(
                    name: "autoDisableDebugLog",
                    type: "number",
                    title: "Auto-disable debug logging",
                    description: "Automatically disable debug logging after this number of minutes (0 = Do not disable)",
                    defaultValue: 15
            )
            input(
                    name: "autoDisableInfoLog",
                    type: "number",
                    title: "Disable Info Logging",
                    description: "Automatically disable info logging after this number of minutes (0 = Do not disable)",
                    defaultValue: 15
            )
        }
    }
}

// [Driver API] Called when Device is first created
void installed() {
    unschedule()
    if (settings.debugLogEnable && settings.autoDisableDebugLog > 0)
        runIn(settings.autoDisableDebugLog * 60, disableDebugLog)
    if (settings.infoLogEnable && settings.autoDisableInfoLog > 0)
        runIn(settings.autoDisableInfoLog * 60, disableInfoLog)

    state.heaterEnabled = false

    sendEvent(name: 'supportedThermostatModes', value: ['off', 'heat', 'emergency heat'])
    sendEvent(name: 'supportedThermostatFanModes', value: ['auto'])
    sendEvent(name: 'thermostatFanMode', value: 'auto')
    sendEvent(name: 'coolingSetpoint', value: 0)

    sendEvent(name: "temperature", value: 0)
    sendEvent(name: "heatingSetpoint", value: 0)
    sendEvent(name: "thermostatMode", value: "off")
    sendEvent(name: "thermostatOperatingState", value: "idle")
}

// [Driver API] Called when Device's preferences are changed
void updated() {
    infoLog("Preferences changed...")
    installed()
}

// [Driver API] Called when Device receives a message
void parse(String description) { }

void debugLog(String msg) {
    if (settings.debugLogEnable)
        log.debug("${device.label?device.label:device.name}: ${msg}")
}

void infoLog(String msg) {
    if (settings.infoLogEnable)
        log.info("${device.label?device.label:device.name}: ${msg}")
}

void warnLog(String msg) {
    log.warn("${device.label?device.label:device.name}: ${msg}")
}

void errorLog(String msg) {
    log.error("${device.label?device.label:device.name}: ${msg}")
}

void disableDebugLog() {
    infoLog("Automatically disabling debug logging after ${settings.autoDisableDebugLog} minutes.")
    device.updateSetting("debugLogEnable", [value: "false", type: "bool"])
}

void disableInfoLog() {
    infoLog("Automatically disabling info logging after ${settings.autoDisableInfoLog} minutes.")
    device.updateSetting("infoLogEnable", [value: "false", type: "bool"])
}

void Update(){
    parent.Update()
}

void setType(String heaterType, String tempVar) {
    state.heaterType = heaterType
    state.tempVar = tempVar
}

void updateState(String heaterStatus, String freezeProtect, Integer currentTemp, Integer targetTemp, String configTempScale, String iaqualinkTempScale) {
    if (freezeProtect == "on") {
        sendEvent(name: "thermostatMode", value: "emergency heat")
        sendEvent(name: "thermostatOperatingState", value: "heating")
    }
    else if (heaterStatus != "off") {
        sendEvent(name: "thermostatMode", value: "heat")
        def operatingState = (heaterStatus == "heating") ? "heating" : "pending heat"
        sendEvent(name: "thermostatOperatingState", value: operatingState)
    }
    else {
        sendEvent(name: "thermostatMode", value: "off")
        sendEvent(name: "thermostatOperatingState", value: "idle")
    }

    state.heaterEnabled = (heaterStatus != "off")
    state.tempScale = configTempScale
    state.iaqualinkTempScale = iaqualinkTempScale

    sendEvent(name: "temperature", value: currentTemp, unit: tempScale)
    sendEvent(name: "heatingSetpoint", value: targetTemp, unit: tempScale)
}

// Thermostat Capability Methods
void auto() { debugLog("Auto mode is not supported for iAqualink Heater.") }
void cool() { debugLog("Cool mode is not supported for iAqualink Heater.") }
void emergencyHeat() { debugLog("Explicitly setting Emergency Heat mode is not supported...the iAqualink Heater will be in emergency heating when freeze protection is active.") }
void fanAuto() { debugLog("Fan operations are not supported for iAqualink Heater. ") }
void fanCirculate() { debugLog("Fan operations are not supported for iAqualink Heater. ") }
void fanOn() { debugLog("Fan operations are not supported for iAqualink Heater. ") }
void setThermostatFanMode(fanmode) { debugLog("Fan operations are not supported for iAqualink Heater. ") }
void setCoolingSetpoint(temperature) { debugLog("Cooling is not supported for iAqualink Heater.") }

void setThermostatMode(thermostatmode) {
    if (thermostatmode != "off" && thermostatmode != "heat") {
        warnLog("Thermostat mode '${thermostatmode}' not supported for iAqualink Heater.")
        return
    }

    if (thermostatmode == "off")
        off()
    else // if (thermostatmode == "heat")
        heat()
}

void heat() {
    if (state.heaterEnabled) {
        debugLog("${device.name} is already enabled")
        return
    }
    def msg = "Enabling ${device.name}"
    infoLog(msg)
    parent.doCommand(msg, "set_${state.heaterType}_heater")
    sendEvent(name: 'thermostatMode', value: "heat")
    if ((device.currentValue("temperature") as Integer) < (device.currentValue("heatingSetpoint") as Integer)) {
        sendEvent(name: "thermostatOperatingState", value: "heating")
        parent.updateAttribute(name: "${state.heaterType}_heater", value: "heating")
    }
    else {
        sendEvent(name: "thermostatOperatingState", value: "pending heat")
        parent.updateAttribute(name: "${state.heaterType}_heater", value: "enabled")
    }
}

void off() {
    if (!(state.heaterEnabled)) {
        debugLog("${device.name} is already disabled")
        return
    }
    def msg ="Disabling ${device.name}"
    infoLog(msg)
    parent.doCommand(msg, "set_${state.heaterType}_heater")
    sendEvent(name: 'thermostatMode', value: "off")
    sendEvent(name: "thermostatOperatingState", value: "idle")
    parent.updateAttribute(name: "${state.heaterType}_heater", value: "off")
}

Integer convertTemperatureForDevice(Integer temperature) {
    if (state.iaqualinkTempScale == state.tempScale)
        return temperature
    else if (state.tempScale == "F")
        return fahrenheitToCelsius(temperature) as Integer
    else // if (state.tempScale == "C")
        return celsiusToFahrenheit(temperature) as Integer
}

void setHeatingSetpoint(temperature) {
    def msg = "Setting Heating Target to ${temperature} degrees for ${device.name}"
    infoLog(msg)
    parent.doCommand(msg, "set_temps", [("${state.tempVar}" as String): convertTemperatureForDevice(temperature as Integer) as String])
    sendEvent(name: "heatingSetpoint", value: temperature as Integer, unit: state.tempScale)
    parent.updateAttribute(name: "${state.heaterType}_set_point", value: temperature as String, unit: state.tempScale)
}

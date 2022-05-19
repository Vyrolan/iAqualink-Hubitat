metadata {
    definition(
            name: "iAqualink TempSensor",
            namespace: "iAqualink",
            author: "Vyrolan",
            importUrl: "https://raw.githubusercontent.com/Vyrolan/iAqualink-Hubitat/main/iAqualink-TempSensor.groovy"
    ) {
        capability "TemperatureMeasurement"
        command "Update", null
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

void updateState(String temperature, String label) {
    def configuredUnit = parent.device.currentValue("iaqualink_temp_scale") as String
    sendEvent(name: "temperature", value: temperature as Integer, unit: configuredUnit)
}

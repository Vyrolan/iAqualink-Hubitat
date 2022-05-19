metadata {
    definition(
            name: "iAqualink",
            namespace: "iAqualink",
            author: "Vyrolan",
            importUrl: "https://raw.githubusercontent.com/Vyrolan/iAqualink-Hubitat/main/iAqualink.groovy"
    ) {
        capability "Initialize"

        attribute "online", "enum", [ "off", "on" ]
        attribute "iaqualink_temp_scale", "string"
        attribute "config_temp_scale", "string"
        attribute "air_temp", "number"
        attribute "pool_temp", "number"
        attribute "spa_temp", "number"
        attribute "pool_set_point", "number"
        attribute "spa_set_point", "number"

        attribute "freeze_protection", "enum", [ "off", "on" ]
        attribute "spa_mode", "enum", [ "off", "on" ]
        attribute "filter_pump", "enum", [ "off", "on" ]
        attribute "spa_heater", "enum", [ "off", "enabled", "heating" ]
        attribute "pool_heater", "enum", [ "off", "enabled", "heating" ]
        attribute "solar_heater", "enum", [ "off", "on" ]

        attribute "aux1", "enum", [ "off", "on" ]
        attribute "aux1_label", "string"
        attribute "aux2", "enum", [ "off", "on" ]
        attribute "aux2_label", "string"
        attribute "aux3", "enum", [ "off", "on" ]
        attribute "aux3_label", "string"
        attribute "aux4", "enum", [ "off", "on" ]
        attribute "aux4_label", "string"
        attribute "aux5", "enum", [ "off", "on" ]
        attribute "aux5_label", "string"
        attribute "aux6", "enum", [ "off", "on" ]
        attribute "aux6_label", "string"
        attribute "aux7", "enum", [ "off", "on" ]
        attribute "aux7_label", "string"
        attribute "aux8", "enum", [ "off", "on" ]
        attribute "aux8_label", "string"

        attribute "onetouch1", "enum", [ "off", "on" ]
        attribute "onetouch1_label", "string"
        attribute "onetouch2", "enum", [ "off", "on" ]
        attribute "onetouch2_label", "string"
        attribute "onetouch3", "enum", [ "off", "on" ]
        attribute "onetouch3_label", "string"
        attribute "onetouch4", "enum", [ "off", "on" ]
        attribute "onetouch4_label", "string"
        attribute "onetouch5", "enum", [ "off", "on" ]
        attribute "onetouch5_label", "string"
        attribute "onetouch6", "enum", [ "off", "on" ]
        attribute "onetouch6_label", "string"

        attribute "cover_pool", "string"
        attribute "spa_salinity", "string"
        attribute "pool_salinity", "string"
        attribute "orp", "string"
        attribute "ph", "string"

        command "Update", null
    }

    preferences {
        section("Device Settings:") {
            input(
                    name: "email",
                    type: "string",
                    title: "Email",
                    description: "iAqualink Login Email",
                    required: true,
                    displayDuringSetup: true
            )
            input(
                    name: "password",
                    type: "string",
                    title: "Password",
                    description: "iAqualink Login Password",
                    required: true,
                    displayDuringSetup: true
            )
            input(
                    name: "deviceIndex",
                    type: "number",
                    title: "Device Index",
                    description: "Index within list of devices on the iAqualink account. Zero-based. Default 0 to select first (or only) device should work for most users.",
                    defaultValue: 0,
                    displayDuringSetup: true
            )
        }
        section("Pool/Spa Settings:") {
            input(
                    name: "spaMode",
                    type: "bool",
                    title: "Enable Control for Spa Mode",
                    defaultValue: false,
                    displayDuringSetup: true
            )
            input(
                    name: "poolHeater",
                    type: "bool",
                    title: "Enable Pool Heater",
                    defaultValue: false,
                    displayDuringSetup: true
            )
            input(
                    name: "spaHeater",
                    type: "bool",
                    title: "Enable Spa Heater",
                    defaultValue: false,
                    displayDuringSetup: true
            )
            input(
                    name: "solarHeater",
                    type: "bool",
                    title: "Enable Solar Heater",
                    defaultValue: false,
                    displayDuringSetup: true
            )
            input (
                    name: "tempUnit",
                    type: "enum",
                    title: "Temperature Unit",
                    description: "Unit used for Temperature values. Defaults to the hub's configured unit but can be forced to F or C here.",
                    options: [
                            "hub": "Hub's Configured Unit (default)",
                            "C": "Celsius",
                            "F": "Fahrenheit"
                    ],
                    required: true,
                    defaultValue: "hub",
                    displayDuringSetup: true
            )
            input (
                    name: "airTemp",
                    type: "bool",
                    title: "Enable Air Temp Sensor",
                    description: "Include separate temperature sensor child device with the air temperature? (Parent device always has the attribute.)",
                    defaultValue: false,
                    displayDuringSetup: true
            )
            input (
                    name: "poolTemp",
                    type: "bool",
                    title: "Enable Pool Temp Sensor",
                    description: "Include separate temperature sensor child device with the pool temperature? (Parent device always has the attribute.)",
                    defaultValue: false,
                    displayDuringSetup: true
            )
            input (
                    name: "spaTemp",
                    type: "bool",
                    title: "Enable Spa Temp Sensor",
                    description: "Include separate temperature sensor child device with the spa temperature? (Parent device always has the attribute.)",
                    defaultValue: false,
                    displayDuringSetup: true
            )
        }
        section("Auxillary/OneTouch Devices:") {
            input(
                    name: "numAux",
                    type: "number",
                    title: "Number of AUX Devices",
                    description: "How many of the AUX devices to include? (Default 7 is max.)",
                    defaultValue: 7,
                    displayDuringSetup: true
            )
            input(
                    name: "auxEA",
                    type: "bool",
                    title: "Enable AUX EA Port",
                    description: "Include the special 8th AUX device (which may show simply as 'N/A 8' in the iAqualink app)?",
                    defaultValue: false,
                    displayDuringSetup: true
            )
            input(
                    name: "numOneTouch",
                    type: "number",
                    title: "Number of OneTouch Setups",
                    description: "How many of the OneTouch setups to include? (Default 6 is max.)",
                    defaultValue: 6,
                    displayDuringSetup: true
            )
        }
        section("Auto-Update and Logging:") {
            input(
                    name: "autoUpdateInterval",
                    type: "number",
                    title: "Auto Update Interval Minutes (0 = Disabled)",
                    description: "Number of minutes between automatic updates to pull latest status",
                    defaultValue: 10,
                    displayDuringSetup: true
            )
            input(
                    name: "debugLogEnable",
                    type: "bool",
                    title: "Enable debug logging",
                    defaultValue: true
            )
            input(
                    name: "infoLogEnable",
                    type: "bool",
                    title: "Enable info logging",
                    defaultValue: true
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
    state.auth_time = 0
    initialize()
}

// [Driver API] Called when Device's preferences are changed
void updated() {
    infoLog("Preferences changed...")
    state.auth_time = 0 // force relogin after any settings changes
    initialize()
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

void updateAttribute(args) {
    sendEvent(args)
}

// [Capability Initialize]
void initialize() {
    unschedule()
    if (settings.debugLogEnable && settings.autoDisableDebugLog > 0)
        runIn(settings.autoDisableDebugLog * 60, disableDebugLog)
    if (settings.infoLogEnable && settings.autoDisableInfoLog > 0)
        runIn(settings.autoDisableInfoLog * 60, disableInfoLog)

    if (!ensureLogin())
        return

    ensureChildren()
    autoUpdate()
}

def ensureLogin() {
    if (now() - state.auth_time < 50 * 60 * 1000) {
        // old session should still be good (less than 50m elapsed)
        debugLog("Skipping login and reusing previous session that should still be active.")
        return true
    }

    // TODO: do other stuff to indicate errors?
    if (!doLogin()) {
        return false
    }
    if (!getSerialNumber()) {
        return false
    }
    return true
}

def doLogin() {
    if (!(settings.email) || !(settings.password)) {
        errorLog("Both Email and Password must be set in preferences.")
        return false
    }


    def url = "https://prod.zodiac-io.com/users/v1/login"

    def params = [
            uri: url,
            requestContentType: "application/json",
            body: """{"api_key": "EOOEMOW4YR6QNB07", "email": "${settings.email}", "password": "${settings.password}"}"""
    ]

    infoLog("Logging into iAqualink service...")

    def success = true
    httpPost(params) { response ->
        if (response?.status != 200) {
            errorLog("Login request failed (HTTP ${response?.status})")
            success = false
            return
        }

        state.session_id = response.data.session_id
        state.auth_token = response.data.authentication_token
        state.user_id = response.data.id
        state.auth_time = now()
        infoLog("Successfully logged in.")
    }
    return success
}

def doGetRequest(String desc, String url) {
    def params = [ uri: url ]

    def result = null
    httpGet(params) { response ->
        if (response?.status != 200) {
            errorLog("${desc} failed (HTTP ${response?.status})")
            return
        }
        result = response.data
    }
    return result
}

def doCommand(String desc, String command, LinkedHashMap<String, String> args = []) {
    def url = "https://p-api.iaqualink.net/v1/mobile/session.json?actionID=command&command=${command}&serial=${state.serial_number}&sessionID=${state.session_id}"
    if (args.size() > 0) {
        ArrayList params = []
        args.each{a -> params.add(a)}
        url += "&" + params.join("&")
    }
    debugLog("Executing iAqualink Command '${command}' with args = ${args}")
    return doGetRequest("Command ${command}", url)
}

def getSerialNumber() {
    infoLog("Fetching Serial Number for device #${settings.deviceIndex} on the account...")

    def response = doGetRequest("Fetching serial number", "https://r-api.iaqualink.net/devices.json?api_key=EOOEMOW4YR6QNB07&authentication_token=${state.auth_token}&user_id=${state.user_id}")
    if (settings.deviceIndex >= response.size()) {
        errorLog("Configured Device Index exceeds number of devices on the account.")
        return false
    }

    state.serial_number = response[settings.deviceIndex as Integer].serial_number
    infoLog("Fetched Serial Number of ${state.serial_number}.")
    return true
}

def getHome() {
    if (!ensureLogin()) return []
    infoLog("Fetching home screen.")
    def response = doCommand("Fetching home screen", "get_home")
    if (!response) return []
    return response.home_screen
}

def getDevices() {
    if (!ensureLogin()) return []
    infoLog("Fetching devices.")
    def response = doCommand("Fetching devices", "get_devices")
    if (!response) return []
    return response.devices_screen
}

def getOneTouch() {
    if (!ensureLogin()) return []
    infoLog("Fetching OneTouch.")
    def response = doCommand("Fetching OneTouch", "get_onetouch")
    if (!response) return []
    return response.onetouch_screen
}

def ensureChild(String key, String label, String type, String command = "", String parentAttr = "") {
    String childId = "${device.id}-${key}"

    if (getChildDevice(childId))
        return

    if (type == "0") {
        def d = addChildDevice("iAqualink ToggleDevice", childId, [name: label, isComponent: true])
        d.setCommand(command, parentAttr)
    }
    else if (type == "2") {
        def d = addChildDevice("iAqualink ColorLight", childId, [name: label, isComponent: true])
        d.setAuxNumber(key.substring(4))
    }
    else if (type == "TempSensor")
        addChildDevice("iAqualink TempSensor", childId, [name: label, isComponent: true])
}

def ensureHeaterChild(String key, String label, String heaterType, String tempVariable) {
    String childId = "${device.id}-${key}"

    if (getChildDevice(childId))
        return

    def d = addChildDevice("iAqualink Heater", childId, [name: label, isComponent: true])
    d.setType(heaterType, tempVariable)
}

void ensureChildren() {
    debugLog("Ensuring all child devices exist.")

    ensureChild("FilterPump", "Filter Pump", "0", "set_pool_pump", "filter_pump")
    if (settings.spaMode)
        ensureChild("SpaMode", "Spa Mode", "0", "set_spa_pump", "spa_mode")
    if (settings.airTemp)
        ensureChild("AirTemp", "Air Temp", "TempSensor")
    if (settings.poolTemp)
        ensureChild("PoolTemp", "Pool Temp", "TempSensor")
    if (settings.spaTemp)
        ensureChild("SpaTemp", "Spa Temp", "TempSensor")
    if (settings.poolHeater)
        ensureHeaterChild("PoolHeater", "Pool Heater", "pool", "temp2")
    if (settings.spaHeater)
        ensureHeaterChild("SpaHeater", "Spa Heater", "spa", "temp1")
    if (settings.solarHeater)
        ensureChild("SolarHeater", "Solar Heater", "0", "set_solar_heater", "solar_heater")

    if (settings.numAux > 0 || settings.auxEA) {
        devices = getDevices()
        for (int i = 1; i <= settings.numAux; i++) {
            def device = devices[i+2]["aux_${i}"]
            ensureChild("AUX_${i}" as String, device[1].label as String, device[3].type as String, "set_aux_${i}" as String, "aux${i}")
        }

        if (settings.auxEA) {
            def device = devices[10].aux_EA
            ensureChild("AUX_EA", device[1].label as String, device[3].type as String, "set_aux_8","aux8")
        }
    }

    if (settings.numOneTouch > 0) {
        onetouch = getOneTouch()
        for (int i = 1; i <= settings.numOneTouch; i++) {
            String key = "OneTouch_${i}"
            def ot = onetouch[i+1]["onetouch_${i}"]
            if (ot[0].status == "0")
                warnLog("Skipping ${key} since it does not seem to be setup on the iAqualink.")
            else
                ensureChild(key, ot[2].label as String, "0", "set_onetouch_${i}", "onetouch${i}")
        }
    }
}

void Update() {
    updateStates()
}

void autoUpdate() {
    try {
        updateStates()
    }
    catch (Exception e) {
        errorLog("Automatic status update failed...will continue to try after configured interval (${settings.autoUpdateInterval} mins). Error: ${e.message}")
    }

    if (settings.autoUpdateInterval > 0)
        runIn(settings.autoUpdateInterval * 60, autoUpdate)
}

void updateParentToggleState(String key, String status) {
    sendEvent(name: key, value: (status == "1" ? "on" : "off"))
}

String convertHeaterState(Integer status) {
    switch (status) {
        case 0:
            return "off"
        case 1:
            return "heating"
        case 3:
            return "enabled"
    }
    return null
}

Integer convertTemperature(String temp) {
    Integer temperature = (temp != "" ? temp as Integer : 0)
    def iaqualinkScale = device.currentValue("iaqualink_temp_scale") as String
    def configuredUnit = device.currentValue("config_temp_scale") as String

    if (iaqualinkScale == configuredUnit)
        return temperature
    else if (iaqualinkScale == "F")
        return fahrenheitToCelsius(temperature) as Integer
    else // if (iaqualinkScale == "C")
        return celsiusToFahrenheit(temperature) as Integer
}

void updateParentTemperatureState(String key, String temp) {
    sendEvent(name: key, value: convertTemperature(temp))
}

void updateChildState(String key, String status, String label = "") {
    def child = getChildDevice("${device.id}-${key}")
    if (child) {
        debugLog("Child Device '${label == "" ? key : label}' has status '${status}'")
        child.updateState(status, label)
    }
}

void updateChildHeater(String key, String status, String freezeProtect, Integer current, Integer target, String configTempScale, String iaqualinkTempScale) {
    def child = getChildDevice("${device.id}-${key}")
    if (!child) return

    debugLog("Update '${key}': status=${status}, freeze=${freezeProtect}, current=${current}, target=${target}")
    child.updateState(status, freezeProtect, current, target, configTempScale, iaqualinkTempScale)
}

void updateStates() {
    if (!ensureLogin()) {
        errorLog("Unable to update device states due to failed login.")
        return
    }

    infoLog("Updating all device states...")

    def home = getHome()

    updateChildState("FilterPump", home[12].pool_pump as String, "Filter Pump")
    updateParentToggleState("filter_pump", home[12].pool_pump as String)
    if (settings.spaMode) {
        updateChildState("SpaMode", home[11].spa_pump as String, "Spa Mode")
        updateParentToggleState("spa_mode", home[11].spa_mode as String)
    }

    if (settings.numAux > 0 || settings.auxEA) {
        devices = getDevices()
        for (int i = 1; i <= settings.numAux; i++) {
            def device = devices[i+2]["aux_${i}"]
            updateChildState("AUX_${i}" as String, device[0].state as String, device[1].label as String)
            updateParentToggleState("aux${i}" as String, device[0].state as String)
            sendEvent(name: "aux${i}_label", value: device[1].label)
        }

        if (settings.auxEA) {
            def device = devices[10].aux_EA
            updateChildState("AUX_EA", device[0].state as String, device[1].label as String)
            updateParentToggleState("aux8" as String, device[0].state as String)
            sendEvent(name: "aux8_label", value: device[1].label)
        }
    }

    if (settings.numOneTouch > 0) {
        onetouch = getOneTouch()
        for (int i = 1; i <= settings.numOneTouch; i++) {
            String key = "OneTouch_${i}"
            def ot = onetouch[i+1]["onetouch_${i}"]
            updateChildState(key, ot[1].state as String, ot[2].label as String)
            updateParentToggleState("onetouch${i}" as String, ot[1].state as String)
            sendEvent(name: "onetouch${i}_label", value: ot[2].label)
        }
    }

    // we'd rather do this in ensureChildren which is only called when preferences change,
    // but currently that path doesn't actually fetch the Home screen so I'd rather the
    // extra event here than the extra HTTP pull all of those times
    // This also lets it update more quickly if the hub's setting were changed.
    String tempScale = (settings.tempUnit == "hub" ? getTemperatureScale() : settings.tempUnit) as String
    sendEvent(name: "config_temp_scale", value: tempScale)

    String iaqualinkTempScale = home[3].temp_scale
    sendEvent(name: "iaqualink_temp_scale", value: iaqualinkTempScale)

    sendEvent(name: "online", value: (home[0].status == "Online" ? "on" : "off"))
    updateParentToggleState("solar_heater", home[15].solar_heater as String)
    sendEvent(name: "spa_salinity", value: home[16].pool_salinity)
    sendEvent(name: "pool_salinity", value: home[17].pool_salinity)
    sendEvent(name: "orp", value: home[18].orp)
    sendEvent(name: "ph", value: home[19].ph)

    String freezeProtect = (home[10].freeze_protection as String) == "1" ? "on" : "off"
    sendEvent(name: "freeze_protection", value: freezeProtect)

    String spaHeaterState = convertHeaterState(home[13].spa_heater as Integer)
    String poolHeaterState = convertHeaterState(home[14].pool_heater as Integer)
    sendEvent(name: "spa_heater", value: spaHeaterState)
    sendEvent(name: "pool_heater", value: poolHeaterState)


    Integer targetPoolTemp = convertTemperature(home[8].pool_set_point as String)
    Integer targetSpaTemp = convertTemperature(home[7].spa_set_point as String)
    sendEvent(name: "pool_set_point", value: targetPoolTemp, unit: tempScale)
    sendEvent(name: "spa_set_point", value: targetSpaTemp, unit: tempScale)

    Integer currentAirTemp = convertTemperature(home[6].air_temp as String)
    Integer currentPoolTemp = convertTemperature(home[5].pool_temp as String)
    Integer currentSpaTemp = convertTemperature(home[4].spa_temp as String)
    sendEvent(name: "air_temp", value: currentAirTemp, unit: tempScale)
    sendEvent(name: "pool_temp", value: currentPoolTemp, unit: tempScale)
    sendEvent(name: "spa_temp", value: currentSpaTemp, unit: tempScale)
    updateChildState("AirTemp", currentAirTemp as String)
    updateChildState("PoolTemp", currentPoolTemp as String)
    updateChildState("SpaTemp", currentSpaTemp as String)

    if (settings.poolHeater)
        updateChildHeater("PoolHeater", poolHeaterState, freezeProtect, currentPoolTemp, targetPoolTemp, tempScale, iaqualinkTempScale)
    if (settings.spaHeater)
        updateChildHeater("SpaHeater", spaHeaterState, freezeProtect, currentSpaTemp, targetSpaTemp, tempScale, iaqualinkTempScale)
}

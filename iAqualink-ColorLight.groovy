metadata {
    definition(
            name: "iAqualink ColorLight",
            namespace: "iAqualink",
            author: "Vyrolan",
            importUrl: "https://raw.githubusercontent.com/Vyrolan/iAqualink-Hubitat/main/iAqualink-ColorLight.groovy"
    ) {
        capability "Switch"
        command "SetColor", [[name: "Color", type: "String"]]
        command "Update", null
    }

    preferences {
        section {
            input(
                    name: "color",
                    type: "string",
                    title:"Light Color (1-12)",
                    required: true,
                    defaultValue: "1"
            )
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

void updateState(String status, String label) {
    device.name = label
    sendEvent(name: "switch", value: (status == "1" ? "on" : "off"))
}

void setAuxNumber(String auxNumber) {
    state.auxNumber = auxNumber
}

void SetColor(String Color) {
    if (Color == "0") {
        debugLog("Ignoring attempt to SetColor of ${device.name} to the off color")
        return // setting the "on" color to off would just lead to confusion
    }

    infoLog("Setting Color of ${device.name} to ${Color} and turning on")
    device.updateSetting("color", [value: Color, type: "string"])
    parent.doCommand(msg, "set_light", ["aux": state.auxNumber, "light": Color])
    sendEvent(name: "switch", value: "on")
    parent.sendEvent(name: "aux${state.auxNumber}", value: "on")
}

void on() {
    if (device.currentValue('switch') == 'off') {
        def msg ="Turning On ${device.name} with Color ${settings.color}"
        infoLog(msg)
        parent.doCommand(msg, "set_light", ["aux": state.auxNumber, "light": settings.color])
        sendEvent(name: "switch", value: "on")
        parent.sendEvent(name: "aux${state.auxNumber}", value: "on")
    } else {
        debugLog("${device.name} Already On")
    }
}

void off() {
    if (device.currentValue('switch') == 'on') {
        def msg ="Turning Off ${device.name}"
        infoLog(msg)
        parent.doCommand(msg, "set_light", ["aux": state.auxNumber, "light": "0"])
        sendEvent(name: "switch", value: "off")
        parent.sendEvent(name: "aux${state.auxNumber}", value: "off")
    } else {
        debugLog("${device.name} Already Off")
    }
}

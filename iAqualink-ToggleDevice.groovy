metadata {
    definition(
            name: "iAqualink ToggleDevice",
            namespace: "iAqualink",
            author: "Vyrolan",
            importUrl: "https://raw.githubusercontent.com/Vyrolan/iAqualink-Hubitat/main/iAqualink-ToggleDevice.groovy"
    ) {
        capability "Switch"
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

void updateState(String status, String label) {
    device.name = label
    sendEvent(name: "switch", value: (status == "1" ? "on" : "off"))
}

void setCommand(String command, String parentAttr) {
    state.command = command
    state.parentAttr = parentAttr
}

void on() {
    if (device.currentValue('switch') == 'off') {
        def msg ="Turning On ${device.name}"
        infoLog(msg)
        parent.doCommand(msg, state.command)
        sendEvent(name: "switch", value: "on")
        parent.sendEvent(name: state.parentAttr, value: "on")
    } else {
        debugLog("${device.name} Already On")
    }
}

void off() {
    if (device.currentValue('switch') == 'on') {
        def msg ="Turning Off ${device.name}"
        infoLog(msg)
        parent.doCommand(msg, state.command)
        sendEvent(name: "switch", value: "off")
        parent.sendEvent(name: state.parentAttr, value: "off")
    } else {
        debugLog("${device.name} Already Off")
    }
}

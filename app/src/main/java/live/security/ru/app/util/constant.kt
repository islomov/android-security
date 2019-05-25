package ru.security.live.util

/**
 * @author sardor
 */
const val INTENT_DESKTOP_POSITION = "INTENT_DESKTOP_POSITION" // String "position"
const val INTENT_ARCHIVE_INFO = "INTENT_ARCHIVE_INFO" // Serializable ArchiveItem
const val INTENT_ARCHIVE_IS_EVENT = "INTENT_ARCHIVE_IS_EVENT" // Boolean isEvent
const val INTENT_CAMERA_INFO = "INTENT_CAMERA_INFO" // Serializable CameraFull
const val INTENT_FROM_CAMERA = "INTENT_FROM_CAMERA" // Boolean fromCamera
const val INTENT_FROM_EVENT = "INTENT_FROM_EVENT" // Boolean fromCamera
const val INTENT_FROM_ARCHIVE = "INTENT_FROM_ARCHIVE" // Boolean "fromArchive"
const val INTENT_ARCHIVE_CAMERA_URL = "INTENT_ARCHIVE_CAMERA_URL" // String cameraUrl
const val INTENT_EVENT_PARAMS = "INTENT_EVENT_PARAMS" // Custom user event, see AddEventActivity.Params
const val INTENT_VIDEO_TYPE = "INTENT_VIDEO_TYPE" // Desktop or camera
const val INTENT_MAP_LAT = "INTENT_MAP_LAT" // Default center latitude for map
const val INTENT_MAP_LON = "INTENT_MAP_LON"
const val INTENT_VIDEO_FROM_MAP = "INTENT_VIDEO_FROM_MAP"// Default center longitude for map

const val INTENT_IS_DEFAULT = "INTENT_IS_DEFAULT"
const val INTENT_FILTER_FROM = "INTENT_FILTER_FROM"
const val INTENT_FILTER_TILL = "INTENT_FILTER_TILL"
const val INTENT_FILTER_SOURCES = "INTENT_FILTER_SOURCES"
const val INTENT_FILTER_EVENT_TYPES = "INTENT_FILTER_EVENT_TYPES"
const val INTENT_FILTER_LOCATIONS = "INTENT_FILTER_LOCATIONS"
const val INTENT_FILTER_LOCATION = "INTENT_FILTER_LOCATION"
const val INTENT_EVENT_FROM_MAP = "INTENT_EVENT_FROM_MAP"
const val INTENT_FILTER_BUILDINGS = "INTENT_FILTER_BUILDINGS"
const val INTENT_TRANSITION_NAME = "INTENT_TRANSITION_NAME"
const val INTENT_BITMAP = "INTENT_BITMAP"
const val INTENT_RTMP = "INTENT_RTMP"

const val REQUEST_CODE_FILTER = 645

const val EVENTS_PER_PAGE = 20
const val ACCOUNT_TYPE_LOGIN = "1"
const val ACCOUNT_TYPE_LDAP = "2"
const val INVALID_TOKEN = ""
const val EXTRA_EVENT_ID = "eventId"

const val licenseUrl = "http://www.head-point.ru/media/cms_page_media/51/Licenzionnye%20usloviya%20ispolzovaniya%20HeadPoint.pdf"
const val companyUrl = "http://www.head-point.ru/ru/"
const val phoneNumber = "+7495775–01–63"
const val supportEmail = "support@head-point.ru"

const val allChecked = 100
const val noneChecked = 0
const val someChecked = 50

const val TYPE_CODE_CAMERA = "camera"
const val TYPE_CODE_CONTROLLER = "controller"
const val TYPE_CODE_SENSOR = "sensor"
const val TYPE_CODE_BUILDING = "building"
const val TYPE_CODE_LIST = "list"

class StatusMap() {
    companion object {
        val map = HashMap<String, String>()

        fun init() {
            map["InUse"] = "В эксплуатации"
            map["Defective"] = "Неисправно"
            map["OnService"] = "На тех.обслуживании"
            map["Control"] = "На контроле"
            map["Draft"] = "Черновик"
            map["Registration"] = "Регистрация на видеосервере"
            map["RegistrationError"] = "Ошибка регистрации"
            map["NotExist"] = "Не существует"
            map["Deleted"] = "Удалено"
            map["Decommissioned"] = "Выведено из эксплуатации"
            map[""] = "Не существует"
        }
    }
}

var PERMISSION_ALL = 1
var PERMISSIONS = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

const val permissionCodeEvents = "Events"
const val permissionCodeVideomonitoring = "Videomonitoring"
const val permissionCodeMaps = "Maps"

const val eventTypeDefault = "default"
const val eventTypeUserCreate = "user-create-event"
const val eventTypeEventCount = "money_bill_counter-event-count"
const val eventTypeEventMix = "money_bill_counter-event-mix"
const val eventTypeEventSingle = "money_bill_counter-event-single"
const val eventTypeEventOrientation = "money_bill_counter-event-orientation"
const val eventTypeEventFade = "money_bill_counter-event-fade"
const val eventTypeEventCounterError = "money_bill_counter-event-error"
const val eventTypeGRNRecognized = "grn-recognized"
const val eventTypFaceCaptured = "face-captured"
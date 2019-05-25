package ru.security.live.data

import io.reactivex.Single
import ru.security.live.data.net.ApiHolder
import ru.security.live.data.net.response.DeviceResponse
import ru.security.live.domain.entity.DeviceItem
import ru.security.live.domain.entity.FolderItem
import ru.security.live.domain.repository.IDeviceListRepository
import ru.security.live.presentation.view.ui.adapters.with_delegate.DelegateViewType
/**
 * @author sardor
 */
object DeviceListRepository : IDeviceListRepository {

    //--Здесь рабочие методы

    /*override fun getDevListStructure(): Single< Pair< ArrayList<DelegateViewType>,
            ArrayList<DelegateViewType> > >{
        rootDevList.clear()
        fullDeviceList.clear()
        val listItems = listOf(
                FolderItem(
                        "section5",
                        arrayListOf(DeviceItem("string3"),
                                DeviceItem("string4"))),
                DeviceItem(
                        "string3"),
                DeviceItem(
                        "string4"),
                FolderItem(
                        "sectio1",
                        arrayListOf(FolderItem(
                                "section5",
                                arrayListOf(DeviceItem("string3"),
                                        DeviceItem("string4"))))),
                DeviceItem(
                        "string9"),
                DeviceItem(
                        "string")
        )
        val structure = JsonParser().parse(Gson().toJson(listItems)).asJsonArray
//        return Single.just(JsonArray())
        recursiveParse(structure, true)
        return Single.just(Pair(rootDevList, fullDeviceList))
    }

    private val fullDeviceList = ArrayList<DelegateViewType>()
    private val rootDevList = ArrayList<DelegateViewType>()
    private fun recursiveParse(arr: JsonArray, isFirst: Boolean = false): ArrayList<DelegateViewType> {
        return ArrayList(
                arr.map {
                    it as JsonObject
                    val e: DelegateViewType =
                    if(it.has("nested"))
                        FolderItem(it["name"].asString, recursiveParse(it["nested"].asJsonArray))
                    else DeviceItem(it["name"].asString)
                    fullDeviceList.add(e)
                    if(isFirst) rootDevList.add(e)
                    e
        })
    }*/

    override fun getDevListStructure(): Single<Pair<ArrayList<DelegateViewType>,
            ArrayList<DelegateViewType>>> {
        rootDevList.clear()
        fullDeviceList.clear()
//        recursiveParse(ApiHolder.privateApi.getRootDeviceList().blockingGet(), true)
//        return Single.just(Pair(rootDevList, fullDeviceList))
        return ApiHolder.privateApi.getRootDeviceList()
                .map {
                    val rootDevList = recursiveParse(it, true)
                    Pair(rootDevList, fullDeviceList)
                }
    }

    private fun childDevList(parentId: String): Single<DeviceResponse> = ApiHolder.privateApi.getChildDeviceList(parentId)

    private val fullDeviceList = ArrayList<DelegateViewType>()
    private val rootDevList = ArrayList<DelegateViewType>()
    private fun recursiveParse(element: DeviceResponse, isFirst: Boolean = false): ArrayList<DelegateViewType> {
        return ArrayList(
                element.items.map {
                    val e: DelegateViewType =
                            if (it.hasChildren)
                                FolderItem(it.name, recursiveParse(childDevList(it.id).blockingGet()))//блокинг
                            else DeviceItem(it.id, it.name, it.type)
                    fullDeviceList.add(e)
                    if (isFirst) rootDevList.add(e)
                    e
                })
    }


}
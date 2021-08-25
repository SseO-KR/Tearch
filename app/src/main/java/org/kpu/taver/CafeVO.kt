package org.kpu.taver

import java.io.Serializable

class CafeVO (
    var allTable:Int,
    var cafeName:String,
    var latitude:Double,
    var longitude:Double,
    var peopleNum:Int
    ) : Serializable
{

    constructor() : this(0, "", 0.0, 0.0 , 0)
}
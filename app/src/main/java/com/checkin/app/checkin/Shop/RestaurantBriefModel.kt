package com.checkin.app.checkin.Shop

import com.checkin.app.checkin.Misc.BriefModel

class RestaurantBriefModel : BriefModel() {
    fun formatRestaurantName() = "Live at <font color=#0295aa>$displayName</font>"
}
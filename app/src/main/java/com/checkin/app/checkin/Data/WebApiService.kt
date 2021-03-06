package com.checkin.app.checkin.Data

import com.checkin.app.checkin.Account.AccountModel
import com.checkin.app.checkin.Auth.AuthResultModel
import com.checkin.app.checkin.Cook.Model.CookTableModel
import com.checkin.app.checkin.Inventory.Model.InventoryAvailabilityModel
import com.checkin.app.checkin.Inventory.Model.InventoryModel
import com.checkin.app.checkin.Manager.Model.ManagerSessionEventModel
import com.checkin.app.checkin.Manager.Model.ManagerSessionInvoiceModel
import com.checkin.app.checkin.Manager.Model.ManagerStatsModel
import com.checkin.app.checkin.Menu.Model.MenuModel
import com.checkin.app.checkin.Menu.Model.OrderedItemModel
import com.checkin.app.checkin.Misc.GenericDetailModel
import com.checkin.app.checkin.Misc.paytm.PaytmModel
import com.checkin.app.checkin.Search.SearchResultPeopleModel
import com.checkin.app.checkin.Search.SearchResultShopModel
import com.checkin.app.checkin.Shop.Private.Finance.FinanceModel
import com.checkin.app.checkin.Shop.Private.Insight.ShopInsightLoyaltyProgramModel
import com.checkin.app.checkin.Shop.Private.Insight.ShopInsightRevenueModel
import com.checkin.app.checkin.Shop.Private.Invoice.RestaurantAdminStatsModel
import com.checkin.app.checkin.Shop.Private.Invoice.RestaurantSessionModel
import com.checkin.app.checkin.Shop.Private.Invoice.ShopSessionDetailModel
import com.checkin.app.checkin.Shop.Private.Invoice.ShopSessionFeedbackModel
import com.checkin.app.checkin.Shop.Private.MemberModel
import com.checkin.app.checkin.Shop.RestaurantModel
import com.checkin.app.checkin.Shop.ShopJoin.ShopJoinModel
import com.checkin.app.checkin.User.ShopCustomerModel
import com.checkin.app.checkin.User.UserModel
import com.checkin.app.checkin.User.bills.NewReviewModel
import com.checkin.app.checkin.User.bills.UserTransactionBriefModel
import com.checkin.app.checkin.User.bills.UserTransactionDetailsModel
import com.checkin.app.checkin.Waiter.Model.*
import com.checkin.app.checkin.session.activesession.chat.SessionChatModel
import com.checkin.app.checkin.session.model.*
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface WebApiService {
    // region USER
    // region GET
    @get:GET("users/")
    val users: Call<List<UserModel>>

    @get:GET("users/self/")
    val personalUser: Call<UserModel>

    @GET("users/{user_pk}/")
    fun getNonPersonalUser(@Path("user_pk") userPk: Long): Call<UserModel>
    // endregion

    @PATCH("users/self/")
    fun postUserData(@Body objectNode: ObjectNode): Call<UserModel>

    @Multipart
    @POST("users/self/picture/")
    fun postUserProfilePic(@Part pic: MultipartBody.Part): Call<GenericDetailModel>
    // endregion

    // region SESSION
    @POST("sessions/customer/new/")
    fun postNewCustomerSession(@Body data: ObjectNode): Call<QRResultModel>

    @POST("/sessions/active/concern/")
    fun postOrderConcern(@Body data: ObjectNode): Call<ObjectNode>

    @POST("sessions/active/customers/")
    fun postActiveSessionCustomers(@Body data: ObjectNode): Call<ObjectNode>

    @PUT("sessions/active/customers/self/")
    fun putActiveSessionSelfCustomer(@Body data: ObjectNode): Call<ObjectNode>

    @POST("sessions/active/customers/{user_id}/")
    fun postActiveSessionCustomerRequest(@Path("user_id") userId: String): Call<GenericDetailModel>

    @DELETE("sessions/active/customers/{user_id}/")
    fun deleteActiveSessionCustomer(@Path("user_id") userId: String): Call<GenericDetailModel>

    @POST("sessions/active/message/")
    fun postCustomerMessage(@Body data: ObjectNode): Call<ObjectNode>

    @POST("sessions/active/request/service/")
    fun postCustomerRequestService(@Body data: ObjectNode): Call<ObjectNode>

    @POST("sessions/active/request/checkout/")
    fun postCustomerRequestCheckout(@Body data: ObjectNode): Call<CheckoutStatusModel>

    @get:GET("sessions/active/check/")
    val activeSessionCheck: Call<SessionBasicModel>

    @get:GET("sessions/active/")
    val activeSession: Call<ActiveSessionModel>

    @get:GET("sessions/active/events/")
    val customerSessionChat: Call<List<SessionChatModel>>

    @get:GET("sessions/active/invoice/")
    val activeSessionInvoice: Call<SessionInvoiceModel>

    @get:GET("sessions/waiter/tables/active/")
    val waiterServedTables: Call<List<WaiterTableModel>>

    @get:GET("sessions/active/promo/")
    val sessionAppliedPromo: Call<SessionPromoModel>

    @get:GET("sessions/active/orders/")
    val activeSessionOrders: Call<List<SessionOrderedItemModel>>

    @GET("sessions/{session_id}/brief/")
    fun getUserSessionBrief(@Path("session_id") sessionId: Long): Call<UserTransactionBriefModel>

    @GET("sessions/{session_id}/detail/")
    fun getUserSessionDetailById(@Path("session_id") sessionId: Long): Call<UserTransactionDetailsModel>

    @get:GET("sessions/recent/users/self/")
    val userRecentCheckins: Call<List<ShopCustomerModel>>

    @POST("sessions/active/pay/paytm/")
    fun postPaytmRequest(): Call<PaytmModel>

    // endregion

    // region MISC
    @get:GET("accounts/self/")
    val selfAccounts: Call<List<AccountModel>>

    @GET("search/people/")
    fun getSearchPeopleResults(
            @Query("search") query: String?, @Query("friendship_status") friendshipStatus: String?): Call<List<SearchResultPeopleModel>>

    @GET("search/restaurant/")
    fun getSearchShopResults(@Query("search") query: String?, @Query("has_nonveg") hasNonVeg: Boolean?, @Query("has_alcohol") hasAlcohol: Boolean?): Call<List<SearchResultShopModel>>
    // endregion

    // region promos
    @get:GET("promos/")
    val promoCodes: Call<List<PromoDetailModel>>

    @GET("promos/active/restaurants/{restaurant_id}/")
    fun getRestaurantActivePromos(@Path("restaurant_id") restaurantId: Long): Call<List<PromoDetailModel>>
    // endregion

    // region AUTH
    @POST("auth/login/")
    fun postLogin(@Body credentials: ObjectNode): Call<AuthResultModel>

    @POST("auth/register/")
    fun postRegister(@Body credentials: ObjectNode): Call<AuthResultModel>

    @PUT("auth/devices/self/update/")
    fun postFCMToken(@Body tokenData: ObjectNode): Call<ObjectNode>
    // endregion

    // region SHOP
    @get:GET("restaurants/")
    val restaurants: Call<List<RestaurantModel>>

    @POST("restaurants/create/")
    fun postRegisterShop(@Body model: ShopJoinModel): Call<GenericDetailModel>

    @GET("restaurants/{shop_id}/")
    fun getRestaurantDetails(@Path("shop_id") shopId: Long): Call<RestaurantModel>

    @Multipart
    @POST("restaurants/{shop_id}/logo/")
    fun postRestaurantLogo(
            @Path("shop_id") shopId: Long, @Part pic: MultipartBody.Part): Call<GenericDetailModel>

    @Multipart
    @POST("restaurants/{shop_id}/covers/{index}/")
    fun postRestaurantCover(
            @Path("shop_id") shopId: Long, @Path("index") index: Int, @Part pic: MultipartBody.Part): Call<GenericDetailModel>

    @DELETE("restaurants/{shop_id}/covers/{index}/")
    fun deleteRestaurantCover(@Path("shop_id") shopId: Long, @Path("index") index: Int): Call<ObjectNode>

    @GET("restaurants/{shop_id}/edit/")
    fun getRestaurantManageDetails(@Path("shop_id") shopId: Long): Call<RestaurantModel>

    @PATCH("restaurants/{shop_id}/edit/")
    fun putRestaurantManageDetails(@Path("shop_id") shopId: Long, @Body shopData: RestaurantModel): Call<ObjectNode>

    @PUT("restaurants/{shop_id}/verify/")
    fun putRestaurantContactVerify(@Path("shop_id") shopId: Long, @Body data: ObjectNode): Call<ObjectNode>

    @GET("/restaurants/{restaurant_id}/insights/revenue/")
    fun getShopInsightRevenueDetail(@Path("restaurant_id") shopId: Long): Call<ShopInsightRevenueModel>

    @GET("/restaurants/{restaurant_id}/insights/loyalty/")
    fun getShopInsightLoyaltyDetail(@Path("restaurant_id") shopId: Long): Call<ShopInsightLoyaltyProgramModel>

    // endregion

    // region SHOP_MEMBERS
    @GET("restaurants/{shop_id}/members/")
    fun getRestaurantMembers(@Path("shop_id") shopId: Long): Call<List<MemberModel>>

    @POST("restaurants/{shop_id}/members/")
    fun postRestaurantMember(@Path("shop_id") shopId: Long, @Body data: MemberModel): Call<ObjectNode>

    @PUT("restaurants/{shop_id}/members/{user_id}/")
    fun putRestaurantMember(@Path("shop_id") shopId: Long, @Path("user_id") userId: Long, @Body data: MemberModel): Call<ObjectNode>

    @DELETE("restaurants/{shop_id}/members/{user_id}/")
    fun deleteRestaurantMember(@Path("shop_id") shopId: Long, @Path("user_id") userId: Long): Call<ObjectNode>

    @GET("sessions/restaurants/{restaurant_id}/")
    fun getRestaurantSessionsById(@Path("restaurant_id") restaurantId: Long, @Query("checked_in_after") checkedOutAfter: String?, @Query("checked_in_before") checkedOutBefore: String?): Call<List<RestaurantSessionModel>>

    @GET("restaurants/{restaurant_id}/stats/admin")
    fun getRestaurantAdminStats(@Path("restaurant_id") restaurantId: Long, @Query("checked_in_after") checkedOutAfter: String?, @Query("checked_in_before") checkedOutBefore: String?): Call<RestaurantAdminStatsModel>

    @GET("sessions/manage/{session_id}/detail/")
    fun getShopSessionDetailById(@Path("session_id") sessionId: Long): Call<ShopSessionDetailModel>

    @GET("restaurants/{restaurant_id}/finance/")
    fun getRestaurantFinanceById(@Path("restaurant_id") restaurantId: Long): Call<FinanceModel>

    @POST("sessions/manage/{session_id}/contacts/")
    fun postSessionContact(@Path("session_id") sessionId: Long, @Body data: SessionContactModel): Call<ObjectNode>

    @GET("sessions/manage/{session_id}/contacts/")
    fun getSessionContactList(@Path("session_id") sessionId: Long): Call<List<SessionContactModel>>

    @PUT("restaurants/{restaurant_id}/finance/")
    fun setRestaurantFinanceById(@Body financeModel: FinanceModel, @Path("restaurant_id") restaurantId: Long): Call<GenericDetailModel>

    @GET("sessions/manage/{session_id}/brief/")
    fun getSessionBriefDetail(@Path("session_id") sessionId: Long): Call<SessionBriefModel>

    @GET("sessions/{session_id}/orders/")
    fun getSessionOrders(@Path("session_id") sessionId: Long): Call<List<SessionOrderedItemModel>>
    // endregion

    // region COOK

    @GET("sessions/restaurants/{restaurant_id}/active/")
    fun getCookActiveTables(@Path("restaurant_id") restaurantId: Long): Call<List<CookTableModel>>

    // endregion

    // region WAITER

    @POST("sessions/waiter/new/")
    fun postNewWaiterSession(@Body data: ObjectNode): Call<QRResultModel>

    @GET("restaurants/{restaurant_id}/tables/")
    fun getRestaurantTables(@Path("restaurant_id") restaurantId: Long, @Query("active") query: Boolean): Call<List<RestaurantTableModel>>

    @GET("sessions/{session_id}/events/waiter/")
    fun getWaiterSessionEvents(@Path("session_id") sessionId: Long): Call<List<WaiterEventModel>>

    @POST("sessions/manage/orders/{order_id}/status/")
    fun postChangeOrderStatus(@Path("order_id") orderId: Long, @Body data: ObjectNode): Call<OrderStatusModel>

    @POST("sessions/{session_id}/manage/orders/status/")
    fun postChangeOrderStatusList(@Path("session_id") sessionId: Long, @Body msOrderStatus: List<OrderStatusModel>): Call<List<OrderStatusModel>>

    @PUT("sessions/manage/events/{event_id}/done/")
    fun putSessionEventDone(@Path("event_id") eventId: Long): Call<GenericDetailModel>

    @GET("restaurants/{restaurant_id}/stats/waiter/")
    fun getRestaurantWaiterStats(@Path("restaurant_id") restaurantId: Long): Call<WaiterStatsModel>

    @POST("sessions/{session_id}/request/checkout/")
    fun postSessionRequestCheckout(@Path("session_id") sessionId: Long, @Body data: ObjectNode): Call<CheckoutStatusModel>

    // endregion

    // region MANAGER

    @GET("restaurants/{restaurant_id}/stats/manager/")
    fun getRestaurantManagerStats(@Path("restaurant_id") restaurantId: Long): Call<ManagerStatsModel>

    @GET("sessions/{session_id}/events/manager/")
    fun getManagerSessionEvents(@Path("session_id") sessionId: Long): Call<List<ManagerSessionEventModel>>

    @GET("sessions/{session_id}/manage/bill/")
    fun getManagerSessionInvoice(@Path("session_id") sessionId: Long): Call<ManagerSessionInvoiceModel>

    @PUT("sessions/{session_id}/manage/bill/")
    fun putManageSessionBill(@Path("session_id") sessionId: Long, @Body data: ObjectNode): Call<GenericDetailModel>

    @POST("sessions/manage/{session_id}/checkout/")
    fun putSessionCheckout(@Path("session_id") sessionId: Long): Call<CheckoutStatusModel>

    @POST("sessions/manage/new/")
    fun postManageInitiateSession(@Body data: ObjectNode): Call<QRResultModel>

    @POST("sessions/active/promos/avail/")
    fun postAvailPromoCode(@Body data: ObjectNode): Call<SessionPromoModel>

    @POST("sessions/manage/{session_id}/table/change/")
    fun postTableSwitch(@Path("session_id") sessionId: Long, @Body data: ObjectNode): Call<ObjectNode>

    @DELETE("sessions/active/promos/remove/")
    fun deletePromoCode(): Call<ObjectNode>

    @DELETE("sessions/active/cancel/checkout/")
    fun deleteCheckout(): Call<ObjectNode>

    @DELETE("sessions/customer/cancel/")
    fun deleteCustomerSessionJoinRequest(): Call<ObjectNode>

    // endregion

    // region MENU
    @GET("menus/restaurants/{shop_id}/available/")
    fun getAvailableMenu(@Path("shop_id") shopId: Long): Call<MenuModel>

    @POST("sessions/active/order/")
    fun postActiveSessionOrders(@Body orderedItemModels: List<OrderedItemModel>): Call<ArrayNode>

    @DELETE("sessions/active/orders/{order_id}/")
    fun deleteSessionOrder(@Path("order_id") order_id: Long): Call<ObjectNode>

    @POST("sessions/{session_id}/manage/order/")
    fun postSessionManagerOrders(@Path("session_id") sessionId: Long, @Body orderedItemModels: List<OrderedItemModel>): Call<ArrayNode>

    @GET("menus/restaurants/{restaurant_id}/manage/available/")
    fun getAvailableRestaurantMenu(@Path("restaurant_id") restaurantId: Long): Call<InventoryModel>

    @GET("menus/items/trending/restaurants/{restaurant_id}/")
    fun getRestaurantTrendingItem(@Path("restaurant_id") restaurantId: Long): Call<List<TrendingDishModel>>

    @GET("menus/items/trending/restaurants/{restaurant_id}/")
    fun getRestaurantRecommendedItems(@Path("restaurant_id") restaurantId: Long): Call<List<TrendingDishModel>>

    @POST("menus/restaurants/{restaurant_id}/manage/items/")
    fun postChangeMenuAvailability(@Path("restaurant_id") restaurantId: Long, @Body msOrderStatus: List<InventoryAvailabilityModel>): Call<List<InventoryAvailabilityModel>>

    // endregion

    // region payments
    @POST("payments/callback/paytm/")
    fun postPaytmCallback(@Body data: ObjectNode): Call<ObjectNode>
    //endregion

    //region Review

    @GET("reviews/sessions/{session_id}/")
    fun getRestaurantSessionReviews(@Path("session_id") sessionId: Long): Call<List<ShopSessionFeedbackModel>>

    @POST("reviews/sessions/{session_id}/")
    fun postCustomerReview(@Path("session_id") sessionId: Long, @Body review: NewReviewModel): Call<ObjectNode>

    // endregion
}

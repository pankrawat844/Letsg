package come.texi.driver.utils

/**
 * Created by techintegrity on 04/07/16.
 */
object Url {
    var URL = "https://digitaldwarka.com/taxiapp/"

    var baseUrl = URL+"web_service/"
    //public static String baseUrl = "http://v1technology.co.uk/demo/naqil/naqilcom/Source/web_service/";
    var signupUrl = baseUrl + "sign_up"
    //public static String signupUrl = "http://138.68.5.43/test.php";
    var loginUrl = baseUrl + "login"
    var forgotPasswordUrl = baseUrl + "forgot_password"
    var facebookLoginUrl = baseUrl + "facebook_login"
    var twitterLoginUrl = baseUrl + "twitter_login"
    var bookCabUrl = baseUrl + "book_cab"
    var loadTripsUrl = baseUrl + "load_trips"
    var loadTripsFiltersUrl = baseUrl + "filter_book"
    var profileUrl = baseUrl + "profile_edit"
    var changePasswordUrl = baseUrl + "change_password"
    var deleteCabUrl = baseUrl + "user_reject_trip"
    var FixAreaUrl = baseUrl + "fix_area_list"

    var userImageUrl = URL+"user_image/"
    var carImageUrl = URL+"car_image/"
    var DriverImageUrl = URL+"driverimages/"

    var socketUrl = "http://162.243.225.225:4040"

    var subscribeUrl = "http://162.243.225.225:8001/subscribe"
    var unsubscribeUrl = "http://162.243.225.225:8001/unsubscribe"

    var FacebookImgUrl = "https://graph.facebook.com/"

    var SocialShareUrl = "http://digitaldwarka.com/taxiapp/web_service/share-page"
    var AppLogUrl = "http://digitaldwarka.com/taxiapp/application/views/img/app-logo-new.png"
}

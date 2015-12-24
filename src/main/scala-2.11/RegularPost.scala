/**
 * Created by sharique on 11/18/2015.
 */
class RegularPost(val _post:String,val _postedBy:String, val _key:String) {
  val postedBy=_postedBy
  val post:String = _post
  var secretKey: String = _key
}

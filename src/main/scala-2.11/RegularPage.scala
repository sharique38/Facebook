import scala.collection.mutable.ArrayBuffer

/**
 * Created by sharique on 11/18/2015.
 */
class RegularPage(val _name:String) {
  val name = _name
  var postsInPage:ArrayBuffer[RegularPost] = ArrayBuffer[RegularPost]()
  var LikeListofUsers:scala.collection.mutable.HashMap[String, Int] = scala.collection.mutable.HashMap[String, Int]()
  def AddPostinPage(_post:RegularPost)={
    postsInPage += _post
  }

  def Addlike(_userid:String): Unit =
  {
    LikeListofUsers += (_userid -> _userid.toInt)

  }

  def Unlike(_userid:String): Unit =
  {
    LikeListofUsers.remove(_userid)

  }
}

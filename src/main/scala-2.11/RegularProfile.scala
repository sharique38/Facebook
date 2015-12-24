import scala.collection.mutable.ArrayBuffer

/**
 * Created by sharique on 11/18/2015.
 */
class RegularProfile(val _id:Int) {
  val id = _id
  var postsInProfile:ArrayBuffer[RegularPost] = ArrayBuffer[RegularPost]()
  def AddPostinProfile(_post:RegularPost)={
    postsInProfile += _post
  }
  def getPostinProfile(): ArrayBuffer[RegularPost] ={    
    return postsInProfile;
  }

}

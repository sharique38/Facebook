import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import javax.crypto.SecretKey

import scala.collection.mutable.ArrayBuffer

/**
 * Created by sharique on 11/18/2015.
 */
class RegularUser(val _id: Int, val _name: String) {
  val id: Int = _id
  var name: String = _name
  var publicKey: PublicKey = null
  var secretKey: String = null
  // stored as encrypted by public key in base64 string format
  var tempNonce: Array[Byte] = Array[Byte]()
  //var gender:String
  val profile: RegularProfile = new RegularProfile(_id)
  var pages: ArrayBuffer[RegularPage] = ArrayBuffer[RegularPage]()
  var albums: ArrayBuffer[RegularAlbum] = ArrayBuffer[RegularAlbum]()
  var friendList: ArrayBuffer[RegularUser] = ArrayBuffer[RegularUser]()
  var albumNameIDmap: scala.collection.mutable.HashMap[String, Int] = scala.collection.mutable.HashMap[String, Int]()

  //name vs index
  def addPage(_page: RegularPage): Unit = {
    pages += _page
  }

  def addAlbum(_album: RegularAlbum): Unit = {
    albums += _album
    albumNameIDmap += (_album.album_name -> (albums.size - 1))
  }

  def addFriend(_friend: RegularUser): Unit = {
    friendList += _friend
  }

  def deletePage(_page: RegularPage): Unit = {
    pages -= _page
  }

  def deleteAlbum(_album: RegularAlbum): Unit = {
    albums -= _album
  }

  def deleteFriend(_friend: RegularUser): Unit = {
    if (friendList.contains(_friend))
      friendList -= _friend
  }

  //def FriendCheck
}

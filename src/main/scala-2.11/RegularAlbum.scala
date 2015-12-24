import scala.collection.mutable.ArrayBuffer

/**
 * Created by sharique on 11/18/2015.
 */
class RegularAlbum(val _str:String) {
  var album_name:String = _str
  var album:ArrayBuffer[RegularPicture] = ArrayBuffer[RegularPicture]()
  def AddPictureInAlbum(_pic:RegularPicture) = {
    album += _pic
  }
  def GetPictureInAlbum(index:Int):RegularPicture = {
    return album(index)
  }
  def GetSize(): Int =
  {
    return album.size
  }
}

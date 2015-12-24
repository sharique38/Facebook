/**
 * Created by sharique on 11/28/2015.
 */
class A(s:String){
  var str:String = s;
  def update(){
    str += "sharique"
    println(s)
  }
}
object Test {
  def main(args: Array[String]) = {
    var allPagesMap:scala.collection.mutable.HashMap[String, RegularPage] = scala.collection.mutable.HashMap[String, RegularPage]()
    var s:String = "var"
    var a:A = new A(s);
    a.update()
    println(s)
  }
}

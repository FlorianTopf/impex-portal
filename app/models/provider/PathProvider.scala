package models.provider

object PathProvider {
  def getFileName(path: String): String = {
    path.split("/").last
  }
  
  def getTreePath(path: String, folder: String): String = {
    "trees/"+folder+"/"+getFileName(path)
  }
  
}
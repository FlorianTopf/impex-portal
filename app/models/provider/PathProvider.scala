package models.provider

//@TODO add FileNotFoundException handling here (if directory doesn't exist)
object PathProvider {
  def getPath(folder: String, dbname: String, path: String): String = {
    var name = path.split("/").last
    if(name.contains("?") || !name.toLowerCase().contains(dbname.toLowerCase())) {
      if(folder == "trees")
        name = folder.substring(0, folder.length()-1)+"_"+dbname+".xml"
      else if(folder == "methods")
        name = folder+"_"+dbname+".wsdl"
    }
    folder+"/"+dbname+"/"+name
  }
  
}
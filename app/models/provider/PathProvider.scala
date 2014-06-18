package models.provider

//@TODO add FileNotFoundException handling here (if directory doesn't exist)
object PathProvider {
  def getPath(folder: String, dbname: String, path: String): String = {
    var name = path.split("/").last	
    // just check if there is a GET route in methods
    if(name.contains("?") && (folder == "methods"))
        name = dbname.split("_").last+"_"+folder+".wsdl"    
    folder+"/"+dbname+"/"+name
  }
  
}
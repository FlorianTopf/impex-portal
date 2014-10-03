// Generated by <a href="http://scalaxb.org/">scalaxb</a>.
package models.binding


trait Methods_AMDA {
  def isAlive(): Either[scalaxb.Soap11Fault[Any], Boolean]
  def getObsDataTree(): Either[scalaxb.Soap11Fault[Any], models.binding.GetObsDataTreeResponseAMDA]
  def getTimeTablesList(userID: Option[String], password: Option[String]): Either[scalaxb.Soap11Fault[Any], models.binding.GetTimeTablesListResponse]
  def getParameterList(userID: String, password: Option[String]): Either[scalaxb.Soap11Fault[Any], models.binding.GetParameterListResponse]
  def getParameter(startTime: String, stopTime: String, parameterID: String, sampling: Option[Float], userID: Option[String], password: Option[String], outputFormat: Option[models.binding.OutputFormat], timeFormat: Option[models.binding.TimeFormat], gzip: Option[BigInt]): Either[scalaxb.Soap11Fault[Any], models.binding.GetParameterResponse]
  def getDataset(startTime: String, stopTime: String, datasetID: String, sampling: Option[Float], userID: Option[String], password: Option[String], outputFormat: Option[models.binding.OutputFormat], timeFormat: Option[models.binding.TimeFormat], gzip: Option[BigInt]): Either[scalaxb.Soap11Fault[Any], models.binding.GetDatasetResponse]
  def getOrbites(startTime: String, stopTime: String, spacecraft: models.binding.EnumSpacecraft, coordinateSystem: models.binding.EnumCoordinateSystemType, units: Option[models.binding.Units], sampling: Option[Float], userID: Option[String], password: Option[String], outputFormat: Option[models.binding.OutputFormat], timeFormat: Option[models.binding.TimeFormat], gzip: Option[BigInt]): Either[scalaxb.Soap11Fault[Any], models.binding.GetOrbitesResponse]
  def getTimeTable(userID: Option[String], password: Option[String], ttID: String): Either[scalaxb.Soap11Fault[Any], models.binding.GetTimeTableResponse]
}





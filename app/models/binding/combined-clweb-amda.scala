// Generated by <a href="http://scalaxb.org/">scalaxb</a>.
package models.binding


case class DataRoot(dataCenter: models.binding.DataCenter)


case class DataCenter(mission: Seq[models.binding.Mission] = Nil,
  desc: String,
  name: String,
  id: String)


case class Mission(instrument: Seq[models.binding.Instrument] = Nil,
  att: Option[String] = None,
  available: Option[String] = None,
  desc: String,
  name: String,
  target: Option[String] = None,
  id: String)


case class Instrument(dataset: Seq[models.binding.Dataset] = Nil,
  att: Option[String] = None,
  desc: String,
  name: String,
  id: String)


case class Dataset(parameter: Seq[models.binding.Parameter] = Nil,
  att: Option[String] = None,
  dataSource: Option[String] = None,
  dataStart: String,
  dataStop: String,
  maxSampling: Option[String] = None,
  name: String,
  sampling: Option[String] = None,
  target: Option[String] = None,
  id: String)


case class Parameter(component: Seq[models.binding.Component] = Nil,
  desc: Option[String] = None,
  display_type: Option[String] = None,
  name: String,
  units: String,
  varValue: Option[String] = None,
  id: String)


case class Component(name: String,
  varValue: Option[String] = None,
  id: String)


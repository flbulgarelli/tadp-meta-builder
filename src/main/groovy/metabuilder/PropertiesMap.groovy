package metabuilder

import groovy.lang.Closure
import groovy.transform.TupleConstructor

@TupleConstructor
class PropertiesMap {
  //TODO add support for restrictions
  //TODO reify property objects
  Map properties

  def set(name, value) {
    properties[name] = value
  }

  def add(name, value) {
    get(name) << value //FIXME
  }

  def get(name) {
    _eval(properties[name])
  }

  def values() {
    properties.values()
  }

  def copyTo(destination) {
    properties.each { name, value -> destination[name] = _eval(value) }
  }

  protected def _eval(value){
    value
  }

  protected def _eval(Closure value){
    value()
  }
}



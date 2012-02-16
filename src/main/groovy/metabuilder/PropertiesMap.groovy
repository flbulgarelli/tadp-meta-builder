package metabuilder

import groovy.transform.TupleConstructor

@TupleConstructor
class PropertiesMap {
  Map properties

  def set(name, value) {
    properties[name].set(value)
  }

  def copyTo(target) {
    properties.each { name, property -> property.setToTarget(target)}
  }
}

class PropertyClassesMap {
  Map propertyClasses = [:]

  def leftShift(propertyClass) {
    propertyClasses[propertyClass.name] = propertyClass
  }

  def copyTo(expandoMetaClass) {
    propertyClasses.each { name, propertyClass -> propertyClass.addTo(expandoMetaClass)}
  }

  def newPropertiesMap() {
    new PropertiesMap(propertyClasses.collectEntries { name, property ->
      [ (name) : property.newProperty()]
    })
  }
}



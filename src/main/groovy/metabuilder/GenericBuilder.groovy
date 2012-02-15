package metabuilder

import groovy.transform.TupleConstructor
import metabuilder.constructor.Constructor
import metabuilder.injector.DependencyInjector

@TupleConstructor
class GenericBuilder {

  PropertiesMap properties
  Constructor constructor
  DependencyInjector dependencyInjector

  def _setOptionalProperty(name, value) {
    properties.set(name, value)
    this
  }

  def _setMandatoryProperty(name, value) {
    assert value != null
    _setOptionalProperty(name, value)
  }

  def _addCollectionPropertyElement(name, value){
    properties.add(name, value)
    this
  }

  def build() {
    dependencyInjector.createAndInitialize(constructor, properties)
  }
}

package metabuilder

import groovy.transform.TupleConstructor
import metabuilder.constructor.Constructor
import metabuilder.injector.DependencyInjector

@TupleConstructor
class GenericBuilder {

  PropertiesMap properties
  Constructor constructor
  DependencyInjector dependencyInjector

  def _setProperty(name, value) {
    properties.set(name, value)
    this
  }

  def build() {
    dependencyInjector.createAndInitialize(constructor, properties)
  }
}

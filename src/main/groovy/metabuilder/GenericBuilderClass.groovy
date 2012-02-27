package metabuilder

import groovy.transform.TupleConstructor

@TupleConstructor
class GenericBuilderClass {
  final metaClazz, propertiesInitialValues, constructor, dependencyInjector

  GenericBuilder newInstance() {
    def builder = new GenericBuilder(
      newPropertiesMap(),
      constructor,
      dependencyInjector)
    builder.metaClass = metaClazz
    builder
  }
  
  def build(closure) {
    def builder = newInstance()
    closure.delegate = new BuildObjectDelegate(builder: builder)
    closure()
    builder.build()
  }

  def newPropertiesMap() {
    propertiesInitialValues.newPropertiesMap()
  }
  
}

class BuildObjectDelegate {
  GenericBuilder builder
  
  def propertyMissing(String name, value) {
    builder._setProperty(name, value)
  }
}
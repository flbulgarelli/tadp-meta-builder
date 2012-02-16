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

  def newPropertiesMap() {
    propertiesInitialValues.newPropertiesMap()
  }
}
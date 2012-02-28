package metabuilder

import metabuilder.constructor.ClosureConstructor
import metabuilder.constructor.NewInstanceConstructor
import metabuilder.injector.SetterInjector
import metabuilder.nested.NewBuilderNestedStyleDelegate
import metabuilder.tabular.NewBuilderTabularStyleDelegate


/**
 * @author flbulgarelli
 * 
 */
class MetaBuilder {

  MetaClass metaClazz = new ExpandoMetaClass(GenericBuilder)
  def propertyClassesMap = new PropertyClassesMap()
  def dependencyInjector = new SetterInjector()
  def constructor

  /**
   * Configura una propiedad obligatoria del builder. Esta propiedad deberá ser especificada en los builder 
   * creados con la {@link GenericBuilderClass}
   * resultante
   * 
   * @param name el nombre de la propiedad
   * @return this
   */
  MetaBuilder withMandatoryProperty(name) {
    _withPropertyClass(PropertyClasses.mandatory(name))
  }

  /**
   * Configura una propiedad opcional del builder. 
   * Esta propiedad podrá ser omitida en los builder 
   * creados con la {@link GenericBuilderClass}
   * resultante. En tal caso, la propiedad tomará el valor {@code defaulValue}
   * 
   * @param name el nombre de la propiedad
   * @param defaultValue un valor o una expresión (closure) con la que se inicializará 
   *  la propiedad opcional en caso de no especiicarse ningún valor
   * @return this
   */
  MetaBuilder withOptionalProperty(name, defaultValue = null) {
    _withPropertyClass(PropertyClasses.optional(name, defaultValue))
  }

  MetaBuilder withFixedProperty(name, value) {
    _withPropertyClass(PropertyClasses.fixed(name, value))
  }

  MetaBuilder withCollectionProperty(name) {
    _withPropertyClass(PropertyClasses.collection(name))
  }

  MetaBuilder withTargetClass(targetClass) {
    constructor = new NewInstanceConstructor(targetClass)
    this
  }

  MetaBuilder withFactoryClosure(closure) {
    constructor = new ClosureConstructor(closure)
  }

  protected def _withPropertyClass(propertyClass) {
    propertyClassesMap << propertyClass
    this
  }

  GenericBuilderClass build() {
    assert constructor != null, "Must set a constructor"
    propertyClassesMap.copyTo(metaClazz)
    metaClazz.initialize()
    new GenericBuilderClass(metaClazz, propertyClassesMap, constructor, dependencyInjector)
  }
  
  static GenericBuilderClass buildTabular(Class targetClass, buildClosure) {
    _buildWithTabularDelegateClosure(new MetaBuilder().withTargetClass(targetClass), buildClosure)
  }
  
  static GenericBuilderClass build(Class targetClass, buildClosure) {
    _buildWithNestedDelegateClosure(new MetaBuilder().withTargetClass(targetClass), buildClosure)
  }
  
  static GenericBuilderClass build(Closure factoryClosure, buildClosure) {
    _buildWithNestedDelegateClosure(new MetaBuilder().withFactoryClosure(factoryClosure), buildClosure)
  }
  
  private static GenericBuilderClass _buildWithNestedDelegateClosure(metaBuilder, Closure buildClosure) {
    _buildWithClosure(metaBuilder, new NewBuilderNestedStyleDelegate(metaBuilder: metaBuilder),  buildClosure)
  }
  
  private static GenericBuilderClass _buildWithTabularDelegateClosure(metaBuilder, Closure buildClosure) {
    _buildWithClosure(metaBuilder, new NewBuilderTabularStyleDelegate(metaBuilder: metaBuilder),  buildClosure)
  }
  
  private static GenericBuilderClass _buildWithClosure(metaBuilder, delegate, Closure buildClosure) {
    buildClosure.delegate = delegate
    buildClosure()
    metaBuilder.build()
  }
}


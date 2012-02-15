package metabuilder

import metabuilder.constructor.ClosureConstructor
import metabuilder.constructor.NewInstanceConstructor
import metabuilder.injector.SetterInjector

/*
 Copyright (c) 2012, The Staccato-Commons Team
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation; version 3 of the License.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 */



/**
 * @author flbulgarelli
 * 
 */
class MetaBuilder {

  MetaClass metaClazz = new ExpandoMetaClass(GenericBuilder)
  def propertiesInitialValues = [:]
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
    _withProperty(name, null) { GenericBuilder builder,  value ->
      builder._setMandatoryProperty(name, value)
    }
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
  MetaBuilder withOptionalProperty(name, defaultValue) {
    _withProperty(name, defaultValue) { GenericBuilder builder, value ->
      builder._setOptionalProperty(name, value)
    }
  }

  MetaBuilder withFixedProperty(name, value) {
    _withProperty(name, value) {
      throw new IllegalStateException("Property $name is not configurable")
    }
  }

  MetaBuilder withCollectionProperty(name) {
    _withCollectionProperty(name) { GenericBuilder builder, value ->
      builder._addCollectionPropertyElement(name, value)
    }
  }

  MetaBuilder withTargetClass(targetClass) {
    constructor = new NewInstanceConstructor(targetClass)
    this
  }

  MetaBuilder withFactoryClosure(closure) {
    constructor = new ClosureConstructor(closure)
  }

  protected def _withProperty(name, defaultValue, closure) {
    metaClazz.setProperty("with${name.capitalize()}") { closure(delegate, it) }
    propertiesInitialValues[name] = defaultValue
    this
  }

  protected def _withCollectionProperty(name, closure) {
    metaClazz.setProperty("add${name.capitalize()}") { closure(delegate, it) }
    propertiesInitialValues[name] = {[]}
    this
  }


  GenericBuilderClass build() {
    assert constructor != null, "Must set a constructor"
    metaClazz.initialize()
    new GenericBuilderClass(metaClazz, propertiesInitialValues, constructor, dependencyInjector)
  }
}









package metabuilder

import metabuilder.constructor.ClosureConstructor
import metabuilder.constructor.NewInstanceConstructor
import metabuilder.injector.SetterInjector
import metabuilder.internal.MethodMissingDelegate

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
  
  static GenericBuilderClass newBuilder(Class targetClass, buildClosure) {
    _buildWithClosure(new MetaBuilder().withTargetClass(targetClass), buildClosure)
  }
  
  static GenericBuilderClass newBuilder(Closure factoryClosure, buildClosure) {
    _buildWithClosure(new MetaBuilder().withFactoryClosure(factoryClosure), buildClosure)
  }
  
  private static GenericBuilderClass _buildWithClosure(metaBuilder, Closure buildClosure) {
    buildClosure.delegate = new NewBuilderDelegate(metaBuilder: metaBuilder)
    buildClosure()
    metaBuilder.build()
  }
}

class NewBuilderDelegate {
  MetaBuilder metaBuilder
  
  //  constructorInjection()
  def mandatoryProperties(closure) {
    _property(closure) {name, args ->
      metaBuilder.withMandatoryProperty(name)
    }
  }

  def optionalProperties(closure) {
    _property(closure) {name, args ->
      metaBuilder.withOptionalProperty(name, args.find())
    }
  }

  def fixedProperties(closure) {
    _property(closure) {name, args ->
      metaBuilder.withFixedProperty(name, args.find())
    }
  }

  protected def _property(Closure closure, methodMissingClosure) {
    closure.delegate = new MethodMissingDelegate(methodMissingClosure)
    closure()
  }
}

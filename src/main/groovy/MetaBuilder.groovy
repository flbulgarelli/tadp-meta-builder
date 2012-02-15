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


import groovy.transform.TupleConstructor

/**
 * @author flbulgarelli
 * 
 */
class MetaBuilder {

  GenericBuilderClass target = new GenericBuilderClass()

  MetaBuilder withMandatoryProperty(name) {
    target._addMandatoryProperty(name)
    this
  }

  MetaBuilder withOptionalProperty(name, defaultValue) {
    target._addOptionalProperty(name, defaultValue)
    this
  }

  MetaBuilder withFixedProperty(name, value) {
    target._addFixedProperty(name, value)
    this
  }

  MetaBuilder withCollectionProperty(name) {
    target._addCollectionProperty(name)
    this
  }

  GenericBuilderClass build() {
    target
  }
}

class GenericBuilderClass {
  def expando = new ExpandoMetaClass(GenericBuilder)
  def properties = [:]
  def constructor
  def dependencyInjector

  def _addOptionalProperty(name, defaultValue) {
    _addSimpleProperty(name, defaultValue) { GenericBuilder builder, value ->
      builder._setOptionalProperty(name, value)
    }
  }

  def _addMandatoryProperty(name) {
    _addSimpleProperty(name, null) { GenericBuilder builder,  value ->
      builder._setMandatoryProperty(name, value)
    }
  }

  def _addFixedProperty(name, value) {
    _addSimpleProperty(name, value) {
      throw new IllegalStateException("Property $name is not configurable")
    }
  }

  def _addCollectionProperty(name) {
    expando.setProperty("add${name.capitalize()}") {  delegate._addCollectionPropertyElement(it)  }
    properties[name] = null
  }

  def _addSimpleProperty(name, defaultValue, closure) {
    expando.setProperty("with${name.capitalize()}") { closure(delegate, it) }
    properties[name] = defaultValue
  }

  GenericBuilder newInstance() {
    def builder = new GenericBuilder(properties.clone(), constructor, dependencyInjector)
    builder.metaClass = expando
    builder
  }
}

@TupleConstructor
class GenericBuilder {

  Map properties
  Constructor constructor
  DependencyInjector dependencyInjector

  def _setOptionalProperty(name, value) {
    properties.setProperty(name, value)
    this
  }

  def _setMandatoryProperty(name, value) {
    assert value != null,
    _setOptionalProperty(name, value)
  }

  def _addCollectionPropertyElement(name, value){
    if(properties[name] == null)
      properties[name] = [value]
    else
      properties[name] << value
  }

  def build() {
    dependencyInjector.createAndInitialize(constructor, properties)
  }
}

interface DependencyInjector {
  def createAndInitialize(Constructor constructor, Map args)
}

class SetterInjector implements DependencyInjector {

  def createAndInitialize(Constructor constructor, Map args) {
    def newInstance = constructor.newInstance([])
    args.each { name, value ->  newInstance.setProperty(name, value) }
    newInstance
  }
}

class ConstructorInjector implements DependencyInjector {

  def createAndInitialize(Constructor constructor, Map args) {
    constructor.newInstance(args.values())
  }
}

interface Constructor  {
  def newInstance(args)
}

@TupleConstructor
class NewInstanceConstructor implements Constructor {
  final targetClass

  def newInstance(Object args) {
    targetClass.newInstance(args)
  }
}

@TupleConstructor
class ClosureConstructor implements Constructor {
  final closure

  def newInstance(Object args) {
    closure(args)
  }
}



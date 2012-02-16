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
package metabuilder

import groovy.lang.Closure
import groovy.lang.ExpandoMetaClass
import groovy.transform.TupleConstructor

/**
 * @author flbulgarelli
 * 
 */
interface PropertyClass {
  def addTo(ExpandoMetaClass metaClazz)
  def setToTarget(target, value)
  def setToProperty(Property target, value)
  Property newProperty()
}

interface Property {
  def setToTarget(target)
  def set(value)
}

class PropertyClasses {
  static PropertyClass optional(name, initialExpression, restriction = Restriction.NULL) {
    new SimplePropertyClass(name, Restriction.NULL, restriction, initialExpression )
  }
  static PropertyClass mandatory(name, restriction = Restriction.NULL) {
    def notNull = new Restriction({it != null},"Property $name is mandatory")
    new SimplePropertyClass(name, notNull, notNull.and(restriction), null)
  }
  static PropertyClass fixed(name, value, restriction = Restriction.NULL) {
    new SimplePropertyClass(name, new Restriction({false},"Property $name is not configurable"), restriction, value)
  }
  static PropertyClass collection(name, restriction = Restriction.NULL) {
    new CollectionPropertyClass(name, Restriction.NULL, restriction)
  }
}

class SimplePropertyClass implements PropertyClass {

  final name
  final setRestriction
  final buildRestriction
  final initialExpression

  SimplePropertyClass(name, setRestriction, buildRestriction, Closure initialExpression) {
    this.name = name
    this.setRestriction = setRestriction
    this.buildRestriction = buildRestriction
    this.initialExpression = initialExpression
  }

  SimplePropertyClass(name, restriction, buildRestriction, initialValue) {
    this(name, restriction, buildRestriction, {initialValue} )
  }

  def addTo(ExpandoMetaClass metaClazz) {
    metaClazz.setProperty("with${name.capitalize()}") {  value ->
      delegate._setProperty(name, value)
    }
  }

  def setToTarget(target, value) {
    buildRestriction.check(value)
    target[name] = value
  }

  def setToProperty(Property property, value) {
    setRestriction.check(value)
    property.value = value
  }

  Property newProperty() {
    new SimpleProperty(this, initialExpression())
  }
}

class CollectionPropertyClass extends SimplePropertyClass {

  CollectionPropertyClass(name, setRestriction, buildRestriction) {
    super(name, setRestriction, buildRestriction, {[]})
  }

  def setToProperty(Property property,  value) {
    setRestriction.check(value)
    property.value << value
  }
}

@TupleConstructor
class Restriction {
  final condition
  final message

  def check(value) {
    assert condition(value), message
  }

  def and(other) {
    [check: {value ->
        this.check(value)
        other.check(value)
      }]
  }

  static final NULL = new Restriction({true}, '')
}

@TupleConstructor
class SimpleProperty implements Property {

  final PropertyClass propertyClass
  def value

  def setToTarget(target) {
    propertyClass.setToTarget(target, value)
  }

  def set(value) {
    propertyClass.setToProperty(this, value)
  }
}




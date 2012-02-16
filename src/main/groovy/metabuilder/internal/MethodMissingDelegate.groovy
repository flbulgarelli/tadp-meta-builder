package metabuilder.internal

import groovy.transform.TupleConstructor

@TupleConstructor
class MethodMissingDelegate {
  final closure
  def methodMissing(String name, args) {
    closure(name, args)
  }
}
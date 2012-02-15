package metabuilder.constructor

import groovy.transform.TupleConstructor

interface Constructor  {
  def newInstance(args)
}

@TupleConstructor
class ClosureConstructor implements Constructor {
  final closure

  def newInstance(args) {
    closure(args)
  }
}

@TupleConstructor
class NewInstanceConstructor implements Constructor {
  Class targetClass

  def newInstance(args) {
    targetClass.newInstance(args as Object[])
  }
}



package metabuilder.injector

import metabuilder.PropertiesMap
import metabuilder.constructor.Constructor

interface DependencyInjector {
  def createAndInitialize(Constructor constructor, PropertiesMap args)
}

class SetterInjector implements DependencyInjector {

  def createAndInitialize(Constructor constructor, PropertiesMap args) {
    def newInstance = constructor.newInstance([])
    args.copyTo(newInstance)
    newInstance
  }
}

class ConstructorInjector implements DependencyInjector {

  def createAndInitialize(Constructor constructor, PropertiesMap args) {
    constructor.newInstance(args.values())
  }
}
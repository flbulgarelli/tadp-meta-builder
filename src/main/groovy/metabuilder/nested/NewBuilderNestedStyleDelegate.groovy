package metabuilder.nested;

import metabuilder.MetaBuilder
import metabuilder.internal.MethodMissingDelegate

class NewBuilderNestedStyleDelegate {
  MetaBuilder metaBuilder
  
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
    new MethodMissingDelegate(methodMissingClosure).with(closure)
  }
}
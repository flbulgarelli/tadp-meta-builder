package metabuilder.tabular

import metabuilder.MetaBuilder
import metabuilder.internal.MethodMissingDelegate

class NewBuilderTabularStyleDelegate {
  MetaBuilder metaBuilder
  def headers = ['name']

  def name = new RowHeader()
  def defaultValue = 'defaultValue'
  def type  = 'type'
  def check = 'check'

  def mandatory =  { name, _, check ->  metaBuilder.withMandatoryProperty(name) }
  def optional =  { name, defaultValue, check ->  metaBuilder.withOptionalProperty(name, defaultValue) }
  def fixed =  { name, value, _ ->  metaBuilder.withFixedProperty(name, value) }


  def propertyMissing(String name) {
    new Row(name)
  }

  /**
   * Una fila de la tabla. 
   * Responsable de colectar los valores de sus celdas y pasarselos al builder
   *  
   * @author flbulgarelli
   */
  class Row {
    def propertyName
    def options

    Row(propertyName) {
      this.propertyName = propertyName
      this.options = [name:propertyName]
    }

    def _currentHeader() {
      headers[options.size()]
    }
    
    def _rowCompleted() {
      options.size() == headers.size()
    }
    
    def _setCurrentCell(value) {
      options[_currentHeader()] = value
    }

    def or(value) {
      _setCurrentCell(value)
      if(_rowCompleted()) {
        options.type(options.name, options.defaultValue, options.check)
      }
      this
    }
  }
  
  /**
   * Una fila de título de la tabla. 
   * Responsable de colectar los títulos
   * @author flbulgarelli
   */
  class RowHeader {
    def or(header){
      headers << header
      this
    }
  }
}


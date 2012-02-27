import static org.junit.Assert.*
import metabuilder.MetaBuilder

import org.junit.Test

import Personaje.HabilidadSimple

/**
 * @author flbulgarelli
 * 
 */
class MetaBuilderNestedDSLTestDriver {

  /* Un DSL más Groovy, empleando bloques anidados */
  
  @Test
  void nestedStyle()  {
    def aldeanoBuilder =
      MetaBuilder.build(Personaje) {        
        mandatoryProperties { 
          nombre  
        }
        optionalProperties {
          puntosDeAtaque(2) { 
            it < 20 
          }
          puntosDeDefensa(1) { 
            it < 5 
          }
          puntosDeVida(25) { 
            it < 40 
          }
        }
        fixedProperties {  
          habilidades {[]} 
        }
      }

    def unAldeano= aldeanoBuilder.build {
      nombre = 'Pedro'
      puntosDeDefensa = 4
    }

    unAldeano.with {
      assert nombre == 'Pedro'
      assert puntosDeAtaque == 2
      assert puntosDeDefensa == 4
      assert puntosDeVida == 25
      assert habilidades == []
    }
  }
  
  @Test
  void optionalPropertiesMayBeNotConfigured()  {
    def guerreroBuilderClass =
      MetaBuilder.build(Personaje) {
        optionalProperties {
          nombre
        }      
      }

    def guerrero = guerreroBuilderClass.build { }
    
    assert guerrero.nombre == null
  }

  @Test(expected = AssertionError)
  void mandatoryPropertiesMustBeConfigured()  {
    def guerreroBuilderClass =
      MetaBuilder.build(Personaje) {
       mandatoryProperties { 
         nombre
       }
      }
    guerreroBuilderClass.build {}
  }

  @Test(expected = AssertionError)
  void mandatoryPropertiesMustBeNonNull()  {
    def guerreroBuilderClass =
      MetaBuilder.build(Personaje) {
      mandatoryProperties {
        nombre
      }
    }
    guerreroBuilderClass.newInstance().withNombre(null).build()
  }

  @Test
  void builderPropertiesSupportExpressionsAsDefaultValues() {
    int count = 0
    def guerreroBuilderClass = 
      MetaBuilder.build(Personaje) {        
        optionalProperties {
          nombre { "guerrero" + count++ }
        }
      }

    assert guerreroBuilderClass.build({}).nombre == "guerrero0"
    assert guerreroBuilderClass.build({}).nombre == "guerrero1"
  }
  

}

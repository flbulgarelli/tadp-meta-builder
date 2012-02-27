import static org.junit.Assert.*
import metabuilder.MetaBuilder

import org.junit.Test

import Personaje.HabilidadSimple

/**
 * @author flbulgarelli
 * 
 */
class MetaBuilderTabularDSLTestDriver {

  /* Otra variante de DSL, empleando parametros etiquetados, 
   * un poco más redundante, 
   * pero quizás mas intuitivo */
  
  @Test
  void tabularStyle()  {
    def aldeanoBuilder =
      MetaBuilder.newBuilderClass(Personaje) {
        mandatory name: 'nombre',                         check: { it.size() > 4 }
        optional  name: 'puntosDeAtaque',  default: 2
        optional  name: 'puntosDeDefensa', default: 1,    check: {it < 5}
        optional  name: 'puntosDeVida',    default: 25,   check: {it < 40}
        fixed     name: 'habilidades',     default: {[]}
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

}

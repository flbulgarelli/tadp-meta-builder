import static org.junit.Assert.*
import metabuilder.MetaBuilder

import org.junit.Test

import Personaje.HabilidadSimple

/**
 * @author flbulgarelli
 * 
 */
class MetaBuilderTabularDSLTestDriver {

  /* Basado en el estilo Block, pero empleando pipes como delimitadores */

  @Test
  void tabularStyle()  {
    def aldeanoBuilder =
      MetaBuilder.buildTabular(Personaje) {
        type      |     name           |   defaultValue     | check
        mandatory | 'nombre'           |   null             | { it.size() > 4 }
        optional  | 'puntosDeAtaque'   |   2                | null
        optional  | 'puntosDeDefensa'  |   1                | {it < 5}
        optional  | 'puntosDeVida'     |   2                | {it < 40}
        fixed     | 'habilidades'      |  {[]}              | null
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

import static org.junit.Assert.*
import metabuilder.MetaBuilder

import org.junit.Test

import Personaje.HabilidadSimple

/**
 * @author flbulgarelli
 * 
 */
class MetaBuilderTabularDSLTestDriver {

  /* Basado en el estilo Block, pero empleando pipes como delimitadores. 
   * Salvo la primera columna, las columnas pueden ser reordenadas 
   * y obtener el mismo resultado*/

  @Test
  void tabularStyle()  {
    def aldeanoBuilder =
      MetaBuilder.buildTabular(Personaje) {
        '-----------------+--------------------+-----------+-----------------'
           name           |   defaultValue     |   type    |  check
        '-----------------+--------------------+-----------+-----------------'   
        nombre            |     'n/a'          | mandatory | { it.size() > 4 }
        puntosDeAtaque    |       2            | optional  | null
        puntosDeDefensa   |       1            | optional  | {it < 5}
        puntosDeVida      |      25            | optional  | {it < 40}
        habilidades       |     {[]}           | fixed     | 'n/a'
        '-----------------+--------------------+-----------+-----------------'
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

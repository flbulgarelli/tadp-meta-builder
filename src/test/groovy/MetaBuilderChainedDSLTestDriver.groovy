import static org.junit.Assert.*
import metabuilder.MetaBuilder

import org.junit.Test

import Personaje.HabilidadSimple

/**
 * @author flbulgarelli
 * 
 */
class MetaBuilderChainedDSLTestDriver {

  /*Introducción: Una interfaz muy simple de mensajes encadenados, estilo Java. */

  @Test
  void chainedStyle()  {
    def guerreroBuilderClass =
      new MetaBuilder()
      .withMandatoryProperty('nombre')
      .withOptionalProperty('puntosDeAtaque', 10)
      .withOptionalProperty('puntosDeDefensa', 2)
      .withOptionalProperty('puntosDeVida', 100)
      .withCollectionProperty('habilidades')
      .withTargetClass(Personaje)
      .build()

    def guerreroBuilder = guerreroBuilderClass.newInstance()

    def alejandroMagno = guerreroBuilder
      .withNombre('Alejandro Magno')
      .withPuntosDeAtaque(25)
      .withHabilidades(HabilidadSimple.CABALGAR)
      .withHabilidades(HabilidadSimple.CORRER)
      .build()

    alejandroMagno.with {
      assert nombre == 'Alejandro Magno'
      assert puntosDeAtaque == 25
      assert puntosDeDefensa == 2
      assert puntosDeVida == 100
      assert habilidades == [
        HabilidadSimple.CABALGAR,
        HabilidadSimple.CORRER
      ]
    }
  }


  @Test
  void resultingBuilderClassIsShareable()  {
    def guerreroBuilderClass =
      new MetaBuilder()
      .withMandatoryProperty('nombre')
      .withCollectionProperty('habilidades')
      .withTargetClass(Personaje)
      .build()

    def willy = guerreroBuilderClass.newInstance()
      .withNombre('William the conqueror')
      .withHabilidades(HabilidadSimple.CORRER)
      .build()

    def julio = guerreroBuilderClass.newInstance()
      .withNombre('Julio Cesar')
      .withHabilidades(HabilidadSimple.CABALGAR)
      .build()

    assert !willy.is(julio)
    willy.with {
      assert nombre == 'William the conqueror'
      assert habilidades == [HabilidadSimple.CORRER]
    }

    julio.with {
      assert nombre == 'Julio Cesar'
      assert habilidades == [HabilidadSimple.CABALGAR]
    }
  }

  @Test
  void globalRestrictionsAreEvaluatedAfterObjectIsBuilt() {
    fail()
  }

  @Test
  void propertyRestrictionsAreEvaluatedBeforePropertyIsSet() {
    fail()
  }

  @Test
  void builderSupportsFactoryMethodClosure()  {
    fail()
  }

  @Test
  void builderSupportsConstructorInjection()  {
    fail()
  }


}

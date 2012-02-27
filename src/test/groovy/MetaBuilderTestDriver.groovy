/*
 Copyright (c) 2012, The Staccato-Commons Team
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation; version 3 of the License.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 */


import static org.junit.Assert.*
import metabuilder.MetaBuilder

import org.junit.Test

import Personaje.HabilidadSimple

/**
 * @author flbulgarelli
 * 
 */
class MetaBuilderTestDriver {

  /*
   * 
   * "Cansados de escribir una y otra vez builders a mano, queremos construir un MetaBuilder: un builder de builders, el cual nos permita especificar,
   *  empleando un DSL simple, al menos los siguientes aspectos del builder instanciado:
   * Cuales son las propiedades que se pueden configurar (el builder instanciado no configurará a discreción cualquier propiedad que le digamos, sino solamente esas)
   * Cuales propiedades son opcionales, y su valor por defecto
   * Validaciones que se puedan realizar sobre las propiedades, y sobre el objeto antes y después de ser construido
   * La estrategia de construcción del objeto (se le puede pasar la clase o un bloque de código que se comporte como factory method)
   * La estrategia de inyección de las propiedades (si es por constructor/argumento del bloque o si es por setter)"
   * 
   * Propiedades que son colecciones, para las que se expongan métodos estilo addXXX
   * Propiedades cuyo valor este fijo
   * Podes establecer, para los defaults y valores fijos, tanto valores como expresiones
   * 
   */
  
  
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

  /* Un DSL más Groovy, empleando bloques anidados */
  
  @Test
  void nestedStyle()  {
    def aldeanoBuilder =
      MetaBuilder.newBuilderClass(Personaje) {        
        mandatoryProperties { 
          nombre  
        }
        optionalProperties {
          puntosDeAtaque(2) //{ it < 20 }
          puntosDeDefensa(1)// { it < 5 }
          puntosDeVida(25)// { it < 40 }
        }
        fixedProperties {  
          habilidades {[]} 
        }
      }

    def unAldeano= aldeanoBuilder.newInstance()
      .withNombre('Pedro')
      .withPuntosDeDefensa(4)
      .build()

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
      MetaBuilder.newBuilderClass(Personaje) {
      optionalProperties {
        nombre
      }      
    }

    def guerrero = guerreroBuilderClass.newInstance().build()
    assert guerrero.nombre == null
  }

  @Test(expected = AssertionError)
  void mandatoryPropertiesMustBeConfigured()  {
    def guerreroBuilderClass =
      MetaBuilder.newBuilderClass(Personaje) {
       mandatoryProperties { 
         nombre
       }
      }
    guerreroBuilderClass.newInstance().build()
  }

  @Test(expected = AssertionError)
  void mandatoryPropertiesMustBeNonNull()  {
    def guerreroBuilderClass =
      MetaBuilder.newBuilderClass(Personaje) {
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
      MetaBuilder.newBuilderClass(Personaje) {        
        optionalProperties {
          nombre { "guerrero" + count++ }
        }
      }

    def guerreroBuilder = guerreroBuilderClass

    assert guerreroBuilder.newInstance().build().nombre == "guerrero0"
    assert guerreroBuilder.newInstance().build().nombre == "guerrero1"
  }
  
  /* Otra variante de DSL, empleando parametros etiquetados, 
   * un poco más redundante, 
   * pero quizás mas intuitivo */
  @Test
  void blockStyle()  {
    def aldeanoBuilder =
      MetaBuilder.newBuilderClass(Personaje) {
        mandatory name: 'nombre',                         check: { it.size() > 4 }
        optional  name: 'puntosDeAtaque',  default: 2
        optional  name: 'puntosDeDefensa', default: 1,    check: {it < 5}
        optional  name: 'puntosDeVida',    default: 25,   check: {it < 40}
        fixed     name: 'habilidades',     default: {[]}
      }

    def unAldeano= aldeanoBuilder.newInstance()
      .withNombre('Pedro')
      .withPuntosDeDefensa(4)
      .build()

    unAldeano.with {
      assert nombre == 'Pedro'
      assert puntosDeAtaque == 2
      assert puntosDeDefensa == 4
      assert puntosDeVida == 25
      assert habilidades == []
    }
  }

  @Test
  void builderSupportsRestrictions() {
    fail()
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

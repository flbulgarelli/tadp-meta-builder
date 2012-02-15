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
   * 
   * 
   */

  @Test
  public void test1() throws Exception {
    def guerreroBuilderClass =
      new MetaBuilder()
      .withMandatoryProperty('nombre')
      .withOptionalProperty('puntosDeAtaque', 10)
      .withOptionalProperty('puntosDeDefensa', 2)
      .withOptionalProperty('puntosDeVida', 100)
      .withCollectionProperty('habilidades')
      .withTargetClass(Personaje)
      //.withConstructorDependencyInjection()
      .build()

    def guerreroBuilder = guerreroBuilderClass.newInstance()

    def alejandroMagno = guerreroBuilder
      .withNombre('Alejandro Magno')
      .withPuntosDeAtaque(25)
      .addHabilidades(HabilidadSimple.CABALGAR)
      .addHabilidades(HabilidadSimple.CORRER)
      .build()

    alejandroMagno.with {
      assert nombre == 'Alejandro Magno'
      assert puntosDeAtaque == 25
      assert puntosDeDefensa == 2
      assert puntosDeVida == 100
      assert habilidades == [
        HabilidadSimple.CABALGAR,
        HabilidadSimple.CORRER]
    }
  }


  //  def aldeanoBuilder =
  //  new MetaBuilder()
  //  .withMandatoryProperty('nombre')
  //  .withOptionalProperty('puntosDeAtaque', 2)
  //  .withOptionalProperty('puntosDeDefensa', 1)
  //  .withOptionalProperty('puntosDeVida', 25)
  //  .withFixedProperty('habilidades', [])
  //  .withTargetClass(Personaje)
  // // .withConstructorDependencyInjection()
  //  .build()

  @Test
  public void testName2() throws Exception {
    def guerreroBuilder =
      new MetaBuilder()
      .withTargetClass(Personaje)
      .withConstructorDependencyInjection()
      .withProperties {
        nombre()
        puntosDeAtaque(10) { it < 50 }
        puntosDeDefensa(2)
        puntosDeVida(100)
      }.withCollections { habilidades() }
      .build()


    def aldeanoBuilder =
      MetaBuilder.newBuilder {
        targetClass(Personaje)
        constructorInjection()
        mandatoryProperties { nombre()  }
        optionalProperties {
          puntosDeAtaque(2) { it < 20 }
          puntosDeDefensa(1) { it < 5 }
          puntosDeVida(25) { it < 40 }
        }
        fixedProperties { habilidades([]) }
        check {
          it.puntosDeAtaque > it.puntosDefensa
        }
      }
  }
}

# Problema

Cansados de escribir una y otra vez builders a mano, queremos construir un MetaBuilder: un builder de builders, 
el cual nos permita especificar, empleando un DSL simple, al menos los siguientes aspectos de un builder genérico:

* Cuales son las propiedades que se pueden configurar: un builder genérico no 
  configurará a discreción cualquier propiedad que le digamos, sino solamente las que le indiquemos
* Cuales propiedades son opcionales, y su valor por defecto
* Validaciones que se puedan realizar sobre las propiedades, y sobre el objeto antes y después de ser construido
* La estrategia de construcción del objeto (se le puede pasar la clase o un bloque de código que se comporte como factory method)
* La estrategia de inyección de las propiedades (si es por constructor/argumento del bloque o si es por setter)
* Propiedades que son colecciones, para las que se expongan métodos estilo addXXX
* Propiedades cuyo valor este fijo

# Caso de uso:

Un metabuilder nos permitirá construir clases de builders genéricos, que harán las veces de fábrica de builders genéricos similares. Es decir,
el caso de uso típico será:

1. Se instanciará un metabuilder y se lo configurará
2. El metabuilder construirá una clase de builder genérico con la configuración dada
3. Se instanciará la clase de builder genérico, lo que devolverá un builder genérico, y se lo configurará
4. El builder genérico construirá el objeto deseado con la configuración dad

# Que hay aquí

En este proyecto contiene pruebas de concepto de 4 estilos diferentes de DSLs, desde mas sencillos hasta más complejos:
* Estilo mensajes encadenados (ChainedStyle): nada raro, lo mismo que se podría lograr en Java Ej:

```groovy
    receptor.mensaje1(valor1, valor2).mensaje2(valor3, valor4)
```

* Estilo de bloque y métodos con paremtros nombrados (BlockStyle). Se logra tomando mapas como argumentos. Ej:

```groovy
     receptor.hacer {
        mensaje1 etiqueta1: valor1, etiqueta2: valor2
        mensaje2 etiqueta1: valor3, etiqueta2: valor4 
     }
```

* Estilo de bloques anidados (NestedStyle): se logra haciendo uso de methodMissing y métodos que toman bloques por parámetros,  a los que se les cambia el delegate  Ej: 

```groovy
      receptor.hacer {
        bloque1 {
         bloque2(valor1)
         bloque3(valor2) {
           bloqueN {
             
           }
         }
        }
      }
```

* Estilo tabular (TabularStyle):  Se logra poniendo en el contexto del bloque objetos que entiendan el mensaje or, los cuales consituirán siempre la  primera columna de la tabla. Es necesario redefinir getProperty, para descubrir el orden en que son colocados los títulos  de la tabla. Ej:

```groovy
      receptor.hacer {
        titlo1 | titulo2 | titulo3
        valor1 | valor2  | valor3
        valor4 | valor5  | valor6
      }
```


# Estado de desarrollo

Por ahora solo están implementados los estilos Chained y Nested  

    

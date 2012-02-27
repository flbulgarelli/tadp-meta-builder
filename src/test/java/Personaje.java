import java.util.Collection;

/**
 * @author flbulgarelli
 * 
 */
public class Personaje {

  private String nombre;
  private int puntosDeAtaque;
  private int puntosDeDefensa;
  private int puntosDeVida;
  private Collection<Habilidad> habilidades;

  // public Personaje(String nombre, int puntosDeAtaque, int puntosDeDefensa,
  // int puntosDeVida,
  // Collection<Habilidad> habilidades) {
  // this.nombre = nombre;
  // this.puntosDeAtaque = puntosDeAtaque;
  // this.puntosDeDefensa = puntosDeDefensa;
  // this.puntosDeVida = puntosDeVida;
  // this.habilidades = habilidades;
  // }

  // dont'care

  public interface Habilidad {
    // don't care
  }

  public enum HabilidadSimple implements Habilidad {
    CORRER, SANAR, CABALGAR
  }

}

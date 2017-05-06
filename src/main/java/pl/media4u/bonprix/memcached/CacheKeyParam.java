package pl.media4u.bonprix.memcached;

/** jesli atrubut metody ma zostac uzyty jako element klucza do memcached i nie
 * jest on Stringiem lub typem prymitywnym to musi implementowac ten interfejs */
public interface CacheKeyParam {

	public String toCacheParam();

}

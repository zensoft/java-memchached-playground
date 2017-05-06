package pl.media4u.bonprix.memcached;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.danga.MemCached.ErrorHandler;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/** implementacja MemcachedDao korzystajace z binarnego protokolu memcached */
public class MemcachedDaoBinary implements MemcachedDao {

	private static final Logger LOG = Logger.getLogger(MemcachedDaoBinary.class);

	private MemCachedClient memCachedClient;

	/** nazwa puli polaczen wykorzystywanych przez tego klienta */
	private String connectionPoolName = "default";

	/** na poczatku kazdego klucza doklejany jest prefix, zwykle jako nazwa
	 * servletu, np. mpl, która jest oddzielony od reszty klucza
	 * za pomocą MemcachedDao.KEY_SEPARATOR */
	private String keyPrefix;

	/** opcjonalny obiekt obslugujacy wyjatki */
	private ErrorHandler errorHandler;

	/** lista adresow serwerow */
	private String[] servers;

	/** wagi serwerow */
	private Integer[] weights;

	/** poczatkowa liczba polaczen do serwera memcached */
	private int initConn;

	/** minimalna liczba polaczen do serwera memcached */
	private int minConn;

	/** maksymalna liczba polaczen do serwera memcached */
	private int maxConn;

	/** maksymalny czas bezczynnosci polaczenia w milisekundach */
	private int maxIdle;

	/** czas bezczynnosci watku utrzymujacego minimalna liczbe otwartych
	 * polaczen, 0 nie wlacza w ogole tego watku */
	private int maintSleep;

	/** flaga wlaczajaca algorytm Nagla */
	private boolean nagle;

	/** timeout dla odczytu */
	private int socketTo;

	/** timeout dla nawiazywania polaczenia */
	private int socketConnectTo;

	public MemcachedDaoBinary() {
	}

	public void init() {
		initConnectionPool();
		initClient();
	}

	private void initConnectionPool() {
		SockIOPool pool = SockIOPool.getInstance(connectionPoolName);
		
		pool.setServers(servers);
		pool.setWeights(weights);
		pool.setInitConn(initConn);
		pool.setMinConn(minConn);
		pool.setMaxConn(maxConn);
		pool.setMaxIdle(maxIdle);
		pool.setMaintSleep(maintSleep);
		pool.setNagle(nagle);
		pool.setSocketTO(socketConnectTo);
		pool.setSocketConnectTO(socketConnectTo);

		pool.initialize();
	}

	@SuppressWarnings("deprecation")
	private void initClient() {
		memCachedClient = new MemCachedClient(connectionPoolName, true);
		memCachedClient.setErrorHandler(errorHandler);
	}

    @Override
    public Map<String, Map<String, String>> statsItems() {
        return memCachedClient.statsItems();
    }

    /* (non-Javadoc)
         *
         * @see pl.media4u.bonprix.memcache.MemcachedDao#get(java.lang.String) */
	@Override
	public Object get(String key) {
		return memCachedClient.get(addKeyPrefix(key));
	}

	/* (non-Javadoc)
	 * 
	 * @see pl.media4u.bonprix.memcache.MemcachedDao#set(java.lang.String,
	 * java.lang.Object, int) */
	@Override
	public boolean set(String key, Object value, int expire) {
		String addKeyPrefix = addKeyPrefix(key);
		return memCachedClient.set(addKeyPrefix, value, DateUtils.addSeconds(new Date(), expire));
	}

	private String addKeyPrefix(String key) {
		return keyPrefix.concat(key);
	}
	
	public String getServersString() {
		return StringUtils.join(Arrays.asList(servers), " ");
	}

	public void setServersString(String serversString) {
		this.servers = serversString.split(" ");
	}

	public String getWeightsString() {
		return StringUtils.join(Arrays.asList(weights), " ");
	}

	public void setWeightsString(String weightsString) {
		String[] strWeights = weightsString.split(" ");

		weights = new Integer[strWeights.length];
		for (int i = 0; i < strWeights.length; i++) {
			try {
				weights[i] = Integer.valueOf(strWeights[i]);

			} catch (NumberFormatException e) {
				if (LOG.isEnabledFor(Level.ERROR)) {
					LOG.error("blad podczas parsowania parametru okreslajacego wagi dla poszczegolnych serwerow memcached: '"
							+ weightsString + "'");
				}
			}
		}
	}

	public int getInitConn() {
		return initConn;
	}

	public void setInitConn(int initConn) {
		this.initConn = initConn;
	}

	public int getMinConn() {
		return minConn;
	}

	public void setMinConn(int minConn) {
		this.minConn = minConn;
	}

	public int getMaxConn() {
		return maxConn;
	}

	public void setMaxConn(int maxConn) {
		this.maxConn = maxConn;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaintSleep() {
		return maintSleep;
	}

	public void setMaintSleep(int maintSleep) {
		this.maintSleep = maintSleep;
	}

	public boolean getNagle() {
		return nagle;
	}

	public void setNagle(boolean nagle) {
		this.nagle = nagle;
	}

	public int getSocketTo() {
		return socketTo;
	}

	public void setSocketTo(int socketTo) {
		this.socketTo = socketTo;
	}

	public int getSocketConnectTo() {
		return socketConnectTo;
	}

	public void setSocketConnectTo(int socketConnectTo) {
		this.socketConnectTo = socketConnectTo;
	}

	public String getConnectionPoolName() {
		return connectionPoolName;
	}

	public void setConnectionPoolName(String connectionPollName) {
		this.connectionPoolName = connectionPollName;
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix + KEY_SEPARATOR;
	}

}

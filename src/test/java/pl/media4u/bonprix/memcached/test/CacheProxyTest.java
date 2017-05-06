package pl.media4u.bonprix.memcached.test;

import static org.junit.Assert.assertTrue;

import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.Collections;

import org.junit.Test;
import org.mockito.Mockito;

import pl.media4u.bonprix.memcached.CacheProxy;
import pl.media4u.bonprix.memcached.MemcachedDao;
import pl.media4u.bonprix.memcached.annotations.CachedMethod;
import pl.media4u.bonprix.memcached.test.mocks.Dummy;
import pl.media4u.bonprix.memcached.test.mocks.DummyComplicatedMethodInvocation;
import pl.media4u.bonprix.memcached.test.mocks.DummyRunMeInvocation;
import pl.media4u.bonprix.memcached.test.util.RealTimer;

public class CacheProxyTest {

	@Test
	public void testDummyAnnotations() throws SecurityException, NoSuchMethodException {
		// GIVEN
		Method method1 = new Dummy().getClass().getMethod("runMe");
		Method method2 = new Dummy().getClass().getMethod("complicatedMethod", String.class, String.class,
				Integer.class, Integer.class, String.class, String.class);

		// THEN
		assertTrue(method1.isAnnotationPresent(CachedMethod.class));
		assertTrue(method2.isAnnotationPresent(CachedMethod.class));
	}

	@Test
	public void testDisabled() throws Throwable {
		// GIVEN
		MemcachedDao cache = Mockito.mock(MemcachedDao.class);

		CacheProxy proxy = new CacheProxy();
		proxy.setMemcachedDaos(Collections.singletonList(cache));

		// WHEN
		proxy.setCacheEnabled(false);
		proxy.invoke(new DummyRunMeInvocation());

		// THEN
		Mockito.verify(cache, Mockito.times(0)).get("runMe");
	}

	@Test
	public void testEnabled() throws Throwable {
		// GIVEN
		MemcachedDao cache = Mockito.mock(MemcachedDao.class);

		CacheProxy proxy = new CacheProxy();
		proxy.setMemcachedDaos(Collections.singletonList(cache));

		// WHEN
		proxy.setCacheEnabled(true);
		proxy.invoke(new DummyRunMeInvocation());

		// THEN
		Mockito.verify(cache, Mockito.times(1)).get("runMe");
	}

	@Test
	public void testInvokePerformance() throws Throwable {
		// GIVEN
		System.out.println("Testing CacheProxy.invoke() performance...");
		MemcachedDao cache = Mockito.mock(MemcachedDao.class);

		CacheProxy proxy = new CacheProxy();
		proxy.setCacheEnabled(true);
		proxy.setMemcachedDaos(Collections.singletonList(cache));

		RealTimer timer = new RealTimer();
		if (timer.getThreadBean().isCurrentThreadCpuTimeSupported()) {
			// WHEN
			timer.start();
			for (int i = 0; i < 10000; ++i) {
				proxy.invoke(new DummyComplicatedMethodInvocation());
			}
			timer.stop();
		}
		// THEN
		System.out.print("10000: ");
		System.out.println(timer.toString());
		timer.setFactor(1.0 / 10000.0);
		System.out.print("    1: ");
		System.out.println(timer.toString());
	}

}

package pl.media4u.bonprix.memcached.test.mocks;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

public class DummyRunMeInvocation implements MethodInvocation {

	Dummy theObject;

	public DummyRunMeInvocation() {
		theObject = new Dummy();
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public Object proceed() throws Throwable {
		return theObject.runMe();
	}

	@Override
	public Object getThis() {
		return theObject;
	}

	@Override
	public AccessibleObject getStaticPart() {
		return null;
	}

	@Override
	public Method getMethod() {
		try {
			return theObject.getClass().getMethod("runMe");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		return null;
	}

}

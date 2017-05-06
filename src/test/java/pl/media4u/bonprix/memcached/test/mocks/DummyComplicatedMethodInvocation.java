package pl.media4u.bonprix.memcached.test.mocks;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

public class DummyComplicatedMethodInvocation implements MethodInvocation {

	Dummy theObject;

	public DummyComplicatedMethodInvocation() {
		theObject = new Dummy();
	}

	@Override
	public Object[] getArguments() {
		return new Object[] { "1", "2", 3, 4, "5", "6" };
	}

	@Override
	public Object proceed() throws Throwable {
		return theObject.complicatedMethod(null, null, null, null, null, null);
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
			return theObject.getClass().getMethod("complicatedMethod", String.class, String.class, Integer.class,
					Integer.class, String.class, String.class);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		return null;
	}

}

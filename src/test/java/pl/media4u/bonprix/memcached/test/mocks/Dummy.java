package pl.media4u.bonprix.memcached.test.mocks;

import pl.media4u.bonprix.memcached.annotations.CachedMethod;

public class Dummy {

	@CachedMethod
	public Object runMe() {
		return "Fresh value";
	}

	@CachedMethod
	public Object complicatedMethod(String arg1, String arg2, Integer arg3, Integer arg4, String arg5, String arg6) {
		return "Fresh value";
	}

}

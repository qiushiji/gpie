package com.ourgame.gpie.sample;

import java.lang.reflect.Method;
import java.net.URI;

public class HelloWorld {
	public static void main(String[] args) throws Exception {

		long start = System.currentTimeMillis();
		URI uri = new URI("tcp://localhost:8888/auth");

		Method[] methods = uri.getClass().getMethods();
		for (int i = 0; i < methods.length; ++i) {

			Method method = methods[i];

			Class<?> retType = method.getReturnType();

			if (method.getName().startsWith("get")
					&& (retType.getSimpleName().equals("String") || retType.getSimpleName().equals("int"))) {
				Object ret = method.invoke(uri, (Object[]) null);

				System.out.printf("%s : %s \n", method.getName(), (ret == null) ? null : ret.toString());
			}
		}

		long end = System.currentTimeMillis();

		System.out.printf("cost %d\n", end - start);
	}
}

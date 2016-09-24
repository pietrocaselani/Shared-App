package io.github.pietrocaselani.httptest;

import org.mozilla.javascript.Function;

/**
 * Created by pc on 9/24/16.
 */
public interface HTTP {
	void get(String link, Function callbackFunction);
}

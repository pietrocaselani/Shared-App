package io.github.pietrocaselani.httptest;

import java.util.Map;

/**
 * Created by pc on 9/24/16.
 */
public interface Delegate {
	void callback(String message);

	void onSuccess(Map json);

	void onError(String errorMessage);
}

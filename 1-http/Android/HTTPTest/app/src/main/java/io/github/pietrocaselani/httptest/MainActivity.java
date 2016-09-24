package io.github.pietrocaselani.httptest;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.UniqueTag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

	Context context;
	private ScriptableObject scope;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initJS();

		final Scriptable downloader = (Scriptable) scope.get("downloader");

		final Function executeTestFunction = (Function) downloader.get("executeTest", downloader);

		final Function downloadFunction = (Function) downloader.get("download", downloader);


		findViewById(R.id.activity_main_button_executeTest).setOnClickListener(new OnClickListener() {
			@Override public void onClick(final View v) {
				final String name = ((TextView) findViewById(R.id.activity_main_edittext_name)).getText().toString();

				executeTestFunction.call(context, scope, downloader, new Object[]{name});
			}
		});

		findViewById(R.id.activity_main_button_download).setOnClickListener(new OnClickListener() {
			@Override public void onClick(final View v) {
				String link = "https://raw.githubusercontent.com/pietrocaselani/JSON-test/master/contatos.json";

				final Object[] args = {link};

				downloadFunction.call(context, scope, downloader, args);
			}
		});
	}

	@Override protected void onDestroy() {
		if (isFinishing())
			Context.exit();

		super.onDestroy();
	}

	private void initJS() {
		context = Context.enter();
		context.setOptimizationLevel(-1);

		scope = context.initStandardObjects();

		final String script = loadJSFile();

		context.evaluateString(scope, script, "downloader.js", 1, null);

		final Object consoleJS = Context.javaToJS(new Console(), scope);
		ScriptableObject.putProperty(scope, "console", consoleJS);

		ScriptableObject.putProperty(scope, "http", Context.javaToJS(getHTTP(), scope));

		final Delegate delegate = getJSDelegate();

		ScriptableObject.putProperty(scope, "delegate", Context.javaToJS(delegate, scope));
	}

	@NonNull private Delegate getJSDelegate() {
		return new Delegate() {
			@Override public void callback(String message) {
				((TextView) findViewById(R.id.activity_main_textview_result)).setText(message);
			}

			@Override public void onSuccess(Map json) {
				List result = (List) json.get("result");

				final StringBuilder text = new StringBuilder();

				for (Object object : result) {
					final Map map = (Map) object;

					for (final Object key : map.keySet()) {
						final Object value = map.get(key);
						text.append(key).append(" = ").append(value).append("\n");
					}

					text.append("\n\n");
				}

				((TextView) findViewById(R.id.activity_main_textview_result)).setText(text.toString());
			}

			@Override public void onError(String errorMessage) {
				((TextView) findViewById(R.id.activity_main_textview_result)).setText(errorMessage);
			}
		};
	}

	private HTTP getHTTP() {
		return new HTTP() {
			@Override public void get(final String link, final Function callback) {
				final HandlerThread thread = new HandlerThread("JSDownload", Process.THREAD_PRIORITY_BACKGROUND);
				thread.start();

				final Handler handler = new Handler(thread.getLooper());

				handler.post(new Runnable() {
					@Override public void run() {
						HttpURLConnection connection = null;
						try {
							connection = (HttpURLConnection) new URL(link).openConnection();

							connection.connect();

							final int statusCode = connection.getResponseCode();

							final String jsonString = convertStreamToString(connection.getInputStream());

							final NativeObject nativeObject = new NativeObject();
							nativeObject.defineProperty("statusCode", statusCode, NativeObject.EMPTY);
							nativeObject.defineProperty("body", jsonString, NativeObject.EMPTY);

							final Scriptable downloaderScope = (Scriptable) scope.get("downloader");

							runOnUiThread(new Runnable() {
								@Override public void run() {
									callback.call(context, scope, downloaderScope, new Object[]{nativeObject});
								}
							});
						} catch (IOException e) {
							throw new RuntimeException(e);
						} finally {
							if (connection != null)
								connection.disconnect();
						}

						thread.quit();
					}
				});
			}
		};
	}

	private String loadJSFile() {
		return loadFileFromAssets("downloader.js");
	}

	private String loadFileFromAssets(String fileName) {
		try {
			return convertStreamToString(getAssets().open(fileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String convertStreamToString(InputStream inputStream) throws IOException {
		final BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		final StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null)
			total.append(line);

		return total.toString();
	}
}

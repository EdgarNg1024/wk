package edgar.wk.net.data.callback;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lzy.okgo.callback.AbsCallback;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by EdgarNg on 2018/7/9.
 */

public abstract class JsonCallBack<T> extends AbsCallback<T> {
    private Type type;
    private Class<T> clazz;

    public JsonCallBack(Type type) {
        this.type = type;
    }

    public JsonCallBack(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T convertResponse(Response response) throws Throwable {
        String body = response.body().string();
        Log.d("=============", body);
        if (TextUtils.isEmpty(body)) return null;

        T data = null;
        Gson gson = new Gson();
        if (type != null) data = gson.fromJson(body, type);
        if (clazz != null) data = gson.fromJson(body, clazz);
        return data;
    }
}

package repository;

import java.net.UnknownHostException;
import java.util.Locale;

import okhttp3.ResponseBody;
import api.ApiCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
abstract class BaseRepository {
    protected <T> void enqueue(Call<T> call, ApiCallback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(httpMessage(response.code()));
                }
            }
            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                callback.onError(networkMessage(throwable));
            }
        });
    }
    protected void enqueueVoid(Call<ResponseBody> call, ApiCallback<Void> callback) {
        enqueue(call, new ApiCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                callback.onSuccess(null);
            }
            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
    protected static String httpMessage(int code) {
        if (code == 401 || code == 403) {
            return "Chave API inválida.";
        }
        return "O servidor devolveu um erro (" + code + ").";
    }
    protected static String networkMessage(Throwable throwable) {
        if (throwable instanceof UnknownHostException) {
            return "Sem ligação à Internet.";
        }
        return "Não foi possível comunicar com o servidor.";
    }
    protected static String filter(String field, String value) {
        return "{\"" + field + "\":\"" + escape(value) + "\"}";
    }
    protected static String orderBy(String field, boolean ascending) {
        return String.format(Locale.US, "{\"$orderby\":{\"%s\":%d}}", field, ascending ? 1 : -1);
    }
    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}



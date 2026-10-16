package mock;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import model.Category;
import model.Trip;
import model.User;

/**
 * Backend simulado do Grupo 1: responde aos pedidos a /rest/... com os dados do
 * MockData, sem sair do telemóvel. Só atua quando USE_MOCK_DATA=true.
 */
public class MockInterceptor implements Interceptor {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final Gson gson = new Gson();
    private final MockData data = MockData.getInstance();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String path = request.url().encodedPath();

        if (!MockData.isEnabled()) {
            return chain.proceed(request);
        }
        if (!path.startsWith("/rest/")) {
            return chain.proceed(request);
        }

        String collection = segment(path, 1);
        String id = segment(path, 2);
        String payload = body(request);

        switch (collection == null ? "" : collection) {
            case "users":
                return rest(request, data.getUsers(), User.class, "usr", id, payload);
            case "categories":
                return categories(request, id, payload);
            case "trips":
                return trips(request, id, payload);
            default:
                return chain.proceed(request);
        }
    }

    private <T> Response rest(Request request, List<T> store, Class<T> type,
                              String idPrefix, String id, String payload) {
        switch (request.method()) {
            case "GET":
                if (id == null) {
                    return json(request, gson.toJson(filterAndSort(store, request)));
                }
                T found = findById(store, id);
                return found == null ? notFound(request) : json(request, gson.toJson(found));

            case "POST": {
                JsonObject object = parse(payload);
                object.addProperty("_id", data.nextId(idPrefix));
                store.add(gson.fromJson(object, type));
                return json(request, object.toString());
            }

            case "PUT": {
                JsonObject object = parse(payload);
                if (object.get("_id") == null) {
                    object.addProperty("_id", id == null ? data.nextId(idPrefix) : id);
                }
                T updated = gson.fromJson(object, type);
                int index = indexOf(store, object.get("_id").getAsString());
                if (index < 0) {
                    store.add(updated);
                } else {
                    store.set(index, updated);
                }
                return json(request, object.toString());
            }

            case "PATCH": {
                T current = findById(store, id);
                if (current == null) {
                    return notFound(request);
                }
                JsonObject object = gson.toJsonTree(current).getAsJsonObject();
                JsonObject changes = parse(payload);
                for (String key : changes.keySet()) {
                    object.add(key, changes.get(key));
                }
                store.set(indexOf(store, id), gson.fromJson(object, type));
                return json(request, object.toString());
            }

            case "DELETE": {
                int index = indexOf(store, id);
                if (index >= 0) {
                    store.remove(index);
                }
                return json(request, "{}");
            }

            default:
                return json(request, "{}");
        }
    }

    private Response categories(Request request, String id, String payload) {
        if ("POST".equals(request.method()) && payload != null && payload.trim().startsWith("[")) {
            Category[] incoming = gson.fromJson(payload, Category[].class);
            List<Category> created = new ArrayList<>();
            for (Category category : incoming) {
                category.setId(data.nextId("cat"));
                data.getCategories().add(category);
                created.add(category);
            }
            return json(request, gson.toJson(created));
        }
        return rest(request, data.getCategories(), Category.class, "cat", id, payload);
    }

    private Response trips(Request request, String id, String payload) {
        if ("GET".equals(request.method()) && id == null) {
            return json(request, gson.toJson(filterAndSort(data.getTrips(), request)));
        }
        return rest(request, data.getTrips(), Trip.class, "trip", id, payload);
    }

    private <T> List<T> filterAndSort(List<T> store, Request request) {
        List<T> result = new ArrayList<>();
        String query = request.url().queryParameter("q");
        String hints = request.url().queryParameter("h");

        for (T item : store) {
            if (query == null || matches(item, query)) {
                result.add(item);
            }
        }
        applyOrderBy(result, hints);
        return result;
    }

    private <T> boolean matches(T item, String query) {
        JsonObject filter = parse(query);
        JsonObject object = gson.toJsonTree(item).getAsJsonObject();
        for (String key : filter.keySet()) {
            JsonElement value = object.get(key);
            if (value == null || value.isJsonNull()
                    || !value.getAsString().equals(filter.get(key).getAsString())) {
                return false;
            }
        }
        return true;
    }

    private <T> void applyOrderBy(List<T> list, String hints) {
        if (hints == null || list.isEmpty()) {
            return;
        }
        JsonObject hint = parse(hints);
        JsonElement orderBy = hint.get("$orderby");
        if (orderBy == null || !orderBy.isJsonObject()) {
            return;
        }
        JsonObject fields = orderBy.getAsJsonObject();
        if (fields.size() == 0) {
            return;
        }
        String field = fields.keySet().iterator().next();
        boolean ascending = fields.get(field).getAsInt() >= 0;

        list.sort((left, right) -> {
            String a = fieldValue(left, field);
            String b = fieldValue(right, field);
            int comparison = compare(a, b);
            return ascending ? comparison : -comparison;
        });
    }

    private <T> String fieldValue(T item, String field) {
        JsonElement value = gson.toJsonTree(item).getAsJsonObject().get(field);
        return value == null || value.isJsonNull() ? null : value.getAsString();
    }

    private int compare(String a, String b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        try {
            return Double.compare(Double.parseDouble(a), Double.parseDouble(b));
        } catch (NumberFormatException e) {
            return a.compareToIgnoreCase(b);
        }
    }

    private <T> T findById(List<T> store, String id) {
        if (id == null) {
            return null;
        }
        int index = indexOf(store, id);
        return index < 0 ? null : store.get(index);
    }

    private <T> int indexOf(List<T> store, String id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < store.size(); i++) {
            JsonElement value = gson.toJsonTree(store.get(i)).getAsJsonObject().get("_id");
            if (value != null && !value.isJsonNull() && id.equals(value.getAsString())) {
                return i;
            }
        }
        return -1;
    }

    private JsonObject parse(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new JsonObject();
        }
        try {
            JsonElement element = JsonParser.parseString(json);
            return element.isJsonObject() ? element.getAsJsonObject() : new JsonObject();
        } catch (RuntimeException e) {
            return new JsonObject();
        }
    }

    private String body(Request request) {
        RequestBody body = request.body();
        if (body == null) {
            return null;
        }
        try {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            return null;
        }
    }

    private String segment(String path, int index) {
        String[] parts = path.split("/");
        int position = index + 1;
        if (position >= parts.length || parts[position].isEmpty()) {
            return null;
        }
        return parts[position];
    }

    private Response json(Request request, String body) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK (simulado)")
                .body(ResponseBody.create(body, JSON))
                .build();
    }

    private Response notFound(Request request) {
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(404)
                .message("Not Found (simulado)")
                .body(ResponseBody.create("{}", JSON))
                .build();
    }
}

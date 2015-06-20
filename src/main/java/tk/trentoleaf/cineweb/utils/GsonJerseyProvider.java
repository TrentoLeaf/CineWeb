package tk.trentoleaf.cineweb.utils;

import com.google.gson.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * This class is a Jersey Provider that uses the Google Gson Library to serialize and deserialize
 * Java objects to and from JSON.
 *
 * @see <a href="https://github.com/DominikAngerer/Boostraped-Jersey-RestAPI">Use GSON in Jersey</a>
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class GsonJerseyProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

    private static final String UTF_8 = "UTF-8";
    private static Gson gson;

    // get a GsonBuilder object with custom serializes
    private static GsonBuilder getGsonBuilderInstance() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // serialize LocalDate
        gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.toString());
            }
        });

        // deserialize LocalDate
        gsonBuilder.registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext arg2) {
                return new LocalDate(json.getAsString());
            }
        });

        // serialize DateTime
        gsonBuilder.registerTypeAdapter(DateTime.class, new JsonSerializer<DateTime>() {
            @Override
            public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return src == null ? null : new JsonPrimitive(src.toString());
            }
        });

        // deserialize DateTime
        gsonBuilder.registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
            @Override
            public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext arg2) {
                return new DateTime(json.getAsString());
            }
        });

        return gsonBuilder;
    }

    // get a custom Gson instance
    public static Gson getGsonInstance() {
        if (gson == null) {
            gson = getGsonBuilderInstance().create();
        }
        return gson;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        try (InputStreamReader streamReader = new InputStreamReader(entityStream, UTF_8)) {
            return getGsonInstance().fromJson(streamReader, genericType);
        } catch (JsonSyntaxException e) {
            // Log exception
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try (OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8)) {
            getGsonInstance().toJson(object, genericType, writer);
        }
    }
}
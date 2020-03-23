package kr.oytech.authenticationservice.handler;

import static feign.Util.UTF_8;
import static feign.Util.ensureClosed;
import static feign.Util.resolveLastTypeParameter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;

public class GsonDecoder implements Decoder {

  private final Gson gson;

  public GsonDecoder(Iterable<TypeAdapter<?>> adapters){
    GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
    for (TypeAdapter<?> adapter : adapters) {
      Type type = resolveLastTypeParameter(adapter.getClass(), TypeAdapter.class);
      builder.registerTypeAdapter(type, adapter);
    }

    this.gson = builder.create();
  }

  public GsonDecoder() {
    this(Collections.<TypeAdapter<?>> emptyList());
  }
  public GsonDecoder(Gson gson) {
    this.gson = gson;
  }

  @Override
  public Object decode(Response response, Type type)
      throws IOException, DecodeException, FeignException {
    if (response.body() == null)
      return null;
    Reader reader = response.body().asReader(UTF_8);
    try {
      return gson.fromJson(reader, type);
    } catch (JsonIOException e) {
      if (e.getCause() != null && e.getCause() instanceof IOException) {
        throw IOException.class.cast(e.getCause());
      }
      throw e;

    } finally {
      ensureClosed(reader);
    }
  }
}

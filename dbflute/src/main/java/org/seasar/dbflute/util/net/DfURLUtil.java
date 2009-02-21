package org.seasar.dbflute.util.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.seasar.dbflute.util.io.DfInputStreamUtil;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfURLUtil {

    protected static final Map<String, String> CANONICAL_PROTOCOLS = new HashMap<String, String>();
    static {
        CANONICAL_PROTOCOLS.put("wsjar", "jar");
    }

    public static InputStream openStream(URL url) {
        try {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void makeFileAndClose(URL url, String outputFilename) {
        InputStream in;
        try {
            in = url.openStream();
        } catch (IOException e) {
            String msg = DfURLUtil.class.getSimpleName() + "#copy() threw the IO exception!";
            throw new IllegalStateException(msg, e);
        }
        DfInputStreamUtil.makeFileAndClose(in, outputFilename);
    }

    public static URLConnection openConnection(URL url) {
        try {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static URL create(String spec) {
        try {
            return new URL(spec);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static URL create(URL context, String spec) {
        try {
            return new URL(context, spec);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String encode(final String s, final String enc) {
        try {
            return URLEncoder.encode(s, enc);
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String decode(final String s, final String enc) {
        try {
            return URLDecoder.decode(s, enc);
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String toCanonicalProtocol(final String protocol) {
        final String canonicalProtocol = (String) CANONICAL_PROTOCOLS.get(protocol);
        if (canonicalProtocol != null) {
            return canonicalProtocol;
        }
        return protocol;
    }

    public static File toFile(final URL fileUrl) {
        try {
            final String path = URLDecoder.decode(fileUrl.getPath(), "UTF-8");
            return new File(path).getAbsoluteFile();
        } catch (final Exception e) {
            throw new IllegalStateException("fileUrl=" + fileUrl, e);
        }
    }

    public static void disableURLCaches() {
        try {
            final Field field = URLConnection.class.getField("defaultUseCaches");
            field.setAccessible(true);
            field.set(null, Boolean.FALSE);
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
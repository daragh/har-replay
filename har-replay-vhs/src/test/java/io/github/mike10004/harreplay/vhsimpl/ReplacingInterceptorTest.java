package io.github.mike10004.harreplay.vhsimpl;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;
import io.github.mike10004.vhs.HttpRespondable;
import io.github.mike10004.vhs.HttpRespondable.ImmutableHttpRespondable;
import org.apache.http.HttpHeaders;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.*;

public class ReplacingInterceptorTest {

    @Test
    public void collectText() throws IOException {
        org.brotli.dec.BrotliInputStream.class.getName();
        String text = "\"<!doctype html>\\n<html>\\n<head>\\n    <title>ABCDEFG Domain</title>\\n\\n    <meta charset=\\\"utf-8\\\" />\\n    <meta http-equiv=\\\"Content-type\\\" content=\\\"text/html; charset=utf-8\\\" />\\n    <meta name=\\\"viewport\\\" content=\\\"width=device-width, initial-scale=1\\\" />\\n    <style type=\\\"text/css\\\">\\n    body {\\n        background-color: #f0f0f2;\\n        margin: 0;\\n        padding: 0;\\n        font-family: \\\"Open Sans\\\", \\\"Helvetica Neue\\\", Helvetica, Arial, sans-serif;\\n        \\n    }\\n    div {\\n        width: 600px;\\n        margin: 5em auto;\\n        padding: 50px;\\n        background-color: #fff;\\n        border-radius: 1em;\\n    }\\n    a:link, a:visited {\\n        color: #38488f;\\n        text-decoration: none;\\n    }\\n    @media (max-width: 700px) {\\n        body {\\n            background-color: #fff;\\n        }\\n        div {\\n            width: auto;\\n            margin: 0 auto;\\n            border-radius: 0;\\n            padding: 1em;\\n        }\\n    }\\n    </style>    \\n</head>\\n\\n<body>\\n<div>\\n    <h1>Example Domain</h1>\\n    <p>This domain is established to be used for illustrative examples in documents. You may use this\\n    domain in examples without prior coordination or asking for permission.</p>\\n    <p><a href=\\\"http://www.iana.org/domains/example\\\">More information...</a></p>\\n</div>\\n</body>\\n</html>\\n\"";
        MediaType contentType = MediaType.HTML_UTF_8;
        checkState(contentType.charset().isPresent());
        byte[] gzipped = gzip(text.getBytes(contentType.charset().get()));
        String contentEncoding = "gzip";
        HttpRespondable r = ImmutableHttpRespondable.builder(200)
                .bodySource(ByteSource.wrap(gzipped))
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_ENCODING, contentEncoding)
                .build();
        String actual = ReplacingInterceptor.collectText(r);
        assertEquals("text", text, actual);
    }

    private static byte[] gzip(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        try (OutputStream gout = new GZIPOutputStream(baos, data.length)) {
            gout.write(data);
        }
        return baos.toByteArray();
    }
}
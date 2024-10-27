package net.covers1624.curl4j.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static net.covers1624.curl4j.CURL.*;

/**
 * A simple wrapper around a curl mime multipart entity.
 * <p>
 * Created by covers1624 on 2/11/23.
 */
public class CurlMimeBody implements Closeable, CurlBindable {

    private final long mime;
    private final List<Closeable> resources;

    private CurlMimeBody(long mime, List<Closeable> resources) {
        this.mime = mime;
        this.resources = resources;
    }

    /**
     * @return The mime pointer.
     */
    public long getMime() {
        return mime;
    }

    @Override
    public void close() throws IOException {
        curl_mime_free(mime);
        for (Closeable resource : resources) {
            try {
                resource.close();
            } catch (Throwable ignore) {
            }
        }
    }

    @Override
    public void apply(long curl) {
        curl_easy_setopt(curl, CURLOPT_MIMEPOST, getMime());
    }

    /**
     * Create a new {@link Builder} for a mime entity.
     *
     * @param handle The {@link CurlHandle}.
     * @return The builder.
     */
    public static Builder builder(CurlHandle handle) {
        return builder(handle.curl);
    }

    /**
     * Create a new {@link Builder} for a mime entity.
     *
     * @param curl The curl pointer.
     * @return The builder.
     */
    public static Builder builder(long curl) {
        return new Builder(curl);
    }

    /**
     * Builder for constructing mime entities.
     */
    public static final class Builder {

        private final long mime;
        private final List<Closeable> resources = new LinkedList<>();

        private Builder(long curl) {
            mime = curl_mime_init(curl);
        }

        /**
         * Add a part to this mime entity.
         *
         * @param name The part name.
         * @return The builder for the part.
         */
        public PartBuilder addPart(String name) {
            return new PartBuilder(name);
        }

        /**
         * Builder for constructing a mime part.
         */
        public class PartBuilder {

            private final long part;

            private PartBuilder(String name) {
                part = curl_mime_addpart(mime);
                curl_mime_name(part, name);
            }

            /**
             * Set the file name for the mime part.
             *
             * @param fileName The file name.
             * @return The same builder.
             */
            public PartBuilder fileName(String fileName) {
                curl_mime_filename(part, fileName);
                return this;
            }

            /**
             * Set the mime type for the mime part.
             *
             * @param type The mime type.
             * @return The same builder.
             */
            public PartBuilder type(String type) {
                curl_mime_type(part, type);
                return this;
            }

            /**
             * Set the body content for the mime part.
             *
             * @param data The data.
             * @return The same builder.
             */
            public PartBuilder body(byte[] data) {
                curl_mime_data(part, data);
                return this;
            }

            /**
             * Set the body content for the mime part to a generic
             * {@link CurlInput} instance. The provided {@link CurlInput}
             * will be automatically cleaned up when the {@link CurlMimeBody}
             * is cleaned up.
             *
             * @param input The {@link CurlInput} data.
             * @return The same builder.
             */
            public PartBuilder body(CurlInput input) {
                long len;
                try {
                    len = input.availableBytes();
                } catch (IOException e) {
                    throw new IllegalArgumentException("Unable to query input for length.", e);
                }
                if (len == -1) throw new IllegalArgumentException("Input must have a known length.");
                resources.add(input);
                curl_mime_data_cb(part, len, input.callback(), input.seekCallback());
                return this;
            }

            /**
             * @return The pointer to the part.
             */
            public long getPart() {
                return part;
            }

            /**
             * Finish this part and return the mime entity builder.
             *
             * @return The mime entity builder.
             */
            public Builder build() {
                return Builder.this;
            }
        }

        /**
         * @return The mime entity pointer.
         */
        public long getMime() {
            return mime;
        }

        /**
         * Finish the mime entity and return the complete mime entity.
         *
         * @return The mime entity.
         */
        public CurlMimeBody build() {
            return new CurlMimeBody(mime, resources);
        }
    }
}

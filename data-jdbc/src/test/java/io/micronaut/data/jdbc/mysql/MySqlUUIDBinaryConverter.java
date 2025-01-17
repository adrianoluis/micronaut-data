package io.micronaut.data.jdbc.mysql;

import io.micronaut.core.convert.ConversionContext;
import io.micronaut.data.model.runtime.convert.AttributeConverter;

import javax.persistence.Converter;
import java.nio.ByteBuffer;
import java.util.UUID;

// MySQL trick to store less data is to use only 16 bytes for UUID type
// We need to use custom UUID <-> binary 16 convertors
@Converter
public class MySqlUUIDBinaryConverter implements AttributeConverter<UUID, byte[]> {

    @Override
    public byte[] convertToPersistedValue(UUID uuid, ConversionContext context) {
        if (uuid == null) {
            return null;
        }
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    @Override
    public UUID convertToEntityValue(byte[] bytes, ConversionContext context) {
        if (bytes == null) {
            return null;
        }
        if (bytes.length != 16) {
            throw new IllegalArgumentException();
        }
        int i = 0;
        long msl = 0;
        for (; i < 8; i++) {
            msl = (msl << 8) | (bytes[i] & 0xFF);
        }
        long lsl = 0;
        for (; i < 16; i++) {
            lsl = (lsl << 8) | (bytes[i] & 0xFF);
        }
        return new UUID(msl, lsl);
    }
}

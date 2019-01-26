package me.lynxplay.alton.utils.properties;

import lombok.Getter;
import me.lynxplay.alton.utils.properties.adapter.SimplePropertyTypeAdapter;
import org.junit.Test;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.assertEquals;

public class NamedPropertiesLoaderTest {

    @Test
    public void defaultLoad() {
        NamedPropertyLoaderTemplate namedPropertyLoaderTemplate = new NamedPropertyLoaderTemplate();
        namedPropertyLoaderTemplate.add(new SimplePropertyTypeAdapter<>(UUID.class , UUID::fromString));
        namedPropertyLoaderTemplate.add(new SimplePropertyTypeAdapter<>(AtomicBoolean.class , s -> new AtomicBoolean(s.equalsIgnoreCase("true"))));

        NamedPropertiesLoader<TestConfiguration> loader = new NamedPropertiesLoader<>(TestConfiguration.class, namedPropertyLoaderTemplate);

        Properties properties = buildConfigProperties();
        TestConfiguration config = loader.load(properties);

        assertEquals(config.getString(), "-string");
        assertEquals(config.getAByte(), Byte.MIN_VALUE);
        assertEquals(config.getAShort(), Short.MIN_VALUE);
        assertEquals(config.getAnInt(), Integer.MIN_VALUE);
        assertEquals(config.getAFloat(), (float) 0);
        assertEquals(config.getADouble(), (double) 0);
        assertEquals(config.getALong(), Long.MIN_VALUE);
        assertEquals(config.getTestEnum(), TestEnum.B);
        assertEquals(config.getUuid(), UUID.fromString("799b3250-71d0-49f9-8054-9fc0d04ba87b"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseException() {
        NamedPropertiesLoader<TestConfiguration> loader = new NamedPropertiesLoader<>(TestConfiguration.class);

        Properties properties = buildConfigProperties();
        properties.setProperty("config.byte" , "notAByte");

        loader.load(properties);
    }

    @Test
    public void parseEmpty() {
        NamedPropertiesLoader<TestConfiguration> loader = new NamedPropertiesLoader<>(TestConfiguration.class);

        Properties properties = buildConfigProperties();
        properties.setProperty("config.byte" , "");

        TestConfiguration config = loader.load(properties);
        assertEquals(config.getAByte() , 0);
    }

    /**
     * Creates a new filled properties instance
     *
     * @return the properties instance
     */
    private Properties buildConfigProperties() {
        Properties properties = new Properties();
        properties.setProperty("config.string", "-string");
        properties.setProperty("config.byte", String.valueOf(Byte.MIN_VALUE));
        properties.setProperty("config.short", String.valueOf(Short.MIN_VALUE));
        properties.setProperty("config.int", String.valueOf(Integer.MIN_VALUE));
        properties.setProperty("config.float", String.valueOf(0));
        properties.setProperty("config.double", String.valueOf(0));
        properties.setProperty("config.long", String.valueOf(Long.MIN_VALUE));
        properties.setProperty("config.enum", "B");
        properties.setProperty("config.uuid", "799b3250-71d0-49f9-8054-9fc0d04ba87b");
        return properties;
    }

    @Getter
    public static class TestConfiguration {

        private String string;
        private byte aByte;
        private short aShort;
        private int anInt;
        private float aFloat;
        private double aDouble;
        private long aLong;
        private TestEnum testEnum;
        private UUID uuid;

        @NamedPropertyConstructor
        public TestConfiguration(@NamedProperty(value = "config.string", defaultValue = "string") String string
                , @NamedProperty(value = "config.byte", defaultValue = "127") byte aByte
                , @NamedProperty(value = "config.short", defaultValue = "32767") short aShort
                , @NamedProperty(value = "config.int", defaultValue = "2147483647") int anInt
                , @NamedProperty(value = "config.float", defaultValue = "42") float aFloat
                , @NamedProperty(value = "config.double", defaultValue = "42") double aDouble
                , @NamedProperty(value = "config.long", defaultValue = "9223372036854775807") long aLong
                , @NamedProperty(value = "config.enum", defaultValue = "A") TestEnum testEnum
                , @NamedProperty(value = "config.uuid", defaultValue = "434eea72-22a6-4c61-b5ef-945874a5c478") UUID uuid) {
            this.string = string;
            this.aByte = aByte;
            this.aShort = aShort;
            this.anInt = anInt;
            this.aFloat = aFloat;
            this.aDouble = aDouble;
            this.aLong = aLong;
            this.testEnum = testEnum;
            this.uuid = uuid;
        }
    }

    public enum TestEnum {
        A, B;
    }

}
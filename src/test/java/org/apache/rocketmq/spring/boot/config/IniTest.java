package org.apache.rocketmq.spring.boot.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Ini} and {@link Ini.Section}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class IniTest {

    // ---- Ini constructor ----

    @Test
    void constructor_default_createsEmptyIni() {
        Ini ini = new Ini();
        assertThat(ini.isEmpty()).isTrue();
        assertThat(ini.size()).isZero();
    }

    @Test
    void constructor_copy_copiesSections() {
        Ini original = new Ini();
        original.setSectionProperty("s1", "k1", "v1");
        Ini copy = new Ini(original);
        assertThat(copy.getSectionProperty("s1", "k1")).isEqualTo("v1");
    }

    @Test
    void constructor_copy_null_throwsException() {
        assertThatThrownBy(() -> new Ini(null)).isInstanceOf(NullPointerException.class);
    }

    // ---- isEmpty ----

    @Test
    void isEmpty_emptyIni_returnsTrue() {
        Ini ini = new Ini();
        assertThat(ini.isEmpty()).isTrue();
    }

    @Test
    void isEmpty_withEmptySection_returnsTrue() {
        Ini ini = new Ini();
        ini.addSection("empty");
        assertThat(ini.isEmpty()).isTrue();
    }

    @Test
    void isEmpty_withContent_returnsFalse() {
        Ini ini = new Ini();
        ini.setSectionProperty("s1", "k1", "v1");
        assertThat(ini.isEmpty()).isFalse();
    }

    // ---- getSectionNames / getSections ----

    @Test
    void getSectionNames_returnsNames() {
        Ini ini = new Ini();
        ini.addSection("s1");
        ini.addSection("s2");
        Set<String> names = ini.getSectionNames();
        assertThat(names).contains("s1", "s2");
    }

    @Test
    void getSections_returnsSections() {
        Ini ini = new Ini();
        ini.addSection("s1");
        Collection<Ini.Section> sections = ini.getSections();
        assertThat(sections).hasSize(1);
    }

    // ---- getSection / addSection / removeSection ----

    @Test
    void getSection_existing_returnsSection() {
        Ini ini = new Ini();
        ini.addSection("s1");
        assertThat(ini.getSection("s1")).isNotNull();
    }

    @Test
    void getSection_nonExisting_returnsNull() {
        Ini ini = new Ini();
        assertThat(ini.getSection("nonexistent")).isNull();
    }

    @Test
    void addSection_new_createsSection() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        assertThat(section).isNotNull();
        assertThat(section.getName()).isEqualTo("s1");
    }

    @Test
    void addSection_existing_returnsExisting() {
        Ini ini = new Ini();
        Ini.Section s1 = ini.addSection("s1");
        Ini.Section s2 = ini.addSection("s1");
        assertThat(s1).isSameAs(s2);
    }

    @Test
    void removeSection_existing_removesAndReturns() {
        Ini ini = new Ini();
        ini.addSection("s1");
        Ini.Section removed = ini.removeSection("s1");
        assertThat(removed).isNotNull();
        assertThat(ini.getSection("s1")).isNull();
    }

    @Test
    void removeSection_nonExisting_returnsNull() {
        Ini ini = new Ini();
        assertThat(ini.removeSection("nonexistent")).isNull();
    }

    // ---- setSectionProperty / getSectionProperty ----

    @Test
    void setSectionProperty_createsSectionIfMissing() {
        Ini ini = new Ini();
        ini.setSectionProperty("s1", "k1", "v1");
        assertThat(ini.getSectionProperty("s1", "k1")).isEqualTo("v1");
    }

    @Test
    void getSectionProperty_withDefault_returnsDefault() {
        Ini ini = new Ini();
        assertThat(ini.getSectionProperty("s1", "k1", "default")).isEqualTo("default");
    }

    @Test
    void getSectionProperty_existing_returnsValue() {
        Ini ini = new Ini();
        ini.setSectionProperty("s1", "k1", "v1");
        assertThat(ini.getSectionProperty("s1", "k1", "default")).isEqualTo("v1");
    }

    @Test
    void getSectionProperty_nullSection_returnsNull() {
        Ini ini = new Ini();
        assertThat(ini.getSectionProperty("s1", "k1")).isNull();
    }

    // ---- load ----

    @Test
    void load_string_parsesSections() {
        Ini ini = new Ini();
        ini.load("[main]\nkey1=value1\nkey2=value2\n");
        assertThat(ini.getSection("main")).isNotNull();
        assertThat(ini.getSectionProperty("main", "key1")).isEqualTo("value1");
        assertThat(ini.getSectionProperty("main", "key2")).isEqualTo("value2");
    }

    @Test
    void load_string_commentsIgnored() {
        Ini ini = new Ini();
        ini.load("[main]\n# comment\n; another comment\nkey1=value1\n");
        assertThat(ini.getSectionProperty("main", "key1")).isEqualTo("value1");
    }

    @Test
    void load_string_defaultSection() {
        Ini ini = new Ini();
        ini.load("key1=value1\n");
        assertThat(ini.getSectionProperty("", "key1")).isEqualTo("value1");
    }

    @Test
    void load_string_multipleSections() {
        Ini ini = new Ini();
        ini.load("[s1]\nk1=v1\n[s2]\nk2=v2\n");
        assertThat(ini.getSectionProperty("s1", "k1")).isEqualTo("v1");
        assertThat(ini.getSectionProperty("s2", "k2")).isEqualTo("v2");
    }

    @Test
    void load_inputStream_parsesContent() throws IOException {
        Ini ini = new Ini();
        String content = "[main]\nkey=value\n";
        ini.load(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        assertThat(ini.getSectionProperty("main", "key")).isEqualTo("value");
    }

    @Test
    void load_inputStream_null_throwsException() {
        Ini ini = new Ini();
        assertThatThrownBy(() -> ini.load((java.io.InputStream) null))
                .isInstanceOf(NullPointerException.class);
    }

    // ---- isSectionHeader / getSectionName ----

    @Test
    void isSectionHeader_valid_returnsTrue() {
        assertThat(Ini.isSectionHeader("[main]")).isTrue();
    }

    @Test
    void isSectionHeader_invalid_returnsFalse() {
        assertThat(Ini.isSectionHeader("main")).isFalse();
    }

    @Test
    void getSectionHeader_null_returnsFalse() {
        assertThat(Ini.isSectionHeader(null)).isFalse();
    }

    @Test
    void getSectionName_valid_returnsName() {
        assertThat(Ini.getSectionName("[main]")).isEqualTo("main");
    }

    @Test
    void getSectionName_invalid_returnsNull() {
        assertThat(Ini.getSectionName("main")).isNull();
    }

    // ---- Map interface methods ----

    @Test
    void map_containsKey() {
        Ini ini = new Ini();
        ini.addSection("s1");
        assertThat(ini.containsKey("s1")).isTrue();
        assertThat(ini.containsKey("s2")).isFalse();
    }

    @Test
    void map_containsValue() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        assertThat(ini.containsValue(section)).isTrue();
    }

    @Test
    void map_get() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        assertThat(ini.get("s1")).isEqualTo(section);
    }

    @Test
    void map_put() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        ini.put("s1", section);
        assertThat(ini.get("s1")).isEqualTo(section);
    }

    @Test
    void map_remove() {
        Ini ini = new Ini();
        ini.addSection("s1");
        ini.remove("s1");
        assertThat(ini.size()).isZero();
    }

    @Test
    void map_putAll() {
        Ini ini = new Ini();
        Ini other = new Ini();
        other.addSection("s1");
        ini.putAll(other);
        assertThat(ini.size()).isEqualTo(1);
    }

    @Test
    void map_clear() {
        Ini ini = new Ini();
        ini.addSection("s1");
        ini.clear();
        assertThat(ini.isEmpty()).isTrue();
    }

    @Test
    void map_keySet() {
        Ini ini = new Ini();
        ini.addSection("s1");
        assertThat(ini.keySet()).contains("s1");
    }

    @Test
    void map_values() {
        Ini ini = new Ini();
        ini.addSection("s1");
        assertThat(ini.values()).hasSize(1);
    }

    @Test
    void map_entrySet() {
        Ini ini = new Ini();
        ini.addSection("s1");
        assertThat(ini.entrySet()).hasSize(1);
    }

    // ---- equals / hashCode / toString ----

    @Test
    void equals_sameContent_returnsTrue() {
        Ini ini1 = new Ini();
        ini1.setSectionProperty("s1", "k1", "v1");
        Ini ini2 = new Ini();
        ini2.setSectionProperty("s1", "k1", "v1");
        assertThat(ini1).isEqualTo(ini2);
    }

    @Test
    void equals_differentContent_returnsFalse() {
        Ini ini1 = new Ini();
        ini1.setSectionProperty("s1", "k1", "v1");
        Ini ini2 = new Ini();
        ini2.setSectionProperty("s1", "k1", "v2");
        assertThat(ini1).isNotEqualTo(ini2);
    }

    @Test
    void equals_notIni_returnsFalse() {
        Ini ini = new Ini();
        assertThat(ini.equals("not an ini")).isFalse();
    }

    @Test
    void hashCode_sameContent_sameHash() {
        Ini ini1 = new Ini();
        ini1.setSectionProperty("s1", "k1", "v1");
        Ini ini2 = new Ini();
        ini2.setSectionProperty("s1", "k1", "v1");
        assertThat(ini1.hashCode()).isEqualTo(ini2.hashCode());
    }

    @Test
    void toString_empty() {
        Ini ini = new Ini();
        assertThat(ini.toString()).isEqualTo("<empty INI>");
    }

    @Test
    void toString_withSections() {
        Ini ini = new Ini();
        ini.addSection("s1");
        assertThat(ini.toString()).contains("s1");
    }

    // ---- Section tests ----

    @Test
    void section_getName() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("mySection");
        assertThat(section.getName()).isEqualTo("mySection");
    }

    @Test
    void section_putAndGet() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        section.put("k1", "v1");
        assertThat(section.get("k1")).isEqualTo("v1");
    }

    @Test
    void section_isEmpty() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        assertThat(section.isEmpty()).isTrue();
        section.put("k1", "v1");
        assertThat(section.isEmpty()).isFalse();
    }

    @Test
    void section_size() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        section.put("k1", "v1");
        section.put("k2", "v2");
        assertThat(section.size()).isEqualTo(2);
    }

    @Test
    void section_containsKey() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        section.put("k1", "v1");
        assertThat(section.containsKey("k1")).isTrue();
        assertThat(section.containsKey("k2")).isFalse();
    }

    @Test
    void section_containsValue() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        section.put("k1", "v1");
        assertThat(section.containsValue("v1")).isTrue();
    }

    @Test
    void section_keySet() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        section.put("k1", "v1");
        assertThat(section.keySet()).contains("k1");
    }

    @Test
    void section_values() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        section.put("k1", "v1");
        assertThat(section.values()).contains("v1");
    }

    @Test
    void section_entrySet() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        section.put("k1", "v1");
        assertThat(section.entrySet()).hasSize(1);
    }

    @Test
    void section_remove() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        section.put("k1", "v1");
        section.remove("k1");
        assertThat(section.isEmpty()).isTrue();
    }

    @Test
    void section_putAll() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        java.util.Map<String, String> map = new java.util.LinkedHashMap<>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        section.putAll(map);
        assertThat(section.size()).isEqualTo(2);
    }

    @Test
    void section_clear() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        section.put("k1", "v1");
        section.clear();
        assertThat(section.isEmpty()).isTrue();
    }

    @Test
    void section_toString_defaultSection() {
        Ini ini = new Ini();
        ini.setSectionProperty("", "k1", "v1");
        Ini.Section section = ini.getSection("");
        assertThat(section.toString()).isEqualTo("<default>");
    }

    @Test
    void section_toString_namedSection() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("mySection");
        assertThat(section.toString()).isEqualTo("mySection");
    }

    @Test
    void section_equals_sameNameAndProps_returnsTrue() {
        Ini ini1 = new Ini();
        ini1.setSectionProperty("s1", "k1", "v1");
        Ini ini2 = new Ini();
        ini2.setSectionProperty("s1", "k1", "v1");
        assertThat(ini1.getSection("s1")).isEqualTo(ini2.getSection("s1"));
    }

    @Test
    void section_equals_differentProps_returnsFalse() {
        Ini ini1 = new Ini();
        ini1.setSectionProperty("s1", "k1", "v1");
        Ini ini2 = new Ini();
        ini2.setSectionProperty("s1", "k1", "v2");
        assertThat(ini1.getSection("s1")).isNotEqualTo(ini2.getSection("s1"));
    }

    @Test
    void section_equals_notSection_returnsFalse() {
        Ini ini = new Ini();
        Ini.Section section = ini.addSection("s1");
        assertThat(section.equals("not a section")).isFalse();
    }

    @Test
    void section_hashCode_sameContent_sameHash() {
        Ini ini1 = new Ini();
        ini1.setSectionProperty("s1", "k1", "v1");
        Ini ini2 = new Ini();
        ini2.setSectionProperty("s1", "k1", "v1");
        assertThat(ini1.getSection("s1").hashCode()).isEqualTo(ini2.getSection("s1").hashCode());
    }

    // ---- Section.isContinued / splitKeyValue ----

    @Test
    void isContinued_blank_returnsFalse() {
        assertThat(Ini.Section.isContinued("")).isFalse();
    }

    @Test
    void isContinued_escaped_returnsTrue() {
        assertThat(Ini.Section.isContinued("value\\")).isTrue();
    }

    @Test
    void isContinued_notEscaped_returnsFalse() {
        assertThat(Ini.Section.isContinued("value")).isFalse();
    }

    @Test
    void splitKeyValue_withEquals_returnsPair() {
        String[] kv = Ini.Section.splitKeyValue("key=value");
        assertThat(kv).containsExactly("key", "value");
    }

    @Test
    void splitKeyValue_withColon_returnsPair() {
        String[] kv = Ini.Section.splitKeyValue("key:value");
        assertThat(kv).containsExactly("key", "value");
    }

    @Test
    void splitKeyValue_withSpace_returnsPair() {
        String[] kv = Ini.Section.splitKeyValue("key value");
        assertThat(kv).containsExactly("key", "value");
    }

    @Test
    void splitKeyValue_null_returnsNull() {
        assertThat(Ini.Section.splitKeyValue(null)).isNull();
    }

    @Test
    void splitKeyValue_blank_returnsNull() {
        assertThat(Ini.Section.splitKeyValue("")).isNull();
    }

    @Test
    void splitKeyValue_noValue_throwsException() {
        assertThatThrownBy(() -> Ini.Section.splitKeyValue("keyonly"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void section_toString_returnsNameAndProps() {
        Ini ini = new Ini();
        ini.addSection("test");
        Ini.Section section = ini.getSection("test");
        section.put("key", "value");
        String str = section.toString();
        assertThat(str).contains("test");
    }

    @Test
    void section_isEmpty_afterClear() {
        Ini ini = new Ini();
        ini.addSection("test");
        Ini.Section section = ini.getSection("test");
        section.put("key", "value");
        assertThat(section.isEmpty()).isFalse();
        section.clear();
        assertThat(section.isEmpty()).isTrue();
    }

    @Test
    void section_keySet_returnsKeys() {
        Ini ini = new Ini();
        ini.addSection("test");
        Ini.Section section = ini.getSection("test");
        section.put("k1", "v1");
        section.put("k2", "v2");
        Set<String> keys = section.keySet();
        assertThat(keys).contains("k1", "k2");
    }

    @Test
    void section_values_returnsValues() {
        Ini ini = new Ini();
        ini.addSection("test");
        Ini.Section section = ini.getSection("test");
        section.put("k1", "v1");
        section.put("k2", "v2");
        Collection<String> values = section.values();
        assertThat(values).contains("v1", "v2");
    }

    @Test
    void section_entrySet_returnsEntries() {
        Ini ini = new Ini();
        ini.addSection("test");
        Ini.Section section = ini.getSection("test");
        section.put("k1", "v1");
        Set<Map.Entry<String, String>> entries = section.entrySet();
        assertThat(entries).hasSize(1);
    }
}

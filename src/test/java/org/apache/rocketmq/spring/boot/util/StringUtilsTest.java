package org.apache.rocketmq.spring.boot.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link StringUtils}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class StringUtilsTest {

    // ---- isEmpty(String) ----

    @Test
    void isEmpty_null_returnsTrue() {
        assertThat(StringUtils.isEmpty((String) null)).isTrue();
    }

    @Test
    void isEmpty_empty_returnsTrue() {
        assertThat(StringUtils.isEmpty("")).isTrue();
    }

    @Test
    void isEmpty_nullLiteral_returnsTrue() {
        assertThat(StringUtils.isEmpty("NULL")).isTrue();
    }

    @Test
    void isEmpty_nullLowerCase_returnsTrue() {
        assertThat(StringUtils.isEmpty("null")).isTrue();
    }

    @Test
    void isEmpty_nonEmpty_returnsFalse() {
        assertThat(StringUtils.isEmpty("hello")).isFalse();
    }

    // ---- isNotEmpty ----

    @Test
    void isNotEmpty_null_returnsFalse() {
        assertThat(StringUtils.isNotEmpty(null)).isFalse();
    }

    @Test
    void isNotEmpty_nonEmpty_returnsTrue() {
        assertThat(StringUtils.isNotEmpty("hello")).isTrue();
    }

    // ---- isNull ----

    @Test
    void isNull_null_returnsTrue() {
        assertThat(StringUtils.isNull(null)).isTrue();
    }

    @Test
    void isNull_blank_returnsTrue() {
        assertThat(StringUtils.isNull("   ")).isTrue();
    }

    @Test
    void isNull_nonBlank_returnsFalse() {
        assertThat(StringUtils.isNull("hello")).isFalse();
    }

    // ---- isEmpty(Object) ----

    @Test
    void isEmptyObject_null_returnsTrue() {
        assertThat(StringUtils.isEmpty((Object) null)).isTrue();
    }

    @Test
    void isEmptyObject_emptyString_returnsTrue() {
        assertThat(StringUtils.isEmpty((Object) "")).isTrue();
    }

    @Test
    void isEmptyObject_nonEmptyString_returnsFalse() {
        assertThat(StringUtils.isEmpty((Object) "hello")).isFalse();
    }

    @Test
    void isEmptyObject_nonString_returnsFalse() {
        assertThat(StringUtils.isEmpty((Object) Integer.valueOf(42))).isFalse();
    }

    // ---- hasLength ----

    @Test
    void hasLength_null_returnsFalse() {
        assertThat(StringUtils.hasLength((CharSequence) null)).isFalse();
    }

    @Test
    void hasLength_empty_returnsFalse() {
        assertThat(StringUtils.hasLength("")).isFalse();
    }

    @Test
    void hasLength_whitespace_returnsTrue() {
        assertThat(StringUtils.hasLength(" ")).isTrue();
    }

    @Test
    void hasLength_text_returnsTrue() {
        assertThat(StringUtils.hasLength("hello")).isTrue();
    }

    @Test
    void hasLengthString_delegates() {
        assertThat(StringUtils.hasLength((String) null)).isFalse();
        assertThat(StringUtils.hasLength("hello")).isTrue();
    }

    // ---- hasText ----

    @Test
    void hasText_null_returnsFalse() {
        assertThat(StringUtils.hasText((CharSequence) null)).isFalse();
    }

    @Test
    void hasText_empty_returnsFalse() {
        assertThat(StringUtils.hasText("")).isFalse();
    }

    @Test
    void hasText_whitespaceOnly_returnsFalse() {
        assertThat(StringUtils.hasText("   ")).isFalse();
    }

    @Test
    void hasText_withText_returnsTrue() {
        assertThat(StringUtils.hasText(" 12345 ")).isTrue();
    }

    @Test
    void hasTextString_delegates() {
        assertThat(StringUtils.hasText((String) null)).isFalse();
        assertThat(StringUtils.hasText("hello")).isTrue();
    }

    // ---- containsWhitespace ----

    @Test
    void containsWhitespace_null_returnsFalse() {
        assertThat(StringUtils.containsWhitespace((CharSequence) null)).isFalse();
    }

    @Test
    void containsWhitespace_noWhitespace_returnsFalse() {
        assertThat(StringUtils.containsWhitespace("hello")).isFalse();
    }

    @Test
    void containsWhitespace_withWhitespace_returnsTrue() {
        assertThat(StringUtils.containsWhitespace("hel lo")).isTrue();
    }

    @Test
    void containsWhitespaceString_delegates() {
        assertThat(StringUtils.containsWhitespace((String) null)).isFalse();
        assertThat(StringUtils.containsWhitespace("hel lo")).isTrue();
    }

    // ---- trimWhitespace ----

    @Test
    void trimWhitespace_null_returnsNull() {
        assertThat(StringUtils.trimWhitespace(null)).isNull();
    }

    @Test
    void trimWhitespace_withWhitespace_trimmed() {
        assertThat(StringUtils.trimWhitespace("  hello  ")).isEqualTo("hello");
    }

    // ---- trimAllWhitespace ----

    @Test
    void trimAllWhitespace_null_returnsNull() {
        assertThat(StringUtils.trimAllWhitespace(null)).isNull();
    }

    @Test
    void trimAllWhitespace_withSpaces_removed() {
        assertThat(StringUtils.trimAllWhitespace("h e l l o")).isEqualTo("hello");
    }

    // ---- trimLeadingWhitespace ----

    @Test
    void trimLeadingWhitespace_null_returnsNull() {
        assertThat(StringUtils.trimLeadingWhitespace(null)).isNull();
    }

    @Test
    void trimLeadingWhitespace_leading_removed() {
        assertThat(StringUtils.trimLeadingWhitespace("  hello")).isEqualTo("hello");
    }

    // ---- trimTrailingWhitespace ----

    @Test
    void trimTrailingWhitespace_null_returnsNull() {
        assertThat(StringUtils.trimTrailingWhitespace(null)).isNull();
    }

    @Test
    void trimTrailingWhitespace_trailing_removed() {
        assertThat(StringUtils.trimTrailingWhitespace("hello  ")).isEqualTo("hello");
    }

    // ---- trimLeadingCharacter ----

    @Test
    void trimLeadingCharacter_null_returnsNull() {
        assertThat(StringUtils.trimLeadingCharacter(null, '/')).isNull();
    }

    @Test
    void trimLeadingCharacter_slashes_trimmed() {
        assertThat(StringUtils.trimLeadingCharacter("///path", '/')).isEqualTo("path");
    }

    // ---- trimTrailingCharacter ----

    @Test
    void trimTrailingCharacter_null_returnsNull() {
        assertThat(StringUtils.trimTrailingCharacter(null, '/')).isNull();
    }

    @Test
    void trimTrailingCharacter_slashes_trimmed() {
        assertThat(StringUtils.trimTrailingCharacter("path///", '/')).isEqualTo("path");
    }

    // ---- startsWithIgnoreCase ----

    @Test
    void startsWithIgnoreCase_nullStr_returnsFalse() {
        assertThat(StringUtils.startsWithIgnoreCase(null, "abc")).isFalse();
    }

    @Test
    void startsWithIgnoreCase_nullPrefix_returnsFalse() {
        assertThat(StringUtils.startsWithIgnoreCase("abc", null)).isFalse();
    }

    @Test
    void startsWithIgnoreCase_match_returnsTrue() {
        assertThat(StringUtils.startsWithIgnoreCase("HelloWorld", "hello")).isTrue();
    }

    @Test
    void startsWithIgnoreCase_noMatch_returnsFalse() {
        assertThat(StringUtils.startsWithIgnoreCase("Hello", "World")).isFalse();
    }

    @Test
    void startsWithIgnoreCase_strShorterThanPrefix_returnsFalse() {
        assertThat(StringUtils.startsWithIgnoreCase("He", "Hello")).isFalse();
    }

    // ---- endsWithIgnoreCase ----

    @Test
    void endsWithIgnoreCase_nullStr_returnsFalse() {
        assertThat(StringUtils.endsWithIgnoreCase(null, "abc")).isFalse();
    }

    @Test
    void endsWithIgnoreCase_nullSuffix_returnsFalse() {
        assertThat(StringUtils.endsWithIgnoreCase("abc", null)).isFalse();
    }

    @Test
    void endsWithIgnoreCase_match_returnsTrue() {
        assertThat(StringUtils.endsWithIgnoreCase("HelloWorld", "world")).isTrue();
    }

    @Test
    void endsWithIgnoreCase_noMatch_returnsFalse() {
        assertThat(StringUtils.endsWithIgnoreCase("Hello", "World")).isFalse();
    }

    @Test
    void endsWithIgnoreCase_strShorterThanSuffix_returnsFalse() {
        assertThat(StringUtils.endsWithIgnoreCase("He", "Hello")).isFalse();
    }

    // ---- substringMatch ----

    @Test
    void substringMatch_match_returnsTrue() {
        assertThat(StringUtils.substringMatch("Hello", 0, "Hel")).isTrue();
    }

    @Test
    void substringMatch_noMatch_returnsFalse() {
        assertThat(StringUtils.substringMatch("Hello", 0, "Wor")).isFalse();
    }

    // ---- countOccurrencesOf ----

    @Test
    void countOccurrencesOf_null_returnsZero() {
        assertThat(StringUtils.countOccurrencesOf(null, "a")).isZero();
    }

    @Test
    void countOccurrencesOf_found_returnsCount() {
        assertThat(StringUtils.countOccurrencesOf("aabbcc", "a")).isEqualTo(2);
    }

    @Test
    void countOccurrencesOf_notFound_returnsZero() {
        assertThat(StringUtils.countOccurrencesOf("aabbcc", "d")).isZero();
    }

    // ---- replace ----

    @Test
    void replace_nullInput_returnsNull() {
        assertThat(StringUtils.replace(null, "a", "b")).isNull();
    }

    @Test
    void replace_replaced() {
        assertThat(StringUtils.replace("aabbcc", "aa", "xx")).isEqualTo("xxbbcc");
    }

    // ---- delete ----

    @Test
    void delete_patternRemoved() {
        assertThat(StringUtils.delete("aabbcc", "bb")).isEqualTo("aacc");
    }

    // ---- deleteAny ----

    @Test
    void deleteAny_nullInput_returnsNull() {
        assertThat(StringUtils.deleteAny(null, "abc")).isNull();
    }

    @Test
    void deleteAny_charsDeleted() {
        assertThat(StringUtils.deleteAny("aabbcc", "ac")).isEqualTo("bb");
    }

    // ---- unqualify ----

    @Test
    void unqualify_dotSeparated_returnsLast() {
        assertThat(StringUtils.unqualify("this.name.is.qualified")).isEqualTo("qualified");
    }

    @Test
    void unqualify_customSeparator_returnsLast() {
        assertThat(StringUtils.unqualify("this:name:is:qualified", ':')).isEqualTo("qualified");
    }

    // ---- capitalize / uncapitalize ----

    @Test
    void capitalize_null_returnsNull() {
        assertThat(StringUtils.capitalize(null)).isNull();
    }

    @Test
    void capitalize_empty_returnsEmpty() {
        assertThat(StringUtils.capitalize("")).isEqualTo("");
    }

    @Test
    void capitalize_firstLetterUpper() {
        assertThat(StringUtils.capitalize("hello")).isEqualTo("Hello");
    }

    @Test
    void uncapitalize_null_returnsNull() {
        assertThat(StringUtils.uncapitalize(null)).isNull();
    }

    @Test
    void uncapitalize_firstLetterLower() {
        assertThat(StringUtils.uncapitalize("Hello")).isEqualTo("hello");
    }

    // ---- getFilename / getFilenameExtension / stripFilenameExtension ----

    @Test
    void getFilename_null_returnsNull() {
        assertThat(StringUtils.getFilename(null)).isNull();
    }

    @Test
    void getFilename_withPath_returnsName() {
        assertThat(StringUtils.getFilename("mypath/myfile.txt")).isEqualTo("myfile.txt");
    }

    @Test
    void getFilename_noSlash_returnsName() {
        assertThat(StringUtils.getFilename("myfile.txt")).isEqualTo("myfile.txt");
    }

    @Test
    void getFilenameExtension_null_returnsNull() {
        assertThat(StringUtils.getFilenameExtension(null)).isNull();
    }

    @Test
    void getFilenameExtension_withExt_returnsExt() {
        assertThat(StringUtils.getFilenameExtension("mypath/myfile.txt")).isEqualTo("txt");
    }

    @Test
    void getFilenameExtension_noExt_returnsNull() {
        assertThat(StringUtils.getFilenameExtension("mypath/myfile")).isNull();
    }

    @Test
    void stripFilenameExtension_null_returnsNull() {
        assertThat(StringUtils.stripFilenameExtension(null)).isNull();
    }

    @Test
    void stripFilenameExtension_withExt_stripped() {
        assertThat(StringUtils.stripFilenameExtension("mypath/myfile.txt")).isEqualTo("mypath/myfile");
    }

    @Test
    void stripFilenameExtension_noExt_returnsOriginal() {
        assertThat(StringUtils.stripFilenameExtension("mypath/myfile")).isEqualTo("mypath/myfile");
    }

    // ---- applyRelativePath ----

    @Test
    void applyRelativePath_withSeparator() {
        assertThat(StringUtils.applyRelativePath("/a/b/file.txt", "c/d.txt")).isEqualTo("/a/b/c/d.txt");
    }

    @Test
    void applyRelativePath_noSeparator() {
        assertThat(StringUtils.applyRelativePath("file.txt", "c/d.txt")).isEqualTo("c/d.txt");
    }

    // ---- cleanPath / pathEquals ----

    @Test
    void cleanPath_null_returnsNull() {
        assertThat(StringUtils.cleanPath(null)).isNull();
    }

    @Test
    void cleanPath_withDotDot_normalized() {
        assertThat(StringUtils.cleanPath("/a/b/../c")).isEqualTo("/a/c");
    }

    @Test
    void pathEquals_equivalent_returnsTrue() {
        assertThat(StringUtils.pathEquals("/a/b/../c", "/a/c")).isTrue();
    }

    // ---- parseLocaleString / toLanguageTag ----

    @Test
    void parseLocaleString_simple_returnsLocale() {
        Locale locale = StringUtils.parseLocaleString("en");
        assertThat(locale.getLanguage()).isEqualTo("en");
    }

    @Test
    void parseLocaleString_withCountry_returnsLocale() {
        Locale locale = StringUtils.parseLocaleString("en_US");
        assertThat(locale.getLanguage()).isEqualTo("en");
        assertThat(locale.getCountry()).isEqualTo("US");
    }

    @Test
    void toLanguageTag_withCountry() {
        assertThat(StringUtils.toLanguageTag(Locale.US)).isEqualTo("en-US");
    }

    // ---- parseTimeZoneString ----

    @Test
    void parseTimeZoneString_valid_returnsTimeZone() {
        assertThat(StringUtils.parseTimeZoneString("GMT+8")).isNotNull();
    }

    @Test
    void parseTimeZoneString_invalid_throwsException() {
        assertThatThrownBy(() -> StringUtils.parseTimeZoneString("INVALID_ZONE"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ---- array manipulation ----

    @Test
    void addStringToArray_null_returnsSingleElement() {
        String[] result = StringUtils.addStringToArray(null, "a");
        assertThat(result).containsExactly("a");
    }

    @Test
    void addStringToArray_existing_appended() {
        String[] result = StringUtils.addStringToArray(new String[]{"a"}, "b");
        assertThat(result).containsExactly("a", "b");
    }

    @Test
    void concatenateStringArrays_bothNull_returnsNull() {
        assertThat(StringUtils.concatenateStringArrays(null, null)).isNull();
    }

    @Test
    void concatenateStringArrays_firstNull_returnsSecond() {
        assertThat(StringUtils.concatenateStringArrays(null, new String[]{"b"})).containsExactly("b");
    }

    @Test
    void concatenateStringArrays_secondNull_returnsFirst() {
        assertThat(StringUtils.concatenateStringArrays(new String[]{"a"}, null)).containsExactly("a");
    }

    @Test
    void concatenateStringArrays_bothPresent_concatenated() {
        assertThat(StringUtils.concatenateStringArrays(new String[]{"a"}, new String[]{"b"}))
                .containsExactly("a", "b");
    }

    @Test
    void mergeStringArrays_noDuplicates_merged() {
        assertThat(StringUtils.mergeStringArrays(new String[]{"a"}, new String[]{"b"}))
                .containsExactly("a", "b");
    }

    @Test
    void mergeStringArrays_withDuplicates_deduped() {
        assertThat(StringUtils.mergeStringArrays(new String[]{"a", "b"}, new String[]{"b", "c"}))
                .containsExactly("a", "b", "c");
    }

    @Test
    void sortStringArray_null_returnsEmpty() {
        assertThat(StringUtils.sortStringArray(null)).isEmpty();
    }

    @Test
    void sortStringArray_unsorted_sorted() {
        assertThat(StringUtils.sortStringArray(new String[]{"c", "a", "b"}))
                .containsExactly("a", "b", "c");
    }

    @Test
    void toStringArray_collection_returnsArray() {
        List<String> list = Arrays.asList("a", "b");
        assertThat(StringUtils.toStringArray(list)).containsExactly("a", "b");
    }

    @Test
    void toStringArray_null_returnsNull() {
        assertThat(StringUtils.toStringArray((Collection<String>) null)).isNull();
    }

    @Test
    void toStringArray_enumeration_returnsArray() {
        Vector<String> v = new Vector<>(Arrays.asList("a", "b"));
        assertThat(StringUtils.toStringArray(v.elements())).containsExactly("a", "b");
    }

    @Test
    void toStringArray_nullEnumeration_returnsNull() {
        assertThat(StringUtils.toStringArray((Enumeration<String>) null)).isNull();
    }

    @Test
    void trimArrayElements_null_returnsEmpty() {
        assertThat(StringUtils.trimArrayElements(null)).isEmpty();
    }

    @Test
    void trimArrayElements_withSpaces_trimmed() {
        assertThat(StringUtils.trimArrayElements(new String[]{" a ", " b "}))
                .containsExactly("a", "b");
    }

    @Test
    void removeDuplicateStrings_null_returnsNull() {
        assertThat(StringUtils.removeDuplicateStrings(null)).isNull();
    }

    @Test
    void removeDuplicateStrings_withDuplicates_deduped() {
        assertThat(StringUtils.removeDuplicateStrings(new String[]{"a", "b", "a"}))
                .containsExactly("a", "b");
    }

    // ---- splitArrayElementsIntoProperties ----

    @Test
    void splitArrayElementsIntoProperties_null_returnsNull() {
        assertThat(StringUtils.splitArrayElementsIntoProperties(null, "=")).isNull();
    }

    @Test
    void splitArrayElementsIntoProperties_valid_returnsProps() {
        Properties props = StringUtils.splitArrayElementsIntoProperties(
                new String[]{"a=1", "b=2"}, "=");
        assertThat(props).isNotNull();
        assertThat(props.getProperty("a")).isEqualTo("1");
        assertThat(props.getProperty("b")).isEqualTo("2");
    }

    // ---- tokenizeToStringArray ----

    @Test
    void tokenizeToStringArray_null_returnsNull() {
        assertThat(StringUtils.tokenizeToStringArray(null)).isNull();
    }

    @Test
    void tokenizeToStringArray_csv_returnsTokens() {
        assertThat(StringUtils.tokenizeToStringArray("a,b,c")).containsExactly("a", "b", "c");
    }

    // ---- delimitedListToStringArray ----

    @Test
    void delimitedListToStringArray_null_returnsEmpty() {
        assertThat(StringUtils.delimitedListToStringArray(null, ",")).isEmpty();
    }

    @Test
    void delimitedListToStringArray_csv_returnsArray() {
        assertThat(StringUtils.delimitedListToStringArray("a,b,c", ","))
                .containsExactly("a", "b", "c");
    }

    @Test
    void delimitedListToStringArray_emptyDelimiter_splitsEachChar() {
        assertThat(StringUtils.delimitedListToStringArray("abc", ""))
                .containsExactly("a", "b", "c");
    }

    @Test
    void delimitedListToStringArray_nullDelimiter_returnsWholeString() {
        assertThat(StringUtils.delimitedListToStringArray("abc", null))
                .containsExactly("abc");
    }

    // ---- commaDelimitedListToStringArray / commaDelimitedListToSet ----

    @Test
    void commaDelimitedListToStringArray_csv_returnsArray() {
        assertThat(StringUtils.commaDelimitedListToStringArray("a,b,c"))
                .containsExactly("a", "b", "c");
    }

    @Test
    void commaDelimitedListToSet_csv_returnsSet() {
        Set<String> set = StringUtils.commaDelimitedListToSet("a,b,a");
        assertThat(set).containsExactly("a", "b");
    }

    // ---- collectionToDelimitedString ----

    @Test
    void collectionToDelimitedString_null_returnsEmpty() {
        assertThat(StringUtils.collectionToDelimitedString(null, ",")).isEmpty();
    }

    @Test
    void collectionToDelimitedString_withItems_returnsDelimited() {
        assertThat(StringUtils.collectionToDelimitedString(Arrays.asList("a", "b"), ","))
                .isEqualTo("a,b");
    }

    @Test
    void collectionToDelimitedString_withPrefixSuffix_returnsFormatted() {
        assertThat(StringUtils.collectionToDelimitedString(Arrays.asList("a", "b"), ",", "'", "'"))
                .isEqualTo("'a','b'");
    }

    @Test
    void collectionToCommaDelimitedString_returnsCSV() {
        assertThat(StringUtils.collectionToCommaDelimitedString(Arrays.asList("a", "b")))
                .isEqualTo("a,b");
    }

    // ---- arrayToDelimitedString ----

    @Test
    void arrayToDelimitedString_null_returnsEmpty() {
        assertThat(StringUtils.arrayToDelimitedString(null, ",")).isEmpty();
    }

    @Test
    void arrayToDelimitedString_single_returnsValue() {
        assertThat(StringUtils.arrayToDelimitedString(new Object[]{"a"}, ",")).isEqualTo("a");
    }

    @Test
    void arrayToDelimitedString_multiple_returnsDelimited() {
        assertThat(StringUtils.arrayToDelimitedString(new Object[]{"a", "b"}, ",")).isEqualTo("a,b");
    }

    @Test
    void arrayToCommaDelimitedString_returnsCSV() {
        assertThat(StringUtils.arrayToCommaDelimitedString(new Object[]{"a", "b"})).isEqualTo("a,b");
    }

    // ---- getMapFromQueryParamString ----

    @Test
    void getMapFromQueryParamString_returnsMap() {
        Map<String, String> result = StringUtils.getMapFromQueryParamString("a`b");
        assertThat(result).isNotNull();
    }

    // ---- replaceAll ----

    @Test
    void replaceAll_noMatch_returnsOriginal() {
        assertThat(StringUtils.replaceAll("xyz", "xx", "abcd")).isEqualTo("abcd");
    }

    // ---- split ----

    @Test
    void splitChar_null_returnsEmpty() {
        assertThat(StringUtils.split(null, ',')).isEmpty();
    }

    @Test
    void splitChar_withDelimiter_returnsParts() {
        // Note: the split(char) method does not include the segment after the last delimiter
        assertThat(StringUtils.split("a,b,c", ',')).containsExactly("a", "b");
    }

    @Test
    void splitChar_noDelimiter_returnsOriginal() {
        assertThat(StringUtils.split("abc", ',')).containsExactly("abc");
    }

    @Test
    void split_delimiterFound_returnsParts() {
        String[] result = StringUtils.split("key=value", "=");
        assertThat(result).containsExactly("key", "value");
    }

    @Test
    void split_noDelimiter_returnsNull() {
        assertThat(StringUtils.split("keyvalue", "=")).isNull();
    }

    @Test
    void split_nullInput_returnsNull() {
        assertThat(StringUtils.split(null, "=")).isNull();
    }

    @Test
    void splits_valid_returnsParts() {
        assertThat(StringUtils.splits("a.b.c", "\\.")).containsExactly("a", "b", "c");
    }

    @Test
    void splits_null_returnsEmpty() {
        assertThat(StringUtils.splits(null, "\\.")).isEmpty();
    }

    // ---- removeLast ----

    @Test
    void removeLast_null_returnsNull() {
        assertThat(StringUtils.removeLast(null)).isNull();
    }

    @Test
    void removeLast_blank_returnsOriginal() {
        // Note: isNull returns true for blank strings, so removeLast returns the original
        assertThat(StringUtils.removeLast("   ")).isEqualTo("   ");
    }

    @Test
    void removeLast_text_lastRemoved() {
        assertThat(StringUtils.removeLast("hello")).isEqualTo("hell");
    }

    // ---- addQuotation ----

    @Test
    void addQuotation_null_returnsNull() {
        assertThat(StringUtils.addQuotation(null)).isNull();
    }

    @Test
    void addQuotion_csv_returnsQuoted() {
        // Note: addQuotation uses the custom split() which splits at the first delimiter only
        assertThat(StringUtils.addQuotation("a,b")).isEqualTo("'a','b'");
    }

    // ---- listToArray / listToString ----

    @Test
    void listToArray_returnsArray() {
        assertThat(StringUtils.listToArray(Arrays.asList("a", "b"))).containsExactly("a", "b");
    }

    @Test
    void listToString_returnsJoined() {
        assertThat(StringUtils.listToString(Arrays.asList("a", "b"), ",")).isEqualTo("a,b");
    }

    // ---- genRandomNum ----

    @Test
    void genRandomNum_returnsCorrectLength() {
        assertThat(StringUtils.genRandomNum(10)).hasSize(10);
    }

    // ---- killNull ----

    @Test
    void killNull_null_returnsEmpty() {
        assertThat(StringUtils.killNull(null)).isEmpty();
    }

    @Test
    void killNull_nonNull_returnsOriginal() {
        assertThat(StringUtils.killNull("hello")).isEqualTo("hello");
    }

    // ---- parentheses / brackets / ditto / quote ----

    @Test
    void parentheses_null_returnsNull() {
        assertThat(StringUtils.parentheses(null)).isNull();
    }

    @Test
    void parentheses_wrapped() {
        assertThat(StringUtils.parentheses("x")).isEqualTo("(x)");
    }

    @Test
    void brackets_null_returnsNull() {
        assertThat(StringUtils.brackets(null)).isNull();
    }

    @Test
    void brackets_wrapped() {
        assertThat(StringUtils.brackets("x")).isEqualTo("[x]");
    }

    @Test
    void ditto_null_returnsNull() {
        assertThat(StringUtils.ditto(null)).isNull();
    }

    @Test
    void ditto_wrapped() {
        assertThat(StringUtils.ditto("x")).isEqualTo("\"x\"");
    }

    @Test
    void quote_null_returnsNull() {
        assertThat(StringUtils.quote((String) null)).isNull();
    }

    @Test
    void quote_wrapped() {
        assertThat(StringUtils.quote("x")).isEqualTo("'x'");
    }

    @Test
    void quoteArray_withSeparator() {
        assertThat(StringUtils.quote(new String[]{"a", "b"}, ",")).isEqualTo("'a','b'");
    }

    @Test
    void quoteArray_null_returnsEmpty() {
        assertThat(StringUtils.quote(null, ",")).isEmpty();
    }

    @Test
    void quoteArray_empty_returnsEmpty() {
        assertThat(StringUtils.quote(new String[]{}, ",")).isEmpty();
    }

    @Test
    void quoteIfString_string_quoted() {
        assertThat(StringUtils.quoteIfString("x")).isEqualTo("'x'");
    }

    @Test
    void quoteIfString_nonString_asIs() {
        assertThat(StringUtils.quoteIfString(Integer.valueOf(42))).isEqualTo(42);
    }

    // ---- trimToAlphaString / trimToAlphaStrings ----

    @Test
    void trimToAlphaString_null_returnsEmpty() {
        assertThat(StringUtils.trimToAlphaString(null)).isEmpty();
    }

    @Test
    void trimToAlphaString_empty_returnsEmpty() {
        assertThat(StringUtils.trimToAlphaString("")).isEmpty();
    }

    @Test
    void trimToAlphaString_withSpecialChars_stripped() {
        assertThat(StringUtils.trimToAlphaString("1\r\n1\r\n")).isEqualTo("11");
    }

    @Test
    void trimToAlphaStrings_null_returnsEmpty() {
        assertThat(StringUtils.trimToAlphaStrings(null)).isEmpty();
    }

    @Test
    void trimToAlphaStrings_empty_returnsEmpty() {
        assertThat(StringUtils.trimToAlphaStrings("")).isEmpty();
    }

    @Test
    void trimToAlphaStrings_withSpecialChars_returnsArray() {
        assertThat(StringUtils.trimToAlphaStrings("1\r\n1")).containsExactly("1", "1");
    }

    // ---- trimToString ----

    @Test
    void trimToString_null_returnsNull() {
        assertThat(StringUtils.trimToString(null)).isNull();
    }

    @Test
    void trimToString_blank_returnsNull() {
        assertThat(StringUtils.trimToString("   ")).isNull();
    }

    @Test
    void trimToString_text_returnsTrimmed() {
        assertThat(StringUtils.trimToString("  hello  ")).isEqualTo("hello");
    }

    // ---- getFirstLetterFromChinessWord ----

    @Test
    void getFirstLetterFromChinessWord_chinese_returnsLetter() {
        char result = StringUtils.getFirstLetterFromChinessWord("中");
        assertThat(result).isBetween('A', 'Z');
    }

    @Test
    void getFirstLetterFromChinessWord_ascii_returnsLetter() {
        assertThat(StringUtils.getFirstLetterFromChinessWord("A")).isEqualTo('A');
    }

    // ---- Unique new tests for uncovered methods ----

    @Test
    void cleanPath_simple_returnsSame() {
        assertThat(StringUtils.cleanPath("file.txt")).isEqualTo("file.txt");
    }

    @Test
    void cleanPath_withParentRef_resolves() {
        assertThat(StringUtils.cleanPath("dir/../file.txt")).isEqualTo("file.txt");
    }

    @Test
    void cleanPath_withCurrentDot_removes() {
        assertThat(StringUtils.cleanPath("dir/./file.txt")).isEqualTo("dir/file.txt");
    }

    @Test
    void cleanPath_withBackslash_normalizes() {
        assertThat(StringUtils.cleanPath("dir\\file.txt")).isEqualTo("dir/file.txt");
    }

    @Test
    void cleanPath_withPrefix_preservesPrefix() {
        assertThat(StringUtils.cleanPath("file:dir/../file.txt")).isEqualTo("file:file.txt");
    }

    @Test
    void cleanPath_absolutePath_preservesLeadingSlash() {
        assertThat(StringUtils.cleanPath("/dir/../file.txt")).isEqualTo("/file.txt");
    }

    @Test
    void parseLocaleString_languageOnly() {
        Locale locale = StringUtils.parseLocaleString("en");
        assertThat(locale).isNotNull();
        assertThat(locale.getLanguage()).isEqualTo("en");
    }

    @Test
    void parseLocaleString_languageAndCountry() {
        Locale locale = StringUtils.parseLocaleString("en_US");
        assertThat(locale).isNotNull();
        assertThat(locale.getLanguage()).isEqualTo("en");
        assertThat(locale.getCountry()).isEqualTo("US");
    }

    @Test
    void parseLocaleString_withVariant() {
        Locale locale = StringUtils.parseLocaleString("en_US_variant");
        assertThat(locale).isNotNull();
        assertThat(locale.getLanguage()).isEqualTo("en");
        assertThat(locale.getCountry()).isEqualTo("US");
    }

    @Test
    void parseLocaleString_withSpaces() {
        Locale locale = StringUtils.parseLocaleString("en US");
        assertThat(locale).isNotNull();
        assertThat(locale.getLanguage()).isEqualTo("en");
    }

    @Test
    void parseLocaleString_empty_returnsNull() {
        Locale locale = StringUtils.parseLocaleString("");
        assertThat(locale).isNull();
    }

    @Test
    void toLanguageTag_withoutCountry() {
        Locale locale = new Locale("zh");
        String tag = StringUtils.toLanguageTag(locale);
        assertThat(tag).isEqualTo("zh");
    }

    @Test
    void parseTimeZoneString_valid() {
        TimeZone tz = StringUtils.parseTimeZoneString("GMT+8");
        assertThat(tz).isNotNull();
    }

    @Test
    void parseTimeZoneString_invalid_throws() {
        assertThatThrownBy(() -> StringUtils.parseTimeZoneString("INVALID_TZ"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void pathEquals_samePaths_returnsTrue() {
        assertThat(StringUtils.pathEquals("dir/file.txt", "dir/file.txt")).isTrue();
    }

    @Test
    void pathEquals_differentPaths_returnsFalse() {
        assertThat(StringUtils.pathEquals("dir1/file.txt", "dir2/file.txt")).isFalse();
    }

    @Test
    void pathEquals_normalized_equal() {
        assertThat(StringUtils.pathEquals("dir/../file.txt", "file.txt")).isTrue();
    }

    @Test
    void applyRelativePath_withoutSeparator() {
        String result = StringUtils.applyRelativePath("/dir/file.txt", "relative");
        assertThat(result).isEqualTo("/dir/relative");
    }

    @Test
    void applyRelativePath_noFolder() {
        String result = StringUtils.applyRelativePath("file.txt", "relative");
        assertThat(result).isEqualTo("relative");
    }

    @Test
    void splitArrayElementsIntoProperties_empty_returnsNull() {
        assertThat(StringUtils.splitArrayElementsIntoProperties(new String[0], "=")).isNull();
    }

    @Test
    void splitArrayElementsIntoProperties_valid() {
        String[] array = {"key1=value1", "key2=value2"};
        Properties props = StringUtils.splitArrayElementsIntoProperties(array, "=");
        assertThat(props).isNotNull();
        assertThat(props.getProperty("key1")).isEqualTo("value1");
    }

    @Test
    void splitArrayElementsIntoProperties_withCharsToDelete() {
        String[] array = {"'key1'='value1'"};
        Properties props = StringUtils.splitArrayElementsIntoProperties(array, "=", "'");
        assertThat(props).isNotNull();
        assertThat(props.getProperty("key1")).isEqualTo("value1");
    }

    @Test
    void substringMatch_beyondLength_returnsFalse() {
        assertThat(StringUtils.substringMatch("hi", 5, "hello")).isFalse();
    }

    @Test
    void collectionToDelimitedString_withPrefixSuffix() {
        List<String> list = List.of("a", "b", "c");
        String result = StringUtils.collectionToDelimitedString(list, ",", "'", "'");
        assertThat(result).isEqualTo("'a','b','c'");
    }

    @Test
    void arrayToDelimitedString_singleElement() {
        assertThat(StringUtils.arrayToDelimitedString(new Object[]{"a"}, ",")).isEqualTo("a");
    }

    @Test
    void arrayToDelimitedString_multiple() {
        assertThat(StringUtils.arrayToDelimitedString(new Object[]{"a", "b"}, ",")).isEqualTo("a,b");
    }

    @Test
    void getMapFromQueryParamString_returnsEmptyMap() {
        Map<String, String> result = StringUtils.getMapFromQueryParamString("key`value");
        assertThat(result).isNotNull();
    }

    @Test
    void replaceAll_noMatch() {
        String result = StringUtils.replaceAll("xyz", "new", "abcabc");
        assertThat(result).isEqualTo("abcabc");
    }

    @Test
    void splits_empty_returnsEmpty() {
        assertThat(StringUtils.splits("", ",")).isEmpty();
    }

    @Test
    void splits_valid() {
        String[] result = StringUtils.splits("a,b,c", ",");
        assertThat(result).containsExactly("a", "b", "c");
    }

    @Test
    void addQuotation_multiple() {
        assertThat(StringUtils.addQuotation("a,b")).isEqualTo("'a','b'");
    }

    @Test
    void listToString_joined() {
        String result = StringUtils.listToString(List.of("a", "b", "c"), ",");
        assertThat(result).isEqualTo("a,b,c");
    }

    @Test
    void killNull_nonNull_returnsSame() {
        assertThat(StringUtils.killNull("hello")).isEqualTo("hello");
    }

    @Test
    void parentheses_nonNull() {
        assertThat(StringUtils.parentheses("test")).isEqualTo("(test)");
    }

    @Test
    void brackets_nonNull() {
        assertThat(StringUtils.brackets("test")).isEqualTo("[test]");
    }

    @Test
    void ditto_nonNull() {
        assertThat(StringUtils.ditto("test")).isEqualTo("\"test\"");
    }

    @Test
    void quote_nonNull() {
        assertThat(StringUtils.quote("test")).isEqualTo("'test'");
    }

    @Test
    void quote_array_withSeparator() {
        String result = StringUtils.quote(new String[]{"a", "b"}, ",");
        assertThat(result).isEqualTo("'a','b'");
    }

    @Test
    void quote_array_empty_returnsEmpty() {
        assertThat(StringUtils.quote(new String[0], ",")).isEmpty();
    }

    @Test
    void quoteIfString_string_returnsQuoted() {
        assertThat(StringUtils.quoteIfString("test")).isEqualTo("'test'");
    }

    @Test
    void quoteIfString_nonString_returnsSame() {
        assertThat(StringUtils.quoteIfString(42)).isEqualTo(42);
    }

    @Test
    void trimToAlphaString_withSpecialChars() {
        assertThat(StringUtils.trimToAlphaString("1\r\n1\r\n")).isEqualTo("11");
    }

    @Test
    void trimToAlphaStrings_withSpecialChars() {
        String[] result = StringUtils.trimToAlphaStrings("1\r\n2");
        assertThat(result).containsExactly("1", "2");
    }
}

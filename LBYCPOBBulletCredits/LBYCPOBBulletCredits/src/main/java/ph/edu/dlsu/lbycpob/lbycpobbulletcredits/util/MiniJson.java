package ph.edu.dlsu.lbycpob.lbycpobbulletcredits.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MiniJson.java
 * =============
 * A small, self-contained JSON parser - just enough of the JSON spec to
 * read plain configuration data like waves.json (objects, arrays,
 * strings, numbers, booleans, null). Deliberately NOT a full-spec,
 * production-grade JSON library.
 * <p>
 * NOTE - why hand-roll this instead of using a library?
 * Pulling in a JSON library (like Jackson or Gson) would mean adding a
 * new Maven dependency, which needs to be downloaded and version-resolved
 * against everything else in the project. For a single, simple config
 * file like waves.json, a ~150-line parser we fully understand and
 * control is a reasonable trade-off against taking on an extra external
 * dependency just for this one use.
 *
 * USAGE:
 *   Object root = MiniJson.parse(jsonText);
 * The returned Object is one of:
 *   - Map&lt;String, Object&gt;  (a JSON object   "{...}")
 *   - List&lt;Object&gt;         (a JSON array    "[...]")
 *   - String                 (a JSON string   "\"...\"")
 *   - Double                 (a JSON number   "123.45")
 *   - Boolean                (true / false)
 *   - null                   (JSON null)
 * Callers (see WaveDataLoader) are expected to know their own schema and
 * cast accordingly, the same way you would when using a generic JSON
 * library without a schema/binding layer.
 */
public final class MiniJson {

    private final String text;
    private int pos;

    private MiniJson(String text) {
        this.text = text;
        this.pos = 0;
    }

    /** Parses a complete JSON document and returns its root value. */
    public static Object parse(String json) {
        MiniJson parser = new MiniJson(json);
        parser.skipWhitespace();
        Object result = parser.parseValue();
        parser.skipWhitespace();
        return result;
    }

    private Object parseValue() {
        skipWhitespace();
        char c = peek();
        return switch (c) {
            case '{' -> parseObject();
            case '[' -> parseArray();
            case '"' -> parseString();
            case 't', 'f' -> parseBoolean();
            case 'n' -> parseNull();
            default -> parseNumber();
        };
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        expect('{');
        skipWhitespace();
        if (peek() == '}') {
            pos++;
            return map;
        }
        while (true) {
            skipWhitespace();
            String key = parseString();
            skipWhitespace();
            expect(':');
            Object value = parseValue();
            map.put(key, value);
            skipWhitespace();
            char c = next();
            if (c == '}') {
                break;
            }
            if (c != ',') {
                throw new IllegalArgumentException("Expected ',' or '}' at position " + (pos - 1));
            }
        }
        return map;
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        expect('[');
        skipWhitespace();
        if (peek() == ']') {
            pos++;
            return list;
        }
        while (true) {
            Object value = parseValue();
            list.add(value);
            skipWhitespace();
            char c = next();
            if (c == ']') {
                break;
            }
            if (c != ',') {
                throw new IllegalArgumentException("Expected ',' or ']' at position " + (pos - 1));
            }
        }
        return list;
    }

    private String parseString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = next();
            if (c == '"') {
                break;
            }
            if (c == '\\') {
                char escaped = next();
                switch (escaped) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'u' -> {
                        String hex = text.substring(pos, pos + 4);
                        pos += 4;
                        sb.append((char) Integer.parseInt(hex, 16));
                    }
                    default -> throw new IllegalArgumentException("Unknown escape sequence: \\" + escaped);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Double parseNumber() {
        int start = pos;
        if (peek() == '-') {
            pos++;
        }
        while (pos < text.length()) {
            char c = text.charAt(pos);
            boolean isNumberChar = Character.isDigit(c) || c == '.' || c == 'e' || c == 'E'
                    || ((c == '+' || c == '-') && pos > start);
            if (!isNumberChar) {
                break;
            }
            pos++;
        }
        return Double.parseDouble(text.substring(start, pos));
    }

    private Boolean parseBoolean() {
        if (text.startsWith("true", pos)) {
            pos += 4;
            return Boolean.TRUE;
        }
        if (text.startsWith("false", pos)) {
            pos += 5;
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Invalid literal at position " + pos);
    }

    private Object parseNull() {
        if (text.startsWith("null", pos)) {
            pos += 4;
            return null;
        }
        throw new IllegalArgumentException("Invalid literal at position " + pos);
    }

    private void skipWhitespace() {
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
    }

    private char peek() {
        if (pos >= text.length()) {
            throw new IllegalArgumentException("Unexpected end of JSON input");
        }
        return text.charAt(pos);
    }

    private char next() {
        char c = peek();
        pos++;
        return c;
    }

    private void expect(char expected) {
        char c = next();
        if (c != expected) {
            throw new IllegalArgumentException("Expected '" + expected + "' but found '" + c + "' at position " + (pos - 1));
        }
    }
}

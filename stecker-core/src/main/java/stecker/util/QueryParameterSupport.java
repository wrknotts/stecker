package stecker.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryParameterSupport {

    public static final Pattern HL_ARRAY_PARAM_PATTERN = Pattern.compile("hl\\[([0-9]+)\\]");
    public static final Pattern HL_PARAM_PATTERN = Pattern.compile("hl");
    public static Pattern LINE_NUMBER_PATTERN = Pattern.compile("[0-9]+(?:[-]?[0-9]+)*");

    private static Logger LOGGER = LoggerFactory.getLogger(QueryParameterSupport.class);

    public static Map<Integer, List<String>> extractAndNormalizeParameters(
            Map<String, String> params) {

        if (null == params) {
            return Collections.<Integer, List<String>> emptyMap();
        }

        LOGGER.debug("extracting and normalizing parameters. Value: '{}'", params);

        Map<Integer, List<String>> result = new HashMap<Integer, List<String>>();
        Set<String> keys = params.keySet();

        for (String k : keys) {

            try {
                // handle case where brackets ('[' or ']') were encoded in URL
                // by requester
                String dk = URLDecoder.decode(k, StandardCharsets.UTF_8.name());

                Matcher arrayMatcher = HL_ARRAY_PARAM_PATTERN.matcher(dk);
                Matcher singleMatcher = HL_PARAM_PATTERN.matcher(dk);

                // handle when request contains a query parameter that
                // represents
                // one element in an array of highlighting declarations
                // hl[n]=n,n
                if (arrayMatcher.matches()) {
                    List<String> lineNbrs = Arrays.asList(params.get(k).split(","));
                    result.put(Integer.parseInt(arrayMatcher.group(1)),
                            normalizeLineNumbers(lineNbrs));
                }
                // handle when request contains a query parameter that
                // represents a
                // single highlighting declaration
                // hl=n,n
                else if (singleMatcher.matches()) {
                    List<String> lineNbrs = Arrays.asList(params.get(k).split(","));
                    result.put(0, normalizeLineNumbers(lineNbrs));
                }

            }
            catch (UnsupportedEncodingException e) {
                LOGGER.error("An error occurred while trying to decode parameter key. Key: '{}'",
                        k, e);
            }
        }

        LOGGER.debug(
                "normalized parameters (each index entry represents a single 'hl=' parameter). Value: '{}'",
                result);

        return result;
    }

    public static List<String> normalizeLineNumbers(List<String> lineNumbers) {

        if (null == lineNumbers) {
            return Collections.<String> emptyList();
        }

        LOGGER.debug("normalizing line numbers. Value: '{}'", lineNumbers);

        Set<String> nl = new TreeSet<String>();

        for (String s : lineNumbers) {
            String candidate = s.trim();
            Matcher matcher = LINE_NUMBER_PATTERN.matcher(candidate);

            if (matcher.matches()) {

                String[] parts = candidate.split("-");

                switch (parts.length) {

                case 1:
                    nl.add(parts[0].trim());
                    break;
                case 2:
                    // ensure first is less than second
                    int lower = Integer.parseInt(parts[0].trim());
                    int upper = Integer.parseInt(parts[1].trim());

                    if (lower == upper) {
                        nl.add(parts[0].trim());
                        break;
                    }

                    if (lower < upper) {
                        while (lower <= upper) {
                            nl.add(String.valueOf(lower));
                            lower++;
                        }
                    }
                default:
                    break;
                }

            }
        }

        LOGGER.debug("normalized line numbers. Value: '{}'", nl);

        return new ArrayList<String>(nl);
    }

}

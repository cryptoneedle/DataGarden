package com.cryptoneedle.garden.common.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * <p>description:  </p>
 *
 * @author CryptoNeedle
 * @date 2025-03-28
 */
public enum DorisDataType {

    /**
     * 数值类型
     */
    BOOLEAN,
    TINYINT,
    SMALLINT,
    INT,
    BIGINT,
    LARGEINT,
    FLOAT,
    DOUBLE,
    DECIMAL("precision", "scale"),

    /**
     * 日期和时间类型
     */
    DATE,
    DATETIME("scale"),

    /**
     * 字符串类型
     */
    CHAR("length"),
    VARCHAR("length"),
    STRING,

    /**
     * 半结构类型
     */
    ARRAY("item_type"),
    MAP("key_type", "value_type"),
    STRUCT("field_types"), // 仅支持在 Duplicate 模型的表中使用
    JSON,
    VARIANT,

    /**
     * 聚合类型
     */
    HLL,
    BITMAP,
    QUANTILE_STATE,
    AGG_STATE,

    /**
     * IP类型
     */
    IPV4,
    IPV6,

    /**
     * 不支持类型
     */
    UNSUPPORTED;

    private final String[] parameters;

    DorisDataType(String... parameters) {
        this.parameters = parameters;
    }

    /**
     * 获取此类型所需的参数名列表
     *
     * @return 参数名数组
     */
    public String[] getParameters() {
        return parameters;
    }

    /**
     * 判断此类型是否需要参数
     *
     * @return 是否需要参数
     */
    public boolean requiresParameters() {
        return parameters != null && parameters.length > 0;
    }

    /**
     * 根据提供的参数构建完整的类型信息
     *
     * @param params 参数值，应与所需参数数量匹配
     * @return 完整的类型信息字符串
     * @throws IllegalArgumentException 如果参数数量不匹配
     */
    public String buildTypeString(Object... params) {
        if (requiresParameters()) {
            if (params.length != parameters.length) {
                throw new IllegalArgumentException(String.format(
                        "Type %s requires %d parameters (%s), but got %d",
                        name(),
                        parameters.length,
                        String.join(", ", parameters),
                        params.length
                ));
            }

            StringBuilder sb = new StringBuilder(name());
            sb.append("(");

            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(params[i]);
            }

            sb.append(")");
            return sb.toString();
        } else {
            if (params.length > 0) {
                throw new IllegalArgumentException(String.format(
                        "Type %s does not require parameters, but got %d",
                        name(),
                        params.length
                ));
            }
            return name();
        }
    }

    /**
     * 根据提供的命名参数构建完整的类型信息
     *
     * @param paramMap 参数名称和值的映射
     * @return 完整的类型信息字符串
     * @throws IllegalArgumentException 如果缺少必需参数或提供了未知参数
     */
    public String buildTypeStringWithNamedParams(Map<String, Object> paramMap) {
        if (requiresParameters()) {
            // 检查是否所有必需参数都提供了
            for (String param : parameters) {
                if (!paramMap.containsKey(param)) {
                    throw new IllegalArgumentException(String.format(
                            "Missing required parameter '%s' for type %s",
                            param, name()
                    ));
                }
            }

            // 检查是否有未知参数
            for (String providedParam : paramMap.keySet()) {
                if (!Arrays.asList(parameters).contains(providedParam)) {
                    throw new IllegalArgumentException(String.format(
                            "Unknown parameter '%s' for type %s",
                            providedParam, name()
                    ));
                }
            }

            StringBuilder sb = new StringBuilder(name());
            sb.append("(");

            boolean first = true;
            for (String param : parameters) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(paramMap.get(param));
                first = false;
            }

            sb.append(")");
            return sb.toString();
        } else {
            if (!paramMap.isEmpty()) {
                throw new IllegalArgumentException(String.format(
                        "Type %s does not require parameters, but got: %s",
                        name(),
                        paramMap.keySet().stream().collect(Collectors.joining(", "))
                ));
            }
            return name();
        }
    }

    /**
     * 为DECIMAL类型创建类型字符串
     *
     * @param precision 精度
     * @param scale     小数位数
     * @return 完整的类型信息字符串
     */
    public static String createDecimal(int precision, int scale) {
        return DECIMAL.buildTypeString(precision, scale);
    }

    /**
     * 为DATETIME类型创建类型字符串
     *
     * @param scale 小数位数精度
     * @return 完整的类型信息字符串
     */
    public static String createDatetime(int scale) {
        return DATETIME.buildTypeString(scale);
    }

    /**
     * 为CHAR类型创建类型字符串
     *
     * @param length 长度
     * @return 完整的类型信息字符串
     */
    public static String createChar(int length) {
        return CHAR.buildTypeString(length);
    }

    /**
     * 为VARCHAR类型创建类型字符串
     *
     * @param length 长度
     * @return 完整的类型信息字符串
     */
    public static String createVarchar(int length) {
        return VARCHAR.buildTypeString(length);
    }

    /**
     * 为ARRAY类型创建类型字符串
     *
     * @param itemType 数组元素类型
     * @return 完整的类型信息字符串
     */
    public static String createArray(String itemType) {
        return ARRAY.buildTypeString(itemType);
    }

    /**
     * 为MAP类型创建类型字符串
     *
     * @param keyType   键类型
     * @param valueType 值类型
     * @return 完整的类型信息字符串
     */
    public static String createMap(String keyType, String valueType) {
        return MAP.buildTypeString(keyType, valueType);
    }

    /**
     * 为STRUCT类型创建类型字符串
     *
     * @param fieldTypes 列类型列表，格式为 "name1:type1,name2:type2,..."
     * @return 完整的类型信息字符串
     */
    public static String createStruct(String fieldTypes) {
        return STRUCT.buildTypeString(fieldTypes);
    }

    /**
     * 为STRUCT类型创建类型字符串
     *
     * @param fields 列名称和类型的映射
     * @return 完整的类型信息字符串
     */
    public static String createStruct(Map<String, String> fields) {
        StringJoiner joiner = new StringJoiner(",");
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            joiner.add(entry.getKey() + ":" + entry.getValue());
        }
        return STRUCT.buildTypeString(joiner.toString());
    }

    /**
     * 使用示例
     */
    public static void main(String[] args) {
        System.out.println("==== 数值类型 ====");
        System.out.println(DorisDataType.BOOLEAN.buildTypeString());  // BOOLEAN
        System.out.println(DorisDataType.TINYINT.buildTypeString());  // TINYINT
        System.out.println(DorisDataType.SMALLINT.buildTypeString());  // SMALLINT
        System.out.println(DorisDataType.INT.buildTypeString());  // INT
        System.out.println(DorisDataType.BIGINT.buildTypeString());  // BIGINT
        System.out.println(DorisDataType.LARGEINT.buildTypeString());  // LARGEINT
        System.out.println(DorisDataType.FLOAT.buildTypeString());  // FLOAT
        System.out.println(DorisDataType.DOUBLE.buildTypeString());  // DOUBLE
        System.out.println(DorisDataType.DECIMAL.buildTypeString(10, 2));  // DECIMAL(10, 2)

        System.out.println("==== 日期和时间类型 ====");
        System.out.println(DorisDataType.DATE.buildTypeString());  // DATE
        System.out.println(DorisDataType.DATETIME.buildTypeString(0));  // DATETIME(0)
        System.out.println(DorisDataType.DATETIME.buildTypeString(3));  // DATETIME(3)
        System.out.println(DorisDataType.createDatetime(6));  // DATETIME(6)

        System.out.println("==== 字符串类型 ====");
        System.out.println(DorisDataType.CHAR.buildTypeString(10));  // CHAR(10)
        System.out.println(DorisDataType.VARCHAR.buildTypeString(100));  // VARCHAR(100)
        System.out.println(DorisDataType.STRING.buildTypeString());  // STRING

        System.out.println("==== 半结构类型 ====");
        System.out.println(DorisDataType.ARRAY.buildTypeString("INT"));  // ARRAY(INT)
        System.out.println(DorisDataType.createArray(DorisDataType.DECIMAL.buildTypeString(10, 2)));  // ARRAY(DECIMAL(10, 2))
        System.out.println(DorisDataType.MAP.buildTypeString("STRING", "INT"));  // MAP(STRING, INT)
        System.out.println(DorisDataType.createMap(DorisDataType.VARCHAR.buildTypeString(20), DorisDataType.DOUBLE.name()));  // MAP(VARCHAR(20), DOUBLE)

        // 结构体类型
        Map<String, String> structFields = new HashMap<>();
        structFields.put("id", DorisDataType.BIGINT.name());
        structFields.put("name", DorisDataType.VARCHAR.buildTypeString(100));
        structFields.put("score", DorisDataType.DECIMAL.buildTypeString(5, 2));
        System.out.println(DorisDataType.createStruct(structFields));  // STRUCT(id:BIGINT,name:VARCHAR(100),score:DECIMAL(5, 2))

        System.out.println(DorisDataType.JSON.buildTypeString());  // JSON
        System.out.println(DorisDataType.VARIANT.buildTypeString());  // VARIANT

        System.out.println("==== 聚合类型 ====");
        System.out.println(DorisDataType.HLL.buildTypeString());  // HLL
        System.out.println(DorisDataType.BITMAP.buildTypeString());  // BITMAP
        System.out.println(DorisDataType.QUANTILE_STATE.buildTypeString());  // QUANTILE_STATE
        System.out.println(DorisDataType.AGG_STATE.buildTypeString());  // AGG_STATE

        System.out.println("==== IP类型 ====");
        System.out.println(DorisDataType.IPV4.buildTypeString());  // IPV4
        System.out.println(DorisDataType.IPV6.buildTypeString());  // IPV6

        System.out.println("==== 命名参数示例 ====");
        // 使用命名参数
        Map<String, Object> decimalParams = new HashMap<>();
        decimalParams.put("precision", 18);
        decimalParams.put("scale", 4);
        System.out.println(DorisDataType.DECIMAL.buildTypeStringWithNamedParams(decimalParams));  // DECIMAL(18, 4)

        Map<String, Object> charParams = new HashMap<>();
        charParams.put("length", 50);
        System.out.println(DorisDataType.CHAR.buildTypeStringWithNamedParams(charParams));  // CHAR(50)

        System.out.println("==== 嵌套复杂类型示例 ====");
        // 嵌套数组
        String nestedArray = DorisDataType.createArray(
                DorisDataType.createArray(DorisDataType.INT.name())
        );
        System.out.println(nestedArray);  // ARRAY(ARRAY(INT))

        // 带Map的结构体
        Map<String, String> complexStructFields = new HashMap<>();
        complexStructFields.put("id", DorisDataType.BIGINT.name());
        complexStructFields.put("tags", DorisDataType.createMap("STRING", "STRING"));
        complexStructFields.put("metrics", DorisDataType.createArray(DorisDataType.DOUBLE.name()));
        System.out.println(DorisDataType.createStruct(complexStructFields));  // STRUCT(id:BIGINT,tags:MAP(STRING, STRING),metrics:ARRAY(DOUBLE))
    }
}
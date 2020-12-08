package com.xiuchezai.search.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author hoo
 */
public class PropertiesUtil {
    /**
     * 加载配置文件不含扩展名properties
     *
     * @param proFileName
     * @return
     */
    public static ResourceBundle loadResourceBundle(String proFileName) {
        return loadResourceBundle(proFileName, null);
    }

    public static ResourceBundle loadResourceBundle(String proFileName, Locale locale) {
        if (proFileName == null || "".equals(proFileName)) {
            return null;
        }
        ResourceBundle bundle = null;
        try {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            bundle = ResourceBundle.getBundle(proFileName, locale);
        } catch (MissingResourceException e) {
        }

        return bundle;
    }

    public static void clearCache() {
        ResourceBundle.clearCache();
    }
}

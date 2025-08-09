/*
 * @ (#) SlugUtils.java 1.1 8/9/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.benhvien1a.util;

import java.util.function.Predicate;

/**
 * @description Utility class for generating SEO-friendly slugs and ensuring uniqueness.
 * @author :
 *     Nguyen Truong An (updated by ChatGPT)
 * @date : 7/15/2025 - updated 8/9/2025
 * @version 1.1
 */
public class SlugUtils {

    /**
     * Remove Vietnamese diacritics and convert to ASCII characters.
     */
    public static String removeVietnameseDiacritics(String str) {
        str = str.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        str = str.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        str = str.replaceAll("[ìíịỉĩ]", "i");
        str = str.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        str = str.replaceAll("[ùúụủũưừứựửữ]", "u");
        str = str.replaceAll("[ỳýỵỷỹ]", "y");
        str = str.replaceAll("đ", "d");

        str = str.replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A");
        str = str.replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E");
        str = str.replaceAll("[ÌÍỊỈĨ]", "I");
        str = str.replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O");
        str = str.replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U");
        str = str.replaceAll("[ỲÝỴỶỸ]", "Y");
        str = str.replaceAll("Đ", "D");

        return str;
    }

    /**
     * Generate a basic slug from the given input.
     */
    public static String generateSlug(String input) {
        String normalized = removeVietnameseDiacritics(input);
        return normalized.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Generate a unique slug by appending a counter if the slug already exists.
     *
     * @param baseName   Original name or title to generate slug from.
     * @param existsFunc Function to check if a slug already exists (e.g. repository::existsBySlug).
     * @return Unique slug string.
     */
    public static String generateUniqueSlug(String baseName, Predicate<String> existsFunc) {
        String baseSlug = generateSlug(baseName);
        String slug = baseSlug;
        int counter = 1;

        while (existsFunc.test(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }
}

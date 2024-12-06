package io.yody.yosurvey.survey.service.helpers;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public class HashGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int HASH_LENGTH = 10;

    public static String generateUniqueHash() {
        // Generate a random alphanumeric string of length 10
        return RandomStringUtils.random(HASH_LENGTH, 0, 0, true, true, null, secureRandom);
    }
}


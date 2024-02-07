package org.baggle.domain.meeting.service;

import java.util.Random;

public class RandomNumberGenerator {
    public static int createRandomNumber(int participationSize) {
        return new Random().nextInt(participationSize);
    }
}

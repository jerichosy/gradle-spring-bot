package com.segs.declarativebot.test.service;

import com.segs.declarativebot.facade.ReminderService;
import com.segs.declarativebot.repositories.ReminderRepository;
import com.segs.declarativebot.test.base.AbstractIntegrationTestBase;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReminderServiceIntegrationTest extends AbstractIntegrationTestBase {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private ReminderRepository reminderRepository;

    @Before
    public void setup() {
    }

    @Test
    public void remindUsers_withPastAndFutureReminders() {
        // given

        // when

        // then
    }

    @Test
    public void addReminder_withValidInput() {
        // given

        // when

        // then
    }

    @Test
    public void addReminder_withAllNullInputs() {
        // given

        // when

        // then
    }

    @Test
    public void addReminder_withNullMessage() {
        // given

        // when

        // then
    }

    @Test
    public void addReminder_withNegativeTimeValue() {
        // given

        // when

        // then
    }

    @Test
    public void addReminder_withDuplicateReminder() {
        // given

        // when

        // then
    }

}

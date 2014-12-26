package com.devoler.aicup.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.devoler.aicup.test.model.BattlefieldTest;
import com.devoler.aicup.test.model.UnitTest;
import com.devoler.aicup.test.model.util.ImmutableRectangleTest;
import com.devoler.aicup.test.model.util.ImmutableSetTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ UnitTest.class, BattlefieldTest.class, ImmutableRectangleTest.class, ImmutableSetTest.class })
public class AllTests {
}

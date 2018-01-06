package com.k2.FilesystemEntityManager;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	FemConfigTests.class,
	FemSingleActionTests.class,
	FemMultipleActionTests.class,
	FemMultiThreadedTests.class
})
public class FilesystemEntityManagerTestSuite {

}
